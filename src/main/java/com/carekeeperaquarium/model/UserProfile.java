package com.carekeeperaquarium.model;

import java.util.ArrayList;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.InsufficientPoints;

public class UserProfile {
    private String userName;
    private int points;
    private final ArrayList<Fish> ownedFishes;
    
    private final int MAX_FISH = 10;

    // --- CONSTRUCTORS ---
    public UserProfile(String userName) {
        this.userName = userName;
        this.points = 100;
        ownedFishes = new ArrayList<>();
    }

    public UserProfile(String userName, int points, ArrayList<Fish> ownedFishes) {
        this.userName = userName;
        this.points = points;
        this.ownedFishes = ownedFishes;
    }

    // --- ACCESSORS ---
    public String getUserName() { return this.userName; }

    public int getPoints() { return this.points; }

    public ArrayList<Fish> getFish() { return new ArrayList<>(this.ownedFishes); } // Copy

    public int getNumberOfFishOwned() { return this.ownedFishes.size(); }

    // --- MODIFIERS ---
    public boolean changeUserName(String newUserName) {
        if (this.userName.equals(newUserName))
            return false;
        this.userName = newUserName;
        return true;
    }

    public void incrementPoints() {
        int toAdd = 1;
        for (Fish fish : ownedFishes) {
            toAdd += fish.getPointsWorth();
        }
        this.points += toAdd;
    }

    public void spendPoints(int points) throws InsufficientPoints {
        if (this.points < points)
            throw new InsufficientPoints("Not enough points. " + this.points + 
                " is less than " + points);
        this.points -= points;
    }

    public void addFish(Fish newFish) {
        if (ownedFishes.size() <= MAX_FISH)
            this.ownedFishes.add(newFish);
    }

    public Fish getFish(int id) throws FishNotFound {
        for (Fish fish : ownedFishes) {
            if (fish.getId() == id)
                return fish;
        }
        throw new FishNotFound("Fish with id '" + id + "' not found");
    }

    public void removeFish(int id) throws FishNotFound {
        this.ownedFishes.remove(getFish(id));
    }

}
