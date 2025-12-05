package com.carekeeperaquarium.business;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.carekeeperaquarium.model.AquariumState;
import com.carekeeperaquarium.model.Fish;
import com.carekeeperaquarium.model.UserProfile;

public class AquariumManager {
    private final AquariumState aquariumInstance;

    private final ReentrantLock lock = new ReentrantLock();
    
    // Initialize the AquariumManager and start scheduled tasks
    public AquariumManager() {
        this.aquariumInstance = AquariumState.getInstance();

        startScheduledTasks();
    }

    private void startScheduledTasks() {
        ScheduledExecutorService scheduler = ThreadPoolManager.getScheduler();
        scheduler.scheduleAtFixedRate(
            aquariumInstance::runIteration, 
            0, 1, TimeUnit.MINUTES);
    }

    public void shutdown() {
        ThreadPoolManager.shutdown();
    }

    // --- SYNCHRONIZED HELPER METHODS ---
    private void executeWithLock(Runnable operation) {
        lock.lock();
        try {
            operation.run();
        } finally {
            lock.unlock();
        }
    }

    private <T> T executeWithLock(Supplier<T> operation) {
        lock.lock();
        try {
            return operation.get();
        } finally {
            lock.unlock();
        }
    }

    // --- INSTANCE ACCESS ---
    // --- ACCESSORS ---
    public ArrayList<UserProfile> getUsers() {
        return executeWithLock(() -> aquariumInstance.getUsers());
    }
    
    public double getTankCleanliness() {
        return executeWithLock(() -> aquariumInstance.getTankCleanliness());
    }

    public UserProfile getUser(String userName) {
        lock.lock();
        try {
            return aquariumInstance.getUser(userName);
        } finally {
            lock.unlock();
        }
    }

    public boolean hasUser(String username) {
        return executeWithLock(() -> aquariumInstance.hasUser(username));
    }

    public String getAquariumStateSummary() {
        return executeWithLock(() -> {
            StringBuilder summary = new StringBuilder();
            summary.append("Aquarium Cleanliness: ")
                   .append(String.format("%.2f", aquariumInstance.getTankCleanliness()))
                   .append("\n");
            summary.append("Users Online: ").append(aquariumInstance.getUsers().size()).append("\n");
            for (UserProfile user : aquariumInstance.getUsers()) {
                summary.append("- ").append(user.getUsername())
                       .append(" (Points: ").append(user.getPoints())
                       .append(", Fish Owned: ").append(user.getNumberOfFishOwned())
                       .append(")\n");
            }
            return summary.toString();
        });
    }

    // --- MODIFIERS ---
    public void addUser(UserProfile user) {
        executeWithLock(() -> aquariumInstance.addUser(user));
    }

    public boolean removeUser(UserProfile user) {
        return executeWithLock(() -> aquariumInstance.removeUser(user));
    }

    public String addFish(String username) {
        return executeWithLock(() -> {
            StringBuilder summary = new StringBuilder();
            Fish newFish = aquariumInstance.addFishRandom(username);
            
            summary.append("New fish added:\n").append(newFish.toString());
            return summary.toString();
        });
    }

    public String viewFish(String username) {
        return executeWithLock(() -> aquariumInstance.userToString(username));
    }

    public void cleanTank(double cleanValue) {
        executeWithLock(() -> aquariumInstance.cleanTank(cleanValue));
    }

    public String feedFish(String userName) {
        return executeWithLock(() -> {
            int numFishFed = aquariumInstance.feedFish(userName);
            return "Fish Fed: " + numFishFed;
        });
    }

}
