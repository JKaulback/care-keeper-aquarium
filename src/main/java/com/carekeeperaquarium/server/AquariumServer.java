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

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        
        System.out.println("Aquarium Server is starting on port " + SERVER_PORT + "...");
        
        while (true) {
            ClientHandler clientHandler = new ClientHandler(
                serverSocket.accept(),
                aquariumManager
            );
            addClient(clientHandler);
            ThreadPoolManager.getClientExecutor().execute(clientHandler);
        }
    }

    public static synchronized void addClient(ClientHandler client) {
        connectedClients.add(client);
    }

    public static synchronized void removeClient(ClientHandler client) {
        connectedClients.remove(client);
    }
}
