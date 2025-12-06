package com.carekeeperaquarium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.carekeeperaquarium.common.Command;

public class AquariumClient {
    private static final String SERVER_URL = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final ConsoleUI console;
    private String aquariumStatus = "This is a placeholder\ntext to represent\nwhat I want to show";
    private volatile boolean running = true;
    private volatile boolean loggedIn = false;
    private volatile boolean pauseMessages = false;
    private volatile boolean waitingForServerInput = false;

    public AquariumClient() throws IOException {
        console = new ConsoleUI();
        // Set initial status
        console.setStatusHeader(aquariumStatus);
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
        handleUserSession();
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
                        case "STATUS_UPDATE:START" -> handleStatusUpdate();
                        case "LOGIN:SUCCESSFUL" -> handleSuccessfulLogin();
                        case "LOGIN:FAIL" -> handleLoginFail();
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

    private void handleSuccessfulLogin() {
        loggedIn = true;
        waitingForServerInput = false;
    }

    private void handleLoginFail() throws IOException {
        console.println(in.readLine());
        waitingForServerInput = false;
    }

    private void handleFishListSelection() throws IOException {
        try {
            ArrayList<String> fishList = new ArrayList<>();
            String line;
            console.println("Loading Fish...");
            
            // Collect all fish data until END marker
            while ((line = in.readLine()) != null && !line.equals("FISH_LIST:END")) {
                fishList.add(line);
            }
            
            if (fishList.isEmpty()) {
                console.println("No fish available");
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

    private void handleStatusUpdate() throws IOException {
        StringBuilder statusBuilder = new StringBuilder();
        String line;
        
        // Collect all status data until END marker
        while ((line = in.readLine()) != null && !line.equals("STATUS_UPDATE:END")) {
            if (statusBuilder.length() > 0) {
                statusBuilder.append("\n");
            }
            statusBuilder.append(line);
        }
        
        aquariumStatus = statusBuilder.toString();
        console.setStatusHeader(aquariumStatus);
        
        // Don't auto-refresh to avoid clearing menus
        // Status will be displayed next time user opens menu
    }

    private void handleUserSession() {
        try {
            String input;
            
            handleLogin();
                
            while (running) {
                // Wait if server is requesting input from the listener thread
                if (waitingForServerInput) {
                    pauseThreadFor(100);
                    continue;
                }

                // Let the user decide when to move on
                console.readLine("Press enter to continue: ");
                
                input = MenuHandler.handleMenu(console);
                
                // Set flag before sending remove-fish command to wait for server response
                if (input.equalsIgnoreCase(Command.REMOVE_FISH.getPrimaryAlias())) {
                    waitingForServerInput = true;
                }

                // Check if quitting
                if (isQuit(input))
                    break;
                
                // Send to server
                this.out.println(input);
            }
        } finally {
            closeConnection();
        }
    }

    private boolean isQuit(String input) {
        for (String quitVal : Command.QUIT.getAliases()) {
            if (input.equalsIgnoreCase(quitVal))
                return true;
        }
        return false;
    }

    private void handleLogin() {
        while (!loggedIn) {
            if (waitingForServerInput) {
                pauseThreadFor(100);
                continue;
            }
            String input = console.readLine("Enter username: ");
            if (input != null && !input.trim().isEmpty()) {
                out.println(input);
                waitingForServerInput = true;
            }
        }
    }

    private void pauseThreadFor(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
