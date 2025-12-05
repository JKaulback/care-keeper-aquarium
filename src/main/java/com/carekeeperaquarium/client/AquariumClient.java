package com.carekeeperaquarium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class AquariumClient {
    private static final String SERVER_URL = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ConsoleUI console;
    private volatile boolean running = true;
    private volatile boolean loggedIn = false;
    private volatile boolean pauseMessages = false;
    private volatile boolean waitingForServerInput = false;

    public AquariumClient() throws IOException {
        console = new ConsoleUI();
    }

    public void run() throws IOException {
        console.println("Connecting to Aquarium Server at " + SERVER_URL + ":" + SERVER_PORT + "...");
        
        socket = new Socket(SERVER_URL, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        console.println("Connected to server!");
        
        // Start a thread to listen for messages from the server
        Thread listenerThread = new Thread(this::listenForMessages);
        listenerThread.setDaemon(true);
        listenerThread.start();
        
        // Main thread handles user input
        handleUserInput();
    }

    private void listenForMessages() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                if (!pauseMessages) {
                    // Check for structured fish list data
                    switch (message) {
                        case "FISH_LIST:START" -> handleFishListSelection();
                        case "FISH_LIST:EMPTY" -> console.println("You don't have any fish to remove.");
                        case "FISH_LIST:ERROR" -> console.println("Error retrieving fish list.");
                        default -> console.println(message);
                    }
                }
            }
        } catch (IOException e) {
            if (running) {
                console.println("Disconnected from server.");
            }
        }
    }

    private void handleFishListSelection() throws IOException {
        waitingForServerInput = true;
        try {
            ArrayList<String> fishList = new ArrayList<>();
            String line;
            console.println("Loading Fish...");
            
            // Collect all fish data until END marker
            while ((line = in.readLine()) != null && !line.equals("FISH_LIST:END")) {
                fishList.add(line);
            }
            
            if (fishList.isEmpty()) {
                console.println("No fish available to remove.");
                out.println("!cancel");
                return;
            }
                   
            // Get user selection
            String selection = MenuHandler.handleFishSelectionMenu(console, fishList);
            
            // Send selection back to server
            out.println(selection);
        } finally {
            waitingForServerInput = false;
        }
    }

    private void handleUserInput() {
        try {
            String input;
            while (running) {
                // Wait if server is requesting input from the listener thread
                while (waitingForServerInput) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                if (!loggedIn) {
                    input = console.readLine("Enter username: ");
                    if (input != null && !input.trim().isEmpty()) {
                        out.println(input);
                        loggedIn = true;
                    }
                    continue;
                }

                input = console.readLine("Press enter to open the menu or 'quit' to stop: ");
                
                if (input.equalsIgnoreCase("quit"))
                    break;

                pauseMessages = true;
                input = MenuHandler.handleMenu(console);
                pauseMessages = false;
                
                if (input.equalsIgnoreCase("quit-menu"))
                    continue;

                // Set flag before sending remove-fish command to wait for server response
                if (input.equalsIgnoreCase("remove-fish")) {
                    waitingForServerInput = true;
                }
                
                this.out.println(input);
            }
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        running = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (console != null) {
                console.println("Connection closed.");
                console.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
