package com.carekeeperaquarium.client;

import java.io.IOException;

import org.jline.keymap.BindingReader;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

public class ConsoleUI {
    private final Terminal terminal;
    private final LineReader lineReader;
    private final BindingReader bindingReader;
    private String statusHeader = "";

    public ConsoleUI() throws IOException {
        terminal = TerminalBuilder.builder()
                .system(true)
                .type("windows")
                .jna(true)
                .jansi(true)
                .build();
        
        lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
        
        bindingReader = new BindingReader(terminal.reader());
    }

    public void setStatusHeader(String status) {
        this.statusHeader = status;
    }

    public void println(String message) {
        // If we're currently reading input, print above the prompt line
        if (lineReader.isReading()) {
            lineReader.printAbove(message);
        } else {
            terminal.writer().println(message);
            terminal.flush();
        }
    }

    public String readLine(String prompt) {
        try {
            return lineReader.readLine(prompt);
        } catch (UserInterruptException e) {
            println("\nUse 'quit' or 'exit' to disconnect.");
            return null;
        } catch (EndOfFileException e) {
            return "quit";
        }
    }

    public String showMenu(Menu menu) {
        int selected = 0;
        int menuSize = menu.getItems().size();

        hideCursor();

        try {
            while (true) {
                displayMenu(menu, selected);

                // Read key input
                int key = readMenuKey();

                if (isUpArrow(key)) {
                    selected = moveUp(selected, menuSize);
                } else if (isDownArrow(key)) {
                    selected = moveDown(selected, menuSize);
                } else if (isEnter(key)) {
                    return menu.getItems().get(selected).getValue();
                }
            }
        } finally {
            showCursor();
            clearScreen();
        }
    }

    public void close() {
        try {
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing terminal: " + e.getMessage());
        }
    }

    private void displayMenu(Menu menu, int selected) {
        clearScreen();

        // Display status header at the top if available
        if (!statusHeader.isEmpty()) {
            println("╔═══════════════════════════════════════════════════════════════╗");
            String[] statusLines = statusHeader.split("\n");
            for (String line : statusLines) {
                println("║ " + line);
            }
            println("╚═══════════════════════════════════════════════════════════════╗");
            println("");
        }

        // Display title
        println("\n" + menu.getTitle());
        println("=".repeat(menu.getTitle().length()));
        println("");

        // Display menu items
        for (int i = 0; i < menu.getItems().size(); i++) {
            Menu.MenuItem item = menu.getItems().get(i);
            String prefix = (i == selected) ? "  → " : "    ";
            println(prefix + item.getLabel());
        }
        
        println("\nUse ↑↓ arrow keys to navigate, Enter to select");
    }

    private int readMenuKey() {
        int key = bindingReader.readCharacter();
        // Arrow keys start with an ESC sequence
        if (key == 27) {
            int next = bindingReader.readCharacter();
            if (next =='[' || next == 'O')
                return bindingReader.readCharacter();
        }

        return key;
    }

    private int moveUp(int selected, int menuSize) {
        return (selected - 1 + menuSize) % menuSize;
    }

    private int moveDown(int selected, int menuSize) {
        return (selected + 1) % menuSize;
    }

    private boolean isUpArrow(int key) {
        return key == 'A';
    }

    private boolean isDownArrow(int key) {
        return key == 'B';
    }

    private boolean isEnter(int key) {
        return (key == '\r' || key == '\n');
    }

    private void hideCursor() {
        terminal.puts(InfoCmp.Capability.cursor_invisible);
        terminal.flush();
    }

    private void showCursor() {
        terminal.puts(InfoCmp.Capability.cursor_visible);
        terminal.flush();
    }

    private void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }
}
