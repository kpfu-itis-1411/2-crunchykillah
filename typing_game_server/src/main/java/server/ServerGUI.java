package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public class ServerGUI {
    private JTextArea consoleOutput;
    private JButton startButton;
    private JButton stopButton;
    private Server server = new Server();
    public ServerGUI() {
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        frame.add(scrollPane, BorderLayout.CENTER);


        PrintStream printStream = new PrintStream(new TextAreaOutputStream(consoleOutput));
        System.setOut(printStream);
        System.setErr(printStream);

        startButton = new JButton("Start Server");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleOutput.append("Server started\n");
                startServer();
            }
        });

        stopButton = new JButton("Stop Server");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consoleOutput.append("Server stopped\n");
                server.stop();
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stop();
                System.exit(0);
            }
        });

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);
        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public void startServer() {
        new Thread(() -> {
            consoleOutput.append("Server started\n");
            server.start();
        }).start();
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}
