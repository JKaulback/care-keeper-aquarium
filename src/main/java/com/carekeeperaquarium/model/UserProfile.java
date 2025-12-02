package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.UUID;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.InsufficientPoints;
import com.carekeeperaquarium.exception.TooManyFish;

public class UserProfile {
    private String userName;
    private int points;
    private final ArrayList<Fish> ownedFishes;
    
    private static final int MAX_FISH = 10;

    // --- CONSTRUCTORS ---
    public UserProfile(String userName) {
        validateUserName(userName);
        this.userName = userName.trim();
        this.points = 100;
        ownedFishes = new ArrayList<>();
    }

    public UserProfile(String userNameLoaded, int pointsLoaded, ArrayList<Fish> ownedFishesLoaded) {
        validateUserName(userNameLoaded);
        if (pointsLoaded < 0)
            throw new IllegalArgumentException("Negative points during user profile initialization");
        if (ownedFishesLoaded.size() > MAX_FISH)
            throw new IllegalArgumentException("Too many fish during initialization: " + ownedFishesLoaded.size());
        
        this.userName = userNameLoaded.trim();
        this.points = pointsLoaded;
        this.ownedFishes = new ArrayList<>();

        for (Fish fish : ownedFishesLoaded) {
            if (fish == null)
                throw new IllegalArgumentException("Null fish in fish list during user profile initialization");
            this.ownedFishes.add(fish);
        }
    }

    // --- ACCESSORS ---
    public String getUserName() { return this.userName; }

    public int getPoints() { return this.points; }

    public ArrayList<Fish> getFish() { return new ArrayList<>(this.ownedFishes); } // Copy

    public int getNumberOfFishOwned() { return this.ownedFishes.size(); }

    public boolean isFull() { return ownedFishes.size() >= UserProfile.MAX_FISH; }

    public int getMaxFish() { return UserProfile.MAX_FISH; }

    public boolean hasDeadFish() {
        for (Fish fish : ownedFishes) {
            if (fish.isDead()){
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        StringBuilder userString = new StringBuilder();
        userString.append(String.format(
            "User: %s, Points: %d\nFish:\n", this.userName, this.points));
        
        for (Fish fish : ownedFishes) {
            userString.append(fish.toString()).append("\n");
        }
        
        return userString.toString();
    }

    // --- MODIFIERS ---
    public void changeUserName(String newUserName) {
        validateUserName(newUserName);
        if (this.userName.equals(newUserName))
            throw new IllegalArgumentException("New user name can not be the same as old name");
        this.userName = newUserName;
    }

    public void incrementPoints() {
        int toAdd = 1;
        for (Fish fish : ownedFishes) {
            toAdd += fish.getPointsWorth();
        }
        this.points += toAdd;
    }

    public void spendPoints(int points) throws InsufficientPoints {
        if (points < 0)
            throw new IllegalArgumentException("Can not spend negative points");
        if (this.points < points)
            throw new InsufficientPoints("Not enough points. " + this.points + 
                " is less than " + points);
        this.points -= points;
    }

    public void addFish(Fish newFish) throws TooManyFish {
        if (newFish == null)
            throw new IllegalArgumentException("Cannot add null fish");
        if (this.isFull())
            throw new TooManyFish("Attempted to add too many fish");
        this.ownedFishes.add(newFish);
    }

    public Fish getFish(UUID id) throws FishNotFound {
        for (Fish fish : ownedFishes) {
            if (fish.getId().equals(id))
                return fish;
        }
        throw new FishNotFound("Fish with id '" + id + "' not found");
    }

    public void removeFish(UUID id) throws FishNotFound {
        this.ownedFishes.remove(getFish(id));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserProfile other = (UserProfile) obj;
        return userName.equals(other.userName);
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }

    // --- HELPERS ---
    private void validateUserName(String newUserName) throws IllegalArgumentException {
        if (newUserName == null || newUserName.isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        if (newUserName.trim().length() > 50) {
            throw new IllegalArgumentException("User name cannot exceed 50 characters");
        }
    }

}
