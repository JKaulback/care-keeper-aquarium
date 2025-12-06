package com.carekeeperaquarium.business;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.carekeeperaquarium.model.AquariumState;
import com.carekeeperaquarium.model.Fish;
import com.carekeeperaquarium.model.UserProfile;
import com.carekeeperaquarium.server.StateObserver;

public class AquariumManager {
    private final AquariumState aquariumInstance;

    private final ReentrantLock lock = new ReentrantLock();
    
    // Initialize the AquariumManager and start scheduled tasks
    public AquariumManager(StateObserver serverObserver) {
        this.aquariumInstance = AquariumState.getInstance();
        
        // Set observer in AquariumState so it can notify on changes
        this.aquariumInstance.setObserver(serverObserver);

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

    public String getAquariumStateSummaryFor(String username) {
        return executeWithLock(() -> aquariumInstance.getSummaryFor(username));
    }

    public String getAquariumStateSummary() {
        return executeWithLock(() -> aquariumInstance.getSummary());
    }

    // --- MODIFIERS ---
    public void addUser(UserProfile user) {
        executeWithLock(() -> aquariumInstance.addUser(user));
    }

    public boolean removeUser(UserProfile user) {
        return executeWithLock(() -> aquariumInstance.removeUser(user));
    }

    public String changeUsername(String oldName, String newName) {
        return executeWithLock(() ->{
            if (aquariumInstance.changeName(oldName, newName)) {
                return "Name successfully changed from '" + oldName + "' to '" + newName + "'";
            } else {
                return "Failed to change name";
            }
        });
    }

    public String addFish(String username) {
        return executeWithLock(() -> {
            Fish newFish = aquariumInstance.addFishRandom(username);
            return "New Fish Added:\n" + newFish.toString();
        });
    }

    public String viewFish(String username) {
        return executeWithLock(() -> aquariumInstance.userToString(username));
    }

    public String removeFish(String username, String fishName) {
        return executeWithLock(() ->{ 
            Fish removedFish = aquariumInstance.removeFish(username, fishName);
            return "Successfully removed fish: " + removedFish.getName();
        });
    }

    public String cleanTank() {
        return executeWithLock(() -> {
            aquariumInstance.cleanTank();
            return "Tank successfully cleaned!";
        });
    }

    public String feedFish(String userName) {
        return executeWithLock(() -> {
            int numFishFed = aquariumInstance.feedFish(userName);
            return "Fish Fed: " + numFishFed;
        });
    }

}
