package com.carekeeperaquarium.common;

/**
 * Defines all commands that can be sent between client and server.
 * This enum serves as the communication protocol, ensuring type safety
 * and consistency across the client-server boundary.
 */
public enum Command {
    ADD_FISH("add-fish"),
    VIEW_FISH("view-fish"),
    FEED_FISH("feed-fish"),
    REMOVE_FISH("remove-fish"),
    CLEAN_TANK("clean-tank"),
    VIEW_TANK("view-tank"),
    GET_FISH_FACT_GENERAL("get-fish-fact-general"),
    FACT("fact-"),
    QUIT("quit", "exit"),
    QUIT_MENU("quit-menu"),
    UNKNOWN("");
    
    private final String[] aliases;
    
    Command(String... aliases) {
        this.aliases = aliases;
    }
    
    /**
     * Parse a string input into a Command enum value.
     * @param input The user input string
     * @return The corresponding Command, or UNKNOWN if no match
     */
    public static Command fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }
        
        String normalized = input.toLowerCase().trim();
        
        for (Command cmd : Command.values()) {
            if (cmd == UNKNOWN) continue;
            
            for (String alias : cmd.aliases) {
                // For FACT command, check if input starts with the prefix
                if (cmd == FACT && normalized.startsWith(alias)) {
                    return cmd;
                }
                // For other commands, exact match
                if (normalized.equals(alias)) {
                    return cmd;
                }
            }
        }
        
        return UNKNOWN;
    }
    
    /**
     * Get the primary alias for this command.
     * @return The first alias defined for this command
     */
    public String getPrimaryAlias() {
        return aliases.length > 0 ? aliases[0] : "";
    }
    
    /**
     * Get all aliases for this command.
     * @return Array of all command aliases
     */
    public String[] getAliases() {
        return aliases;
    }
}
