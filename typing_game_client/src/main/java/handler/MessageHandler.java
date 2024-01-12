package handler;

import app_gui.UserAppGUI;
import protocol.Protocol;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MessageHandler {
    private Socket socket;
    private Protocol protocol;
    private UserAppGUI gui;

    public MessageHandler(Socket socket, Protocol protocol, UserAppGUI gui) {
        this.socket = socket;
        this.protocol = protocol;
        this.gui = gui;
    }
    public void handleMessages() {
        try {
            InputStream is = socket.getInputStream();
            int data = is.read();
            StringBuilder sb = new StringBuilder();
            while(data != -1) {
                sb.append((char) data);
                if ((char) data == '\n') {
                    String line = sb.toString();
                    byte[] command = line.getBytes(StandardCharsets.UTF_8);
                    Map<Integer, List<String>> parsedCommand = protocol.parseCommand(command);
                    int commandType = parsedCommand.keySet().iterator().next();
                    switch (commandType) {
                        case Protocol.OK_REG:
                            handleOkReg(parsedCommand.get(commandType));
                            break;
                        case Protocol.ERR_REG:
                            handleErrReg(parsedCommand.get(commandType));
                            break;
                        case Protocol.OK_LOG:
                            handleOkLog(parsedCommand.get(commandType));
                            break;
                        case Protocol.ERR_LOG:
                            handleErrLog(parsedCommand.get(commandType));
                            break;
                        case Protocol.WORDS:
                            handleWords(parsedCommand.get(commandType));
                            break;
                        case Protocol.OK_RES:
                            handleOkRes(parsedCommand.get(commandType));
                            break;
                        case Protocol.RATING:
                            handleRating(parsedCommand.get(commandType));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown command type: " + commandType);
                    }
                    sb = new StringBuilder();
                }
                data = is.read();
            }
        } catch (SocketException e) {
            SwingUtilities.invokeLater(() -> {
                gui.getConnectionErrorLabel().setText("Сервер отсоединился, попробуйте снова");
                gui.getCardLayout().show(gui.getFrame().getContentPane(), "ServerConnectionPanel");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOkReg(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.getRegisterErrorLabel().setText(null);
            gui.getCardLayout().show(gui.getFrame().getContentPane(), "GameChoicePanel");
        });
    }

    private void handleErrReg(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.getRegisterErrorLabel().setText("Ошибка регистрации, такая почта или username уже существуют");
        });
    }

    private void handleOkLog(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.getLoginErrorLabel().setText(null);
            gui.getCardLayout().show(gui.getFrame().getContentPane(), "GameChoicePanel");
        });
    }

    private void handleErrLog(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.getLoginErrorLabel().setText("Пароль или логин введены не правильно");
        });
    }

    private void handleWords(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.getCardLayout().show(gui.getFrame().getContentPane(), "GamePanel");

            gui.getGameTextArea().setText(args.get(0));
        });
    }

    private void handleOkRes(List<String> args) {
        if (args.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                gui.getCardLayout().show(gui.getFrame().getContentPane(), "ChooseRankingPanel");
            });
        } else {
            String oldSpeed = args.get(0);
            String oldCorrectness = args.get(1);
            String currentSpeed = args.get(2);
            String currentCorrectness = args.get(3);

            SwingUtilities.invokeLater(() -> {
                gui.getCardLayout().show(gui.getFrame().getContentPane(), "SaveNewOrOldResultsPanel");

                gui.getPreviousSpeedField().setText("Speed: " + oldSpeed + " символов в минуту");
                gui.getPreviousCorrectnessField().setText("Correctness: " + oldCorrectness + "%");
                gui.getCurrentSpeedField().setText("Speed: " + currentSpeed + " символов в минуту");
                gui.getCurrentCorrectnessField().setText("Correctness: " + currentCorrectness + "%" );
            });
        }
    }


    private void handleRating(List<String> args) {
        SwingUtilities.invokeLater(() -> {
            gui.setRankingData(args.get(0));
            gui.updateRanking();

            gui.getCardLayout().show(gui.getFrame().getContentPane(), "RankingPanel");
        });
    }

}
