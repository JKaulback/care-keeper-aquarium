package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserProfile {
    private String username;
    private int points;
    private final ArrayList<Fish> ownedFishes;
    
    private static final int MAX_FISH = 9;

    // --- CONSTRUCTORS ---
    public UserProfile(String username) {
        validateUsername(username);
        this.username = username.trim();
        this.points = 100;
        ownedFishes = new ArrayList<>();
    }

    public UserProfile(String usernameLoaded, int pointsLoaded, ArrayList<Fish> ownedFishesLoaded) {
        validateUsername(usernameLoaded);
        if (pointsLoaded < 0)
            throw new IllegalArgumentException("Negative points during user profile initialization");
        if (ownedFishesLoaded.size() > MAX_FISH)
            throw new IllegalArgumentException("Too many fish during initialization: " + ownedFishesLoaded.size());
        
        this.username = usernameLoaded.trim();
        this.points = pointsLoaded;
        this.ownedFishes = new ArrayList<>();

        for (Fish fish : ownedFishesLoaded) {
            if (fish == null)
                throw new IllegalArgumentException("Null fish in fish list during user profile initialization");
            this.ownedFishes.add(fish);
        }
    }

    // --- ACCESSORS ---
    public String getUsername() { return this.username; }

    public int getPoints() { return this.points; }

    public ArrayList<Fish> getFish() { return new ArrayList<>(this.ownedFishes); } // Copy

    public int getNumberOfFishOwned() { return this.ownedFishes.size(); }

    public boolean isFull() { return ownedFishes.size() >= UserProfile.MAX_FISH; }

    public int getMaxFish() { return UserProfile.MAX_FISH; }

    public Fish getFishById(UUID id) {
        for (Fish fish : ownedFishes) {
            if (fish.getId().equals(id))
                return fish;
        }
        throw new NoSuchElementException("Fish with id '" + id + "' not found");
    }

    public boolean hasDeadFish() {
        for (Fish fish : ownedFishes) {
            if (fish.isDead()){
                return true;
            }
        }
        return false;
    }

    public boolean hasFish() {
        return !this.ownedFishes.isEmpty();
    }

    public ArrayList<Fish> getDeadFish() {
        ArrayList<Fish> deadFish = new ArrayList<>();
        for (Fish fish : ownedFishes) {
            if (fish.isDead()){
                deadFish.add(fish);
            }
        }
        return deadFish;
    }

    public ArrayList<Fish> getLiveFish() {
        ArrayList<Fish> livingFish = new ArrayList<>();
        for (Fish fish : ownedFishes) {
            if (!fish.isDead()) {
                livingFish.add(fish);
            }
        }
        return livingFish;
    }

    @Override
    public String toString() {
        StringBuilder userString = new StringBuilder();
        userString.append(String.format(
            "User: %s, Points: %d\nFish:\n", this.username, this.points));
        
        for (Fish fish : ownedFishes) {
            userString.append(fish.toString()).append("\n");
        }
        
        return userString.toString();
    }

    // --- MODIFIERS ---
    public void changeUsername(String newUsername) {
        validateUsername(newUsername);
        if (this.username.equals(newUsername))
            throw new IllegalArgumentException("New user name can not be the same as old name");
        this.username = newUsername.trim();
    }

    public void incrementPoints() {
        int toAdd = 1;
        for (Fish fish : ownedFishes) {
            toAdd += fish.getPointsWorth();
        }
        this.points += toAdd;
    }

    public void spendPoints(int points) {
        if (points < 0)
            throw new IllegalArgumentException("Can not spend negative points");
        if (this.points < points)
            throw new IllegalStateException("Not enough points. " + this.points + 
                " is less than " + points);
        this.points -= points;
    }

    public void addFish(Fish newFish) {
        if (newFish == null)
            throw new IllegalArgumentException("Cannot add null fish");
        if (this.isFull())
            throw new IllegalStateException("Attempted to add too many fish");
        this.ownedFishes.add(newFish);
    }

    public Fish getFish(String name) {
        for (Fish fish : ownedFishes) {
            if (fish.getName().equals(name))
                return fish;
        }
        throw new NoSuchElementException("Fish with name '" + name + "' not found");
    }

    public Fish removeFish(String name) {
        Fish removedFish = getFish(name);
        this.ownedFishes.remove(removedFish);
        return removedFish;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserProfile other = (UserProfile) obj;
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    // --- HELPERS ---
    private void validateUsername(String newUsername) throws IllegalArgumentException {
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (newUsername.trim().length() > 50) {
            throw new IllegalArgumentException("User name cannot exceed 50 characters");
        }
    }

}
