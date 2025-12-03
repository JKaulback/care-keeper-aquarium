package com.carekeeperaquarium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AquariumClient {
    private static final String SERVER_URL = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public AquariumClient() {
        scanner = new Scanner(System.in);
    }

    public void run() throws IOException {
        System.out.println("Connecting to Aquarium Server at " + SERVER_URL + ":" + SERVER_PORT + "...");
        
        socket = new Socket(SERVER_URL, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        System.out.println("Connected to server!");
        
        // Start a thread to listen for messages from the server
        Thread listenerThread = new Thread(this::listenForMessages);
        listenerThread.start();
        
        // Main thread handles user input
        handleUserInput();
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }

    private void handleUserInput() {
        try {
            String input;
            while (true) {
                input = scanner.nextLine();
                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                    break;
                }
                out.println(input);
            }
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (scanner != null) scanner.close();
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
