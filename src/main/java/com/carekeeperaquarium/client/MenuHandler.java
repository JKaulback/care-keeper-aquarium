package com.carekeeperaquarium.client;

import java.util.ArrayList;

import com.carekeeperaquarium.common.Command;

public class MenuHandler {

    private enum MenuOption {
        MAIN, FISH, TANK, FACTS, SPECIES1, SPECIES2, SETTINGS, RETURNING
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
            case MenuOption.FACTS -> { return showFishFactsMenu(console); }
            case MenuOption.SPECIES1 -> { return showSpecies1Menu(console); }
            case MenuOption.SPECIES2 -> { return showSpecies2Menu(console); }
            default -> throw new IllegalArgumentException("Unexpected value: " + currentMenu);
        }
    }

    private static MenuOption processInput(String input) {
        return switch (input) {
            case "manage-fish" -> MenuOption.FISH;
            case "manage-tank" -> MenuOption.TANK;
            case "fish-facts", "back-facts" -> MenuOption.FACTS;
            case "get-fish-fact-specific", "species-1" -> MenuOption.SPECIES1;
            case "species-2" -> MenuOption.SPECIES2;
            case "back" -> MenuOption.MAIN;
            default -> MenuOption.RETURNING; // A command has been determined, returning to AquariumClient
        };
    }

    private static String showMainMenu(ConsoleUI console) {
        Menu mainMenu = new Menu("Aquarium Client Main Menu");
        mainMenu.addItem("Manage Fish", "manage-fish");
        mainMenu.addItem("Manage Tank", "manage-tank");
        mainMenu.addItem("Get Fish Facts", "fish-facts");
        mainMenu.addItem("Quit", Command.QUIT_MENU.getPrimaryAlias());
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

    private static String showFishFactsMenu(ConsoleUI console) {
        Menu fishFactsMenu = new Menu("Fish Facts Menu");
        fishFactsMenu.addItem("General Fish Facts", Command.GET_FISH_FACT_GENERAL.getPrimaryAlias());
        fishFactsMenu.addItem("Specific Fish Facts", "get-fish-fact-specific");
        fishFactsMenu.addItem("Back to Main Menu", "back");
        return console.showMenu(fishFactsMenu);
    }
     
    private static String showSpecies1Menu(ConsoleUI console) {
        Menu speciesMenu = new Menu("Fish Species Menu (page 1)");
        speciesMenu.addItem("Angel Fish", "fact-angel-fish");
        speciesMenu.addItem("Yellow Tang", "fact-yellow-tang");
        speciesMenu.addItem("Neon Goby", "fact-neon-goby");
        speciesMenu.addItem("Clown Loach", "fact-clown-loach");
        speciesMenu.addItem("Swordtail", "fact-swordtail");
        speciesMenu.addItem("Cardinal Tetra", "fact-cardinal-tetra");
        speciesMenu.addItem("Next Page", "species-2");
        speciesMenu.addItem("Back to Fish Facts Menu", "back-facts");
        return console.showMenu(speciesMenu);
    }

    private static String showSpecies2Menu(ConsoleUI console) {
        Menu speciesMenu = new Menu("Fish Species Menu (page 2)");
        speciesMenu.addItem("Bristlenose", "fact-bristlenose");
        speciesMenu.addItem("Betta", "fact-betta");
        speciesMenu.addItem("Tiger Barb", "fact-tiger-barb");
        speciesMenu.addItem("White Cloud Mountain Minnow", "fact-white-cloud-mountain-minnow");
        speciesMenu.addItem("Convict Cichlid", "fact-convict-cichlid");
        speciesMenu.addItem("Clownfish", "fact-clownfish");
        speciesMenu.addItem("Prev Page", "species-1");
        speciesMenu.addItem("Back to Fish Facts Menu", "back-facts");
        return console.showMenu(speciesMenu);
    }
}
