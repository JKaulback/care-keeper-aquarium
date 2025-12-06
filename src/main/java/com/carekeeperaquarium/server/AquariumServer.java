package com.carekeeperaquarium.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.carekeeperaquarium.business.AquariumManager;
import com.carekeeperaquarium.business.ThreadPoolManager;

public class AquariumServer {
    public static final int SERVER_PORT = 8080;
    public static final List<ClientHandler> connectedClients = new ArrayList<>();
    private static final AquariumManager aquariumManager = new AquariumManager();
    private static final ServerObserver serverObserver = new ServerObserver();

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        // Register shutdown hook to clean up resources on Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            try {
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
                ThreadPoolManager.shutdown();
                System.out.println("Server shutdown complete.");
            } catch (IOException e) {
                System.err.println("Error during shutdown: " + e.getMessage());
            }
        }));
        
        System.out.println("Aquarium Server is starting on port " + SERVER_PORT + "...");
        
        try {
            while (true) {
                ClientHandler clientHandler = new ClientHandler(
                    serverSocket.accept(),
                    aquariumManager
                );
                addClient(clientHandler);
                ThreadPoolManager.getClientExecutor().execute(clientHandler);
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                throw e;
            }
            // If socket is closed, it's likely due to shutdown hook
            System.out.println("Server socket closed.");
        }
    }

    public static synchronized void addClient(ClientHandler client) {
        connectedClients.add(client);
        serverObserver.addPropertyChangeListener(client);
    }

    public static synchronized void removeClient(ClientHandler client) {
        connectedClients.remove(client);
        serverObserver.removePropertyChangeListener(client);
    }
}
