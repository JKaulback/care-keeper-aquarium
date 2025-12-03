package com.carekeeperaquarium;

import com.carekeeperaquarium.client.AquariumClient;
import com.carekeeperaquarium.server.AquariumServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("client")) {
                // Launch client
                System.out.println("Starting Aquarium Client...");
                AquariumClient client = new AquariumClient();
                client.run();
            } else {
                // Launch server (default)
                System.out.println("Starting Aquarium Server...");
                AquariumServer server = new AquariumServer();
                server.run();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}