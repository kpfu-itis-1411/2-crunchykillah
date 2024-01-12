package app_gui;

import client.Client;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import lombok.Getter;
import lombok.Setter;
import servce.TextAnalyzer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.net.URL;

@Getter@Setter
public class UserAppGUI {

    private JFrame frame;
    private CardLayout cardLayout;
    private Client client;
    private JLabel loginErrorLabel;
    private JLabel registerErrorLabel;
    private JTextArea gameTextArea;
    private JLabel connectionErrorLabel;
    private int timeLeft = 30;
    private JLabel timerLabel;
    private boolean firstInput = true;
    private JLabel speedLabel;
    private JLabel correctnessLabel;
    private JTextArea userInputField;
    private JLabel previousSpeedField;
    private JLabel previousCorrectnessField;
    private JLabel currentSpeedField;
    private JLabel currentCorrectnessField;
    private JLabel message;
    private Timer timer;
    private String rankingData;
    private JTextArea rankingArea;

    public UserAppGUI() {
        FlatMacDarkLaf.setup();
        frame = new JFrame("CRUNCHY TYPE GAME");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 480);
        cardLayout = new CardLayout();
        frame.setLayout(cardLayout);

        frame.add(createServerConnectionPanel(), "ServerConnectionPanel");
        frame.add(createLoginPage(), "LoginPanel");
        frame.add(createRegisterPage(), "RegisterPanel");
        frame.add(createGameChoicePage(), "GameChoicePanel");
        frame.add(createGamePage(), "GamePanel");
        frame.add(createResultPage(), "ResultPanel");
        frame.add(createChooseRankingPage(), "ChooseRankingPanel");
        frame.add(createSaveNewOrOldResults(), "SaveNewOrOldResultsPanel");
        frame.add(createRankingPage(), "RankingPanel");

