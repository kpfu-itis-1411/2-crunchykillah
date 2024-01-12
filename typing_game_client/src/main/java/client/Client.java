package client;

import app_gui.UserAppGUI;
import handler.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import protocol.Protocol;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

@Getter@Setter
public class Client {
    private static final int SERVER_PORT = 11616;

    private Socket socket;
    private Protocol protocol;
    private MessageHandler messageHandler;
    private UserAppGUI gui;

    public Client(UserAppGUI gui, String serverIP) {
        this.gui = gui;
        protocol = new Protocol();
        try {
            socket = new Socket(serverIP, SERVER_PORT);
            messageHandler = new MessageHandler(socket, protocol, gui);
        } catch (SocketException | UnknownHostException e) {
            SwingUtilities.invokeLater(() -> {
                gui.getConnectionErrorLabel().setText("Ошибка подключения, попробуйте еще раз");
                gui.getCardLayout().show(gui.getFrame().getContentPane(), "ServerConnectionPanel");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(() -> {
            messageHandler.handleMessages();
        }).start();
    }

    public void sendCommand(int commandType, String... args) throws IOException {
        byte[] command = protocol.createCommand(commandType, args);
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(command);
        outputStream.flush();
    }
}
