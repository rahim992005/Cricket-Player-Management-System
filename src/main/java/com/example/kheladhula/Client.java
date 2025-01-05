package com.example.kheladhula;

import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private List<ServerMessageListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private volatile boolean running = true;

    public void startClient(String serverAddress, int port, String clubName) {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Send club name
            out.writeObject(clubName);

            // Listen for messages
            new Thread(() -> {
                try {
                    Object message;
                    while (running && (message = in.readObject()) != null) {
                        if (message instanceof List) {
                            List<Player> players = (List<Player>) message;
                            Platform.runLater(() -> handleServerMessage(players));
                        } else if (message instanceof String) {
                            String textMessage = (String) message;
                            Platform.runLater(() -> handleServerMessage(textMessage));
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (running) {
                        e.printStackTrace(); // Log only if the client is still running
                    }
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            if (socket != null && out != null) {
                out.writeObject(message);
                out.flush();
            } else {
                System.err.println("Socket or output stream is not initialized.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(ServerMessageListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(List<Player> players) {
        synchronized (listeners) {
            for (ServerMessageListener listener : listeners) {
                listener.onServerMessage(players);
            }
        }
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void handleServerMessage(List<Player> players) {
        notifyListeners(players);
    }

    public void handleServerMessage(String message) {
        synchronized (listeners) {
            for (ServerMessageListener listener : listeners) {
                listener.onServerMessage(message);
            }
        }
    }

    public void stopClient() {
        running = false; // Set the flag to false to stop the reading loop
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
