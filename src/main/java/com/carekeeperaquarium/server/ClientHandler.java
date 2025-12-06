package com.carekeeperaquarium.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.carekeeperaquarium.business.AquariumManager;
import com.carekeeperaquarium.common.Command;
import com.carekeeperaquarium.model.Fish;
import com.carekeeperaquarium.model.UserProfile;

public class ClientHandler implements Runnable, PropertyChangeListener {

    private static final String CANCEL_STRING = "Cancelled. No changes made";

    private final Socket socket;
    private final AquariumManager aquariumManager;
    private final StateObserver stateObserver;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, AquariumManager aquariumManager, StateObserver stateObserver) {
        this.socket = socket;
        this.aquariumManager = aquariumManager;
        this.stateObserver = stateObserver;
    }

    @Override
    public void run() {
        try {
            // Register this client as a listener for state changes
            stateObserver.addPropertyChangeListener(this);
            
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals("tankUpdate") && !(username == null)) {
            sendStatusUpdate();
        }
    }

    private void sendStatusUpdate() {
        this.out.println("STATUS_UPDATE:START");
        this.out.println(aquariumManager.getAquariumStateSummaryFor(username));
        this.out.println("STATUS_UPDATE:END");
    }

    private boolean isUsernameNotNullOrEmpty(String username) {
        return username != null && !username.trim().isEmpty();
    }

    private boolean isValidNameCharacters(String username) {
        return username.matches("[a-zA-Z0-9 _-]+");
    }

    private void handleLogin() throws IOException {
        this.out.println("Welcome to CareKeeper Aquarium!");
        while (true) {
            username = this.in.readLine();
            
            // Validate username not null or empty
            if (!isUsernameNotNullOrEmpty(username)) {
                handleLoginFail("Invalid username. Please try again.");
                continue;
            }    

            // Validate username uses legal characters
            if (!isValidNameCharacters(username)) {
                handleLoginFail("Invalid username. Can not contain special characters");
                continue;
            }

            // Check if user already logged in
            if (aquariumManager.hasUser(username)) {
                handleLoginFail("Username already logged in. Please try a different username.");
                continue;
            }

            // Username acceptable
            break;

        }
        this.out.println("LOGIN:SUCCESSFUL");
        // Create user profile and add to aquarium manager
        UserProfile user = new UserProfile(username);
        aquariumManager.addUser(user);
        this.out.println("Login successful! Welcome, " + username + ".");
        System.out.println("User " + username + " has logged in");
    }

    private void handleLoginFail(String message) {
        this.out.println("LOGIN:FAIL");
        this.out.println(message);
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
                case ADD_FISH -> { 
                    String message;
                    try { message = aquariumManager.addFish(username); }
                    catch (IllegalStateException e) { message = e.getMessage(); } 
                    catch (Exception e) { message = "Error adding fish"; }
                    this.out.println(message);
                }
                case VIEW_FISH -> { 
                    String message;
                    try { message = aquariumManager.viewFish(username); }
                    catch (Exception e) { message = "Error viewing fish"; }
                    this.out.println(message); 
                }
                case FEED_FISH -> {
                    String message;
                    try { message = aquariumManager.feedFish(username); }
                    catch (Exception e) { message = "Error feeding fish"; }
                    this.out.println(message); 
                }
                case REMOVE_FISH -> { 
                    String message = CANCEL_STRING; // Assume cancel
                    sendFishListToClient(username); // Send fish list to client
                    String fishName = getName(); // Get the name of the fish to remove
                    // If not cancelled, attempt to remove the fish
                    if (!isOperationCancelled(fishName)) {
                        try { message = aquariumManager.removeFish(username, fishName); }
                        catch (Exception e) { message = e.getMessage(); } 
                    }
                    // Print the outcome to the user
                    this.out.println(message);
                }
                case CLEAN_TANK -> { this.out.println(aquariumManager.cleanTank()); }
                case VIEW_TANK -> { this.out.println(aquariumManager.getAquariumStateSummary()); }
                case GET_FISH_FACT -> { this.out.println("Feature to get general fish facts is not yet implemented.");} // TODO
                case QUIT -> { 
                    this.out.println("Goodbye, " + username + "!"); 
                    return;
                }
                default -> { this.out.println("Unknown command. Please try again."); }
            }
        }
    }

    private void handleShutdown() throws IOException {
        // Unregister from state observer
        stateObserver.removePropertyChangeListener(this);
        
        if (username != null) {
            try {
                this.out.println("Goodbye, " + username + "!");
                UserProfile user = aquariumManager.getUser(username);
                aquariumManager.removeUser(user);
                System.out.println("User " + username + " has disconnected");
            } catch (NoSuchElementException e) {
                System.out.println("User " + username + " not found: " + e.getMessage());
            }
        }
        if (this.in != null) { this.in.close(); }
        if (this.out != null) { this.out.close(); }
    }

    private String getName() throws IOException {
        String name = in.readLine();
        if (name == null)
            return "!cancel";
        return name;
    }

    private boolean isOperationCancelled(String value) {
        return value.equalsIgnoreCase("!cancel");
    }

    private void sendFishListToClient(String username) {
        try {
            UserProfile user = aquariumManager.getUser(username);
            ArrayList<Fish> fishList = user.getFish();
            
            if (fishList.isEmpty()) {
                this.out.println("FISH_LIST:EMPTY");
                return;
            }

            // Send fish list with special format that client can parse
            this.out.println("FISH_LIST:START");
            for (int i = 0; i < fishList.size(); i++) {
                Fish fish = fishList.get(i);
                this.out.println(fish.getName());
            }
            this.out.println("FISH_LIST:END");
        } catch (NoSuchElementException e) {
            this.out.println("FISH_LIST:ERROR");
        }
    }
}
