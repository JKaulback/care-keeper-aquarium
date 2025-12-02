package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.carekeeperaquarium.exception.UserNotFound;

public class AquariumState {
    private static AquariumState instance;

    private static final double MAX_CLEANLINESS = 100.0;
    private static final double MIN_CLEANLINESS = 0.0;
    private static final double BASE_SOIL_VALUE = 1.0;

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
    public ArrayList<UserProfile> getUsers() { return new ArrayList<>(users.values()); }

    public double getTankCleanliness() { return this.tankCleanliness; }

    public UserProfile getUser(String userName) throws UserNotFound {
        if (userName == null || userName.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty");
        if (users.containsKey(userName)) {
            return users.get(userName);
        }
        throw new UserNotFound("User not logged in");
    }

    // --- MODIFIERS ---
    public void addUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("Cannot add null user to aquarium");
        if (users.containsKey(user.getUserName()))
            throw new IllegalArgumentException("User already exists");
        users.put(user.getUserName(), user);
    }

    public boolean removeUser(UserProfile user) {
        if (user == null)
            throw new IllegalArgumentException("Cannot remove null user from aquarium");
        return users.remove(user.getUserName()) != null;
    }

    public void recalculateCleanliness() {
        if (this.tankCleanliness > MIN_CLEANLINESS) {
            double tankSoilValue = BASE_SOIL_VALUE;
            for (UserProfile user : users.values()) {
                for (Fish fish : user.getFish()) {
                    tankSoilValue += fish.getSize() * fish.getSoilRate();
                }
            }
            this.tankCleanliness -= tankSoilValue;
        }
        this.clampCleanliness();
    }
    
    public void cleanTank(double cleanValue) {
        if (cleanValue < 0)
            throw new IllegalArgumentException("Clean value cannot be negative");
        this.tankCleanliness += cleanValue;
        this.clampCleanliness();
    }

    protected void reset() {
        this.users.clear();
        this.tankCleanliness = MAX_CLEANLINESS;
    }

    private void clampCleanliness() {
        if (this.tankCleanliness < MIN_CLEANLINESS) this.tankCleanliness = MIN_CLEANLINESS;
        else if (this.tankCleanliness > MAX_CLEANLINESS) this.tankCleanliness = MAX_CLEANLINESS;
    }
}
