package com.carekeeperaquarium.model;

import java.util.ArrayList;

import com.carekeeperaquarium.exception.UserNotFound;

public class AquariumState {
    private static AquariumState instance;

    private ArrayList<UserProfile> users;
    private double tankCleanliness;


    // --- CONSTRUCTOR ---
    private AquariumState() {
      this.users = new ArrayList<>();
      this.tankCleanliness = 100;  
    }

    public static AquariumState getAquarium() {
        if (instance == null) {
            instance = new AquariumState();
        }

        return instance;
    }

    // --- ACCESSORS ---
    public ArrayList<UserProfile> getUsers() { return new ArrayList<>(users); }

    public double getTankCleanliness() { return this.tankCleanliness; }

    public UserProfile getUser(String userName) throws UserNotFound {
        for (UserProfile user : users) {
            if (user.getUserName().equals(userName))
                return user;
        }
        throw new UserNotFound("User not logged in");
    }

    // --- MODIFIERS ---
    public void addUser(UserProfile user) {
        users.add(user);
    }

    public void removeUser(UserProfile user) {
        users.remove(user);
    }

    public void recalculateCleanliness() {
        
        if (this.tankCleanliness > 0) {
            double tankSoilValue = 1;
            for (UserProfile user : users) {
                for (Fish fish : user.getFish()) {
                    tankSoilValue += fish.getSize() * fish.getSoilRate();
                }
            }
            this.tankCleanliness -= tankSoilValue;
        }

        if (this.tankCleanliness <= 0)
            this.tankCleanliness = 0;
    }
    
    public void cleanTank(double cleanValue) {
        this.tankCleanliness += cleanValue;
        if (this.tankCleanliness > 100) {
            this.tankCleanliness = 100;
        }
    }

    // For testing purposes only
    protected void reset() {
        this.users.clear();
        this.tankCleanliness = 100;
    }

}
