package com.carekeeperaquarium.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.carekeeperaquarium.business.AquariumManager;
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

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            // Handle user registration/login
            
            handleLogin();

            // Main interaction loop (to be implemented)

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
            this.out.println("Enter username: ");
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
    }

    private void handleShutdown() throws IOException {
        if (username != null) {
            try {
                this.out.println("Goodbye, " + username + "!");
                UserProfile user = aquariumManager.getUser(username);
                aquariumManager.removeUser(user);
                System.out.println("User " + username + " has disconnected and been removed from the aquarium.");
            } catch (Exception e) {
                System.out.println("Error during user shutdown: " + e.getMessage());
            }
        }

        if (this.in != null) { this.in.close(); }

        if (this.out != null) { this.out.close(); }
    }
}