        frame.setVisible(true);
    }

    private JPanel createServerConnectionPanel() {
        JPanel serverConnectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(250, 250));
        try {
            URL url = getClass().getResource("/main.png");
            ImageIcon icon = new ImageIcon(url);
            JLabel imageLabel = new JLabel(icon);
            imagePanel.add(imageLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.insets = new Insets(0, 0, 0, 100); //правый отступ
        serverConnectionPanel.add(imagePanel, gbc);

        JLabel instructionLabel = new JLabel("Введите IP-адрес сервера", SwingConstants.CENTER);
        instructionLabel.setFont(font);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        serverConnectionPanel.add(instructionLabel, gbc);

        JTextField serverIPField = new JTextField(15);
        serverIPField.setFont(font);
        serverIPField.setPreferredSize(new Dimension(400, 30));

        gbc.gridy = 1;
        serverConnectionPanel.add(serverIPField, gbc);

        JButton connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(400, 50));
        connectButton.setFont(font);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIP = serverIPField.getText();
                if (!serverIP.isEmpty()) {
                    client = new Client(UserAppGUI.this, serverIP);
                    if (client.getMessageHandler() != null) {
                        client.start();
                        cardLayout.show(frame.getContentPane(), "LoginPanel");
                    }
                }
            }
        });

        gbc.gridy = 2;
        serverConnectionPanel.add(connectButton, gbc);

        connectionErrorLabel = new JLabel();
        connectionErrorLabel.setFont(font);
        connectionErrorLabel.setForeground(Color.RED);
        connectionErrorLabel.setPreferredSize(new Dimension(400, 40));

        gbc.gridx = 1;
        gbc.gridy = 3;
        serverConnectionPanel.add(connectionErrorLabel, gbc);

        return serverConnectionPanel;
    }




    private JPanel createLoginPage() {
        JPanel loginPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        JLabel username = new JLabel("Username:");
        username.setFont(font);
        inputPanel.add(username);
        JTextField usernameField = new JTextField();
        usernameField.setFont(font);
        inputPanel.add(usernameField);

        JLabel password = new JLabel("Password:");
        password.setFont(font);
        inputPanel.add(password);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(font);
        inputPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(font);
        loginErrorLabel = new JLabel();

        loginErrorLabel.setForeground(Color.RED);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (usernameField.getText().equals("") || passwordField.getText().equals("")) {
                        loginErrorLabel.setText("Вы ввели пустые пароль или логин");
                    } else {
                        client.sendCommand(3, usernameField.getText(), new String(passwordField.getPassword()));
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        inputPanel.add(loginButton);
        JButton toRegisterButton = new JButton("Register");
        toRegisterButton.setFont(font);
        toRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(frame.getContentPane(), "RegisterPanel");
            }
        });
        inputPanel.add(toRegisterButton);
        loginPanel.add(loginErrorLabel, BorderLayout.NORTH);
        loginPanel.add(inputPanel, BorderLayout.CENTER);
        return loginPanel;
    }


    private JPanel createRegisterPage() {
        Font font = new Font("Monospaced", Font.PLAIN, 16);
        JPanel registerPanel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        JLabel username = new JLabel("Username:");
        username.setFont(font);
        inputPanel.add(username);
        JTextField usernameField = new JTextField();
        usernameField.setFont(font);
        inputPanel.add(usernameField);

        JLabel email = new JLabel("Email:");
        email.setFont(font);
        inputPanel.add(email);
        JTextField emailField = new JTextField();
        emailField.setFont(font);
        inputPanel.add(emailField);

        JLabel password = new JLabel("Password:");
        password.setFont(font);
        inputPanel.add(password);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(font);
        inputPanel.add(passwordField);
        registerErrorLabel = new JLabel();
        registerErrorLabel.setForeground(Color.RED);

        JButton registerButton = new JButton("Register");
        registerButton.setFont(font);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (usernameField.getText().equals("") || emailField.getText().equals("") || passwordField.getPassword().equals("")) {
                        registerErrorLabel.setText("Вы ввели пустые данные, попробуйте снова");
                    } else {
                        client.sendCommand(0, usernameField.getText(), emailField.getText(), new String(passwordField.getPassword()));  // Отправляем команду на сервер
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        inputPanel.add(registerButton);

        JButton toLoginButton = new JButton("Back to Login");
        toLoginButton.setFont(font);
        toLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(frame.getContentPane(), "LoginPanel");
            }
        });
        inputPanel.add(toLoginButton);

        registerPanel.add(registerErrorLabel, BorderLayout.NORTH);
        registerPanel.add(inputPanel, BorderLayout.CENTER);

        return registerPanel;
    }

    private JPanel createGameChoicePage() {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel instructionLabel = new JLabel("Выберите предпочитаемый язык", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        gamePanel.add(instructionLabel, gbc);

        JComboBox<String> languageComboBox = new JComboBox<>(new String[] {"english", "русский"});
        languageComboBox.setFont(new Font("Monospaced", Font.PLAIN, 16));
        gamePanel.add(languageComboBox, gbc);

        JButton startGameButton = new JButton("Начать игру");
        startGameButton.setFont(new Font("Monospaced", Font.PLAIN, 16));
        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLanguage = (String) languageComboBox.getSelectedItem();
                if (selectedLanguage == null) {
                    selectedLanguage = "english";
                }
                try {
                    client.sendCommand(6, selectedLanguage);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        gbc.weighty = 1;
        gamePanel.add(startGameButton, gbc);

        return gamePanel;
    }


    private JPanel createGamePage() {
        final JPanel[] gamePanel = {new JPanel()};
        gamePanel[0].setLayout(new BoxLayout(gamePanel[0], BoxLayout.Y_AXIS));
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        gameTextArea = new JTextArea();
        gameTextArea.setFont(new Font("Arial", Font.PLAIN, 20));
        gameTextArea.setEditable(false);
        gameTextArea.setLineWrap(true);
        gameTextArea.setWrapStyleWord(true);
        gameTextArea.setPreferredSize(new Dimension(500, 100));
        gamePanel[0].add(gameTextArea);

        userInputField = new JTextArea();
        userInputField.setFont(new Font("Arial", Font.PLAIN, 20));
        userInputField.setLineWrap(true);
        userInputField.setWrapStyleWord(true);
        gamePanel[0].add(userInputField);
        timeLeft = 30;
        firstInput = true;
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Оставшееся время: " + timeLeft);
                if (timeLeft <= 0) {
                    ((Timer)e.getSource()).stop();
                    TextAnalyzer textAnalyzer = new TextAnalyzer(gameTextArea.getText(), userInputField.getText());
                    int correctness = (int) textAnalyzer.getCorrectness();
                    int speed = textAnalyzer.getSpeed();
                    speedLabel.setText("Скорость: " + speed + " символов в минуту");
                    correctnessLabel.setText("Корректность: " + correctness + "%");


                    JPanel newGamePanel = createGamePage();

                    frame.getContentPane().remove(gamePanel[0]);
                    frame.getContentPane().add(newGamePanel, "GamePanel");
                    gamePanel[0] = newGamePanel;

                    cardLayout.show(frame.getContentPane(),"ResultPanel");
                }
            }
        });

        timerLabel = new JLabel("Оставшееся время: " + timeLeft);
        timerLabel.setFont(font);
        gamePanel[0].add(timerLabel);

        userInputField.getDocument().addDocumentListener(new DocumentListener() {

            private void startTimer() {
                if (firstInput) {
                    timer.start();
                    firstInput = false;
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                startTimer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                startTimer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                startTimer();
            }
        });

        return gamePanel[0];
    }

    private JPanel createResultPage() {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        Font font = new Font("Monospaced", Font.PLAIN, 20);

        speedLabel = new JLabel("Speed: ");
        speedLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        resultPanel.add(speedLabel, gbc);

        correctnessLabel = new JLabel("Correctness: ");
        correctnessLabel.setFont(font);
        gbc.gridy = 1;
        resultPanel.add(correctnessLabel, gbc);

        JButton submitButton = new JButton("Подтвердить результат");
        submitButton.setFont(font);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.sendCommand(8, correctnessLabel.getText().replace("Корректность: ", "").replace("%",""),
                            speedLabel.getText().replace("Скорость: ","").replace(" символов в минуту",""));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(0,0,15,0);
        resultPanel.add(submitButton, gbc);

        return resultPanel;
    }

    public JPanel createSaveNewOrOldResults() {
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel previousResultLabel = new JLabel("Ваш сохраненный раннее результат:");
        previousResultLabel.setFont(font);
        panel.add(previousResultLabel, gbc);

        previousSpeedField = new JLabel("Speed: ");
        previousSpeedField.setFont(font);
        panel.add(previousSpeedField, gbc);

        previousCorrectnessField = new JLabel("Correctness: ");
        previousCorrectnessField.setFont(font);
        gbc.insets = new Insets(0,0,20,0);
        panel.add(previousCorrectnessField, gbc);

        JLabel currentResultLabel = new JLabel("Ваш текущий результат: ");
        gbc.insets = new Insets(0,0,0,0);
        currentResultLabel.setFont(font);

        panel.add(currentResultLabel, gbc);

        currentSpeedField = new JLabel("Speed: ");
        currentSpeedField.setFont(font);
        panel.add(currentSpeedField, gbc);

        currentCorrectnessField = new JLabel("Correctness: ");
        currentCorrectnessField.setFont(font);
        panel.add(currentCorrectnessField, gbc);

        JButton saveNewResultButton = new JButton("Сохранить текущий результат");
        saveNewResultButton.setFont(font);
        saveNewResultButton.addActionListener(e -> {
            try {
                client.sendCommand(13, currentSpeedField.getText().replace("Speed: ", "").replace(" символов в минуту",""),
                        currentCorrectnessField.getText().replace("Correctness: ","").replace("%",""));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        gbc.weighty = 1;
        panel.add(saveNewResultButton, gbc);

        JButton keepPreviousResultButton = new JButton("Оставить прошлый результат");
        keepPreviousResultButton.setFont(font);
        keepPreviousResultButton.addActionListener(e -> {
            cardLayout.show(frame.getContentPane(), "ChooseRankingPanel");
            message.setText("");
        });
        panel.add(keepPreviousResultButton, gbc);

        return panel;
    }


    public JPanel createChooseRankingPage() {
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        message = new JLabel("Ваши результаты успешно сохранены");
        message.setFont(font);
        message.setHorizontalAlignment(JLabel.CENTER);

        JButton viewRankingButton = new JButton("Просмотреть рейтинг");
        viewRankingButton.setFont(font);
        viewRankingButton.addActionListener(e -> {
            try {
                client.sendCommand(10);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JButton returnToGameButton = new JButton("Вернуться к игре");
        returnToGameButton.setFont(font);
        returnToGameButton.addActionListener(e -> {
            cardLayout.show(frame.getContentPane(),"GameChoicePanel");
        });

        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel();

        panel.add(message, BorderLayout.NORTH);
        buttonsPanel.add(returnToGameButton);
        buttonsPanel.add(viewRankingButton);
        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    public JPanel createRankingPage() {
        Font font = new Font("Monospaced", Font.PLAIN, 16);

        JPanel rankingPanel = new JPanel();
        rankingPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Рейтинг игроков");
        title.setFont(font);
        title.setHorizontalAlignment(JLabel.CENTER);

        rankingArea = new JTextArea();
        rankingArea.setFont(font);
        rankingArea.setEditable(false);

        JButton returnToGameButton = new JButton("Вернуться к игре");
        returnToGameButton.setFont(font);
        returnToGameButton.addActionListener(e -> {
            cardLayout.show(frame.getContentPane(),"GameChoicePanel");
        });

        rankingPanel.add(title, BorderLayout.NORTH);
        rankingPanel.add(new JScrollPane(rankingArea), BorderLayout.CENTER);
        rankingPanel.add(returnToGameButton, BorderLayout.SOUTH);

        return rankingPanel;
    }
    public void updateRanking() {
        if (rankingData != null) {
            String[] users = rankingData.split("\\|");

            rankingArea.setText("");
            for (String user : users) {
                rankingArea.append(user + "\n");
            }
        }
    }

    public static void main(String[] args) {
        new UserAppGUI();
    }
}
