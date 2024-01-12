package handler;

import lombok.Getter;
import lombok.Setter;
import model.EnglishWord;
import model.GameResults;
import model.Participant;
import model.RussianWord;
import org.w3c.dom.ls.LSOutput;
import protocol.Protocol;
import service.EnglishWordService;
import service.GameResultService;
import service.ParticipantService;
import service.RussianWordService;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

//В контексте многопоточного программирования в Java, volatile имеет два основных свойства:
// 1) Операции чтения/записи volatile переменной являются атомарными.
// 2) Результат операции записи значения в volatile переменную одним потоком, становится виден всем другим потокам, которые используют эту переменную для чтения из нее значения
@Setter@Getter
public class MessageHandler implements Runnable {
    private Socket clientSocket;
    private Protocol protocol;
    private ParticipantService participantService;
    private EnglishWordService englishWordService;
    private RussianWordService russianWordService;
    private GameResultService gameResultService;
    private Participant participant;
    private boolean isAuthenticated = false;
    private volatile boolean running = true;

    public MessageHandler(Socket clientSocket, Protocol protocol) {
        this.clientSocket = clientSocket;
        this.protocol = protocol;
        this.participantService = new ParticipantService();
        this.englishWordService = new EnglishWordService();
        this.russianWordService = new RussianWordService();
        this.gameResultService = new GameResultService();
    }

