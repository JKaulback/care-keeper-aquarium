package com.carekeeperaquarium;

import java.io.IOException;

import com.carekeeperaquarium.client.AquariumClient;
import com.carekeeperaquarium.server.AquariumServer;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equalsIgnoreCase("client")) {
                // Launch client
                AquariumClient client = new AquariumClient();
                client.run();
            } else {
                // Launch server (default)
                AquariumServer server = new AquariumServer();
                server.run();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}