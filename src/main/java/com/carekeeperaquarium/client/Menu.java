package com.carekeeperaquarium.client;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String title;
    private final List<MenuItem> items;

    public Menu(String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }

    public void addItem(String label, String value) {
        items.add(new MenuItem(label, value));
    }

    public String getTitle() {
        return title;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public static class MenuItem {
        private final String label;
        private final String value;

        public MenuItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }
}
