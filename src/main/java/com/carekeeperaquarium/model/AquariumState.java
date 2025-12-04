package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.UserNotFound;

public class AquariumState {
    private static AquariumState instance;

    private static final double MAX_CLEANLINESS = 100.0;
    private static final double MIN_CLEANLINESS = 0.0;

    private final HashMap<String, UserProfile> users;
    private double tankCleanliness;


    // --- CONSTRUCTOR ---
    private AquariumState() {
      this.users = new HashMap<>();
      this.tankCleanliness = MAX_CLEANLINESS;  
    }

    public static synchronized AquariumState getInstance() {
        if (instance == null) {
            instance = new AquariumState();
        }

        return instance;
    }

    // --- ACCESSORS ---
    public synchronized ArrayList<UserProfile> getUsers() { return new ArrayList<>(users.values()); }

    public synchronized double getTankCleanliness() { return this.tankCleanliness; }

    public synchronized UserProfile getUser(String Username) throws UserNotFound {
        if (Username == null || Username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        if (users.containsKey(Username)) {
            return users.get(Username);
        }
        throw new UserNotFound("User not logged in");
    }

    public synchronized boolean hasUser(String username) {
        return users.containsKey(username);
    }

    // --- MODIFIERS ---
    public synchronized void runIteration() {
        recalculateCleanliness();
        processHunger();
        processFishGrowth();
        processPointAwards();
    }

    public synchronized void addUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("Cannot add null user to aquarium");
        if (users.containsKey(user.getUsername()))
            throw new IllegalArgumentException("User already exists");
        users.put(user.getUsername(), user);
    }

    public synchronized boolean removeUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("Cannot remove null user from aquarium");
        return users.remove(user.getUsername()) != null;
    }

    public synchronized void recalculateCleanliness() {
        if (this.tankCleanliness > MIN_CLEANLINESS) {
            double tankSoilValue = 0;
            for (UserProfile user : users.values()) {
                for (Fish fish : user.getFish()) {
                    tankSoilValue += fish.getSize() * fish.getSoilRate();
                }
            }
            this.tankCleanliness -= tankSoilValue;
        }
        this.clampCleanliness();
    }

    public synchronized void processHunger() {
        for (UserProfile user : users.values()) {
            for (Fish fish : user.getFish()) {
                fish.processHunger();
            }
        }
    }
    
    public synchronized void processFishGrowth() {
        for (UserProfile user : users.values()) {
            for (Fish fish : user.getFish()) {
                fish.grow();
            }
        }
    }

    public synchronized void processPointAwards() {
        for (UserProfile user : users.values()) {
            user.incrementPoints();
        }
    }

    public synchronized void cleanTank(double cleanValue) {
        if (cleanValue < 0)
            throw new IllegalArgumentException("Clean value cannot be negative");
        this.tankCleanliness += cleanValue;
        this.clampCleanliness();
    }

    public synchronized void feedFish(String username, UUID fishId, int foodAmount) throws UserNotFound, FishNotFound {
        UserProfile user = getUser(username);
        Fish fish = user.getFishById(fishId);
        fish.feed(foodAmount);
    }

    protected synchronized void reset() {
        this.users.clear();
        this.tankCleanliness = MAX_CLEANLINESS;
    }

    private synchronized void clampCleanliness() {
        if (this.tankCleanliness < MIN_CLEANLINESS) this.tankCleanliness = MIN_CLEANLINESS;
        else if (this.tankCleanliness > MAX_CLEANLINESS) this.tankCleanliness = MAX_CLEANLINESS;
    }
}
