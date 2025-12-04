package com.carekeeperaquarium.client;

import java.io.IOException;

/**
 * Example demonstrating how to use the interactive menu
 */
public class MenuExample {
    public static void main(String[] args) throws IOException {
        ConsoleUI console = new ConsoleUI();

        // Create a menu
        Menu mainMenu = new Menu("Main Menu");
        mainMenu.addItem("View Aquarium", "view");
        mainMenu.addItem("Add Fish", "add");
        mainMenu.addItem("Feed Fish", "feed");
        mainMenu.addItem("Settings", "settings");
        mainMenu.addItem("Quit", "quit");

        // Show menu and get selection
        String choice = console.showMenu(mainMenu);
        
        console.println("\nYou selected: " + choice);
        
        // Handle the choice
        switch (choice) {
            case "view":
                console.println("Viewing aquarium...");
                break;
            case "add":
                console.println("Adding fish...");
                break;
            case "feed":
                console.println("Feeding fish...");
                break;
            case "settings":
                console.println("Opening settings...");
                break;
            case "quit":
                console.println("Goodbye!");
                break;
        }

        console.close();
    }
}
