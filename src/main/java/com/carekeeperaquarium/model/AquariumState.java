package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import com.carekeeperaquarium.business.FishFactory;

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

    public synchronized UserProfile getUser(String Username) {
        if (Username == null || Username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        if (users.containsKey(Username)) {
            return users.get(Username);
        }
        throw new NoSuchElementException("User not logged in");
    }

    public synchronized boolean hasUser(String username) {
        return users.containsKey(username);
    }

    public synchronized String userToString(String username) {
        if (!hasUser(username))
            throw new NoSuchElementException("User not found");
        return getUser(username).toString();
    }

    // --- MODIFIERS ---
    public synchronized void runIteration() {
        recalculateCleanliness();
        processHunger();
        processFishGrowth();
        processPointAwards();
        System.out.println("Updating tank...");
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
            if (this.tankCleanliness < MIN_CLEANLINESS)
                this.tankCleanliness = MIN_CLEANLINESS;
        }
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

    public synchronized Fish addFishRandom(String username) {
        Fish newFish = FishFactory.createRandomFish();
        UserProfile user = this.getUser(username);
        
        // Make sure there are no duplicate named fish
        int duplicateNameCount = 0;
        for (Fish fish : user.getFish()) {
            if (fish.getName().startsWith(newFish.getName()))
                duplicateNameCount++;
        }
        
        // If duplicates found, name fish {name} {duplicateNameCount}
        if (duplicateNameCount > 0) 
            newFish.changeName(newFish.getName() + " " + duplicateNameCount);

        user.addFish(newFish);
        return newFish;
    }

    public synchronized Fish removeFish(String username, String fishName) {
        UserProfile user = getUser(username);
        return user.removeFish(fishName);
    }

    public synchronized void cleanTank() {
        this.tankCleanliness = MAX_CLEANLINESS;
    }

    public synchronized int feedFish(String username) {
        UserProfile user = getUser(username);
        int count = 0;
        for (Fish fish : user.getFish()) {
            try {
                fish.feed();
                count++;
            } catch (IllegalStateException e) {
                // Attempt to feed dead fish
            }
        }
        return count;
    }

    protected synchronized void reset() {
        this.users.clear();
        this.tankCleanliness = MAX_CLEANLINESS;
    }
}
