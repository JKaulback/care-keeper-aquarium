package com.carekeeperaquarium.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.carekeeperaquarium.business.AquariumManager;
import com.carekeeperaquarium.common.Command;
import com.carekeeperaquarium.exception.UserNotFound;
import com.carekeeperaquarium.model.UserProfile;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final AquariumManager aquariumManager;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, AquariumManager aquariumManager) {
        this.socket = socket;
        this.aquariumManager = aquariumManager;
    }

    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            // Handle user registration/login
            
            handleLogin();

            // Main interaction loop (to be implemented)
            runMainLoop();

            // Close resources on exit
            handleShutdown();
            
        } catch (IOException e) {
            System.out.println("Connection error with client: " + e.getMessage());
        } finally {
            AquariumServer.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private void handleLogin() throws IOException {
        this.out.println("Welcome to CareKeeper Aquarium!");
        while (true) {
            username = this.in.readLine();
            // Validate username
            if (validateUsername(username)) {
                // Check if user already logged in
                if (!aquariumManager.hasUser(username))
                    break;
                else
                    this.out.println("Username already logged in. Please try a different username.");
            } else {
                this.out.println("Invalid username. Please try again."); 
            }
        }
        // Create user profile and add to aquarium manager
        UserProfile user = new UserProfile(username);
        aquariumManager.addUser(user);
        this.out.println("Login successful! Welcome, " + username + ".");
        System.out.println("User " + username + " has logged in");
    }

    private void runMainLoop() throws IOException {
        while (true) {
            String clientMessage = in.readLine();
            if (clientMessage == null) {
                break;
            }
            
            // Parse command using enum
            Command command = Command.fromString(clientMessage);
            
            // Process commands using switch
            switch (command) {
                case ADD_FISH -> { this.out.println("Feature to add fish is not yet implemented."); } // TODO
                case VIEW_FISH -> { this.out.println("Feature to view fish is not yet implemented."); } // TODO
                case FEED_FISH -> { this.out.println("Feature to feed fish is not yet implemented."); } // TODO
                case REMOVE_FISH -> { this.out.println("Feature to remove fish is not yet implemented."); } // TODO
                case CLEAN_TANK -> { this.out.println("Feature to clean tank is not yet implemented."); } // TODO
                case VIEW_TANK -> { this.out.println(aquariumManager.getAquariumStateSummary()); }
                case GET_FISH_FACT_GENERAL -> { this.out.println("Feature to get general fish facts is not yet implemented.");} // TODO
                case FACT -> { this.out.println("Feature to get specific fish facts is not yet implemented."); } // TODO
                case CHANGE_USERNAME -> { this.out.println("Feature to change username not yet implemented."); } // TODO
                case QUIT -> { 
                    this.out.println("Goodbye, " + username + "!"); 
                    return;
                }
                default -> { this.out.println("Unknown command. Please try again."); }
            }
        }
    }

    private void handleShutdown() throws IOException {
        if (username != null) {
            try {
                this.out.println("Goodbye, " + username + "!");
                UserProfile user = aquariumManager.getUser(username);
                aquariumManager.removeUser(user);
                System.out.println("User " + username + " has disconnected");
            } catch (UserNotFound e) {
                System.out.println("User " + username + " not found" + e.getMessage());
            }
        }

        if (this.in != null) { this.in.close(); }

        if (this.out != null) { this.out.close(); }
    }
}