    @Override
    public void run() {
        while (running) {
            try {
                handleMessages();
            } catch (SocketException e) {
                System.out.println("User disconnected");
                running = false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleMessages() throws IOException {
        System.out.println("handle server");
        InputStream is = clientSocket.getInputStream();
        int data = is.read();
        StringBuilder sb = new StringBuilder();
        if (isAuthenticated) {
            sendResponse(protocol.createCommand(4));
        }
        while(data != -1) {
            sb.append((char) data);
            if ((char) data == '\n') {
                String line = sb.toString();
                byte[] command = line.getBytes(StandardCharsets.UTF_8);
                Map<Integer, List<String>> parsedCommand = protocol.parseCommand(command);
                int commandType = parsedCommand.keySet().iterator().next();
                switch (commandType) {
                    case Protocol.REGISTER:
                        handleRegister(parsedCommand.get(commandType));
                        isAuthenticated = true;
                        break;
                    case Protocol.CLOSE_GAME:
                        closeGame(parsedCommand.get(commandType));
                        break;
                    case Protocol.LOGIN:
                        isAuthenticated = handleLogin(parsedCommand.get(commandType));
                        break;
                    case Protocol.START_GAME:
                        if (isAuthenticated) {
                            handleStartGame(parsedCommand.get(commandType));
                        }
                        break;
                    case Protocol.SUBMIT_RESULT:
                        if (isAuthenticated) {
                            handleSubmitResult(parsedCommand.get(commandType));
                        }
                        break;
                    case Protocol.REQUEST_RANKING:
                        if (isAuthenticated) {
                            handleRequestRanking(parsedCommand.get(commandType));
                        }
                        break;
                    case Protocol.NEW_DATA:
                        if (isAuthenticated) {
                            handleNewData(parsedCommand.get(commandType));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown command type: " + commandType);
                }
                sb = new StringBuilder();
            }
            data = is.read();
        }
    }



    private void handleRegister(List<String> args) {
        Participant participant = new Participant();
        participant.setUsername(args.get(0));
        participant.setEmail(args.get(1));
        participant.setPassword(args.get(2));
        try {
            if (participantService.findByUserName(participant.getUsername()) != null ||
                    participantService.findByEmail(participant.getEmail()) != null) {
                byte[] message = protocol.createCommand(Protocol.ERR_REG);
                sendResponse(message);
            } else if (participantService.saveParticipant(participant)) {
                byte[] message = protocol.createCommand(Protocol.OK_REG);
                sendResponse(message);
            } else {
                byte[] message = protocol.createCommand(Protocol.ERR_REG);
                sendResponse(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean handleLogin(List<String> args) {
        String username = args.get(0);
        String password = args.get(1);
        Optional<Participant> optionalParticipant = participantService.findByUserName(username);
        if (optionalParticipant.isPresent()) {
            participant = optionalParticipant.get();
            if (participant.getPassword().equals(password)) {
                try {
                    byte[] message = protocol.createCommand(Protocol.OK_LOG);
                    sendResponse(message);
                    setParticipant(participant);
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    byte[] message = protocol.createCommand(Protocol.ERR_LOG);
                    sendResponse(message);
                    return false;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            try {
                byte[] message = protocol.createCommand(Protocol.ERR_LOG);
                sendResponse(message);
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleStartGame(List<String> args) {
        String language = args.get(0);
        try {
            if (language.equals("english")) {
                int countRows = englishWordService.countWords();
                sendResponse(protocol.createCommand(7, getWords(countRows,language)));
            } else if (language.equals("russian")) {
                int countRows = russianWordService.countWords();
                sendResponse(protocol.createCommand(7, getWords(countRows,language)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSubmitResult(List<String> args) {
        String correctness = args.get(0);
        String speed = args.get(1);

        Optional<GameResults> oldGameResults = gameResultService.findByParticipantId(participant.getParticipantId());

        if (oldGameResults.isPresent()) {
            String oldSpeed = String.valueOf(oldGameResults.get().getSpeed());
            String oldCorrectness = String.valueOf(oldGameResults.get().getCorrectnessPercentage());
            try {
                sendResponse(protocol.createCommand(9, oldSpeed, oldCorrectness, speed, correctness));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            GameResults gameResults = GameResults.builder()
                    .participantId(participant.getParticipantId())
                    .correctnessPercentage(Integer.parseInt(correctness))
                    .speed(Integer.parseInt(speed))
                    .username(participant.getUsername())
                    .build();

            if (gameResultService.save(gameResults)) {
                try {
                    sendResponse(protocol.createCommand(9));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleNewData(List<String> args) {
        String speed = args.get(0);
        String correctness = args.get(1);

        GameResults gameResults = GameResults.builder()
                .participantId(participant.getParticipantId())
                .correctnessPercentage(Integer.parseInt(correctness))
                .speed(Integer.parseInt(speed))
                .username(participant.getUsername())
                .gameDate(Timestamp.from(Instant.now()))
                .build();
        if (gameResultService.update(gameResults)) {
            try {
                sendResponse(protocol.createCommand(9));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleRequestRanking(List<String> args) {
        List<GameResults> gameResultsList = gameResultService.getAll();

        if (!gameResultsList.isEmpty()) {
            gameResultsList.sort(Comparator.comparing(GameResults::getSpeed)
                    .thenComparing(GameResults::getCorrectnessPercentage));

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < gameResultsList.size(); i++) {
                GameResults gameResult = gameResultsList.get(i);
                sb.append(gameResult.getUsername() + " speed: " + gameResult.getSpeed() + " correctness: "
                        + gameResult.getCorrectnessPercentage() + "|");
            }
            try {
                sendResponse(protocol.createCommand(11, String.valueOf(sb)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                sendResponse(protocol.createCommand(11, "Рейтинг на данный момент пуст"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    private void closeGame(List<String> args) {
        try {
            clientSocket.close();
            running = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getWords(int countRows, String language) {
        StringBuilder builder = new StringBuilder();
        Random rand = new Random();
        int countWords = rand.nextInt((35 - 25) + 1) + 25;

        for (int i = 0; i < countWords; i++) {
            if (language.equals("english")) {
                int randomId = rand.nextInt((countRows - 1) + 1) + 1;
                Optional<EnglishWord> optionalEnglishWord = englishWordService.getWordById(randomId);
                if (optionalEnglishWord.isPresent()) {
                    builder.append(optionalEnglishWord.get().getWord());
                    builder.append(" ");
                } else {
                    throw new IllegalArgumentException("Unknown en word with id = " + randomId);
                }
            } else {
                int randomId = rand.nextInt((countRows - 1) + 1) + 1;
                Optional<RussianWord> optionalRussianWord = russianWordService.getWordById(randomId);
                if (optionalRussianWord.isPresent()) {
                    builder.append(optionalRussianWord.get().getWord());
                    builder.append(" ");
                } else {
                    throw new IllegalArgumentException("Unknown ru word with id = " + randomId);
                }
            }
        }
        return builder.toString();
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    private void sendResponse(byte[] message) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(message);
        outputStream.flush();
    }

}
