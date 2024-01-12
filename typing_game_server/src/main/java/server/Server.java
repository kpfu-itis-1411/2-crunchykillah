package server;

import handler.MessageHandler;
import protocol.Protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    private static final int PORT = 11616;
    private Protocol protocol = new Protocol();
    private Map<String, MessageHandler> sessions = new HashMap<>();
    private ServerSocket serverSocket = null;

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server successfully started");
            ExecutorService executor = Executors.newFixedThreadPool(10);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientKey = clientSocket.getInetAddress().toString().replace("/", "");
                MessageHandler handler = sessions.get(clientKey);
                if (handler == null) {
                    System.out.println("New client connected: " + clientKey);
                    handler = new MessageHandler(clientSocket, protocol);
                    sessions.put(clientKey, handler);
                } else {
                    System.out.println("Welcome back, " + clientKey);
                    handler.setAuthenticated(true);
                    handler.setRunning(true);
                    handler.setClientSocket(clientSocket);
                }
                executor.execute(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server stopped");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
