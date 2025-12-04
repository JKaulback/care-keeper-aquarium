package com.carekeeperaquarium.business;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.UserNotFound;
import com.carekeeperaquarium.model.AquariumState;
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

    public UserProfile getUser(String userName) throws UserNotFound {
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

    public void cleanTank(double cleanValue) {
        executeWithLock(() -> aquariumInstance.cleanTank(cleanValue));
    }

    public void feedFish(String userName, java.util.UUID fishId, int foodAmount) 
            throws UserNotFound, FishNotFound {
        lock.lock();
        try {
            aquariumInstance.feedFish(userName, fishId, foodAmount);
        } finally {
            lock.unlock();
        }
    }

}
