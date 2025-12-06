package com.carekeeperaquarium.server;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class StateObserver {
    private final List<PropertyChangeListener> listeners;
    private String aquariumSummary;

    public StateObserver() {
        listeners = new ArrayList<>();
        aquariumSummary = "Nothing yet...";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    public String getAquariumSummary() {
        return aquariumSummary;
    }

    public void setAquariumSummary(String newSummary) {
        String oldSummary = this.aquariumSummary;
        this.aquariumSummary = newSummary;
        firePropertyChangeEvent("tankUpdate", oldSummary, newSummary);
    }

    private void firePropertyChangeEvent(String propertyName, String oldSummary, String newSummary) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldSummary, newSummary);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }
}
