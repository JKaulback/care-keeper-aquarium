package com.carekeeperaquarium.client;

import java.util.ArrayList;

import com.carekeeperaquarium.common.Command;

public class MenuHandler {

    private enum MenuOption {
        MAIN, FISH, TANK, FACT, RETURNING
    }

    public static String handleMenu(ConsoleUI console) {
        MenuOption currentMenu = MenuOption.MAIN;
        String input;
        while (true) { 
            input = getSelection(console, currentMenu); // Menu.value of the selection
            currentMenu = processInput(input); // Decide on next navigation or returning
            if (currentMenu == MenuOption.RETURNING) // If returning, end menu navigation
                return input;
        }
    }

    public static String handleFishSelectionMenu(ConsoleUI console, ArrayList<String> fishArray) {
        Menu fishListMenu = new Menu("Your Fish");
        for (String fish : fishArray)
            fishListMenu.addItem(fish);
        fishListMenu.addItem("Cancel", "!cancel");
        return console.showMenu(fishListMenu);
    }

    private static String getSelection(ConsoleUI console, MenuOption currentMenu) {
        switch (currentMenu) {
            case MenuOption.MAIN -> { return showMainMenu(console); }
            case MenuOption.FISH -> { return showFishMenu(console); }
            case MenuOption.TANK -> { return showTankMenu(console); }
            default -> throw new IllegalArgumentException("Unexpected value: " + currentMenu);
        }
    }

    private static MenuOption processInput(String input) {
        return switch (input) {
            case "manage-fish" -> MenuOption.FISH;
            case "manage-tank" -> MenuOption.TANK;
            case "fish-fact" -> MenuOption.FACT;
            case "back" -> MenuOption.MAIN;
            default -> MenuOption.RETURNING; // A command has been determined, returning to AquariumClient
        };
    }

    private static String showMainMenu(ConsoleUI console) {
        Menu mainMenu = new Menu("Aquarium Client Main Menu");
        mainMenu.addItem("Manage Fish", "manage-fish");
        mainMenu.addItem("Manage Tank", "manage-tank");
        mainMenu.addItem("Get Fish Fact", Command.GET_FISH_FACT.getPrimaryAlias());
        mainMenu.addItem("Log Out", Command.QUIT.getPrimaryAlias());
        return console.showMenu(mainMenu);
    }

    private static String showFishMenu(ConsoleUI console) {
        Menu fishMenu = new Menu("Fish Management Menu");
        fishMenu.addItem("Add Fish", Command.ADD_FISH.getPrimaryAlias());
        fishMenu.addItem("View Fish", Command.VIEW_FISH.getPrimaryAlias());
        fishMenu.addItem("Feed Fish", Command.FEED_FISH.getPrimaryAlias());
        fishMenu.addItem("Remove Fish", Command.REMOVE_FISH.getPrimaryAlias());
        fishMenu.addItem("Back to Main Menu", "back");
        return console.showMenu(fishMenu);
    }

    private static String showTankMenu(ConsoleUI console) {
        Menu tankMenu = new Menu("Tank Maintenance Menu");
        tankMenu.addItem("View Tank Status", Command.VIEW_TANK.getPrimaryAlias());
        tankMenu.addItem("Clean Tank", Command.CLEAN_TANK.getPrimaryAlias());
        tankMenu.addItem("Back to Main Menu", "back");
        return console.showMenu(tankMenu);
    }
}
