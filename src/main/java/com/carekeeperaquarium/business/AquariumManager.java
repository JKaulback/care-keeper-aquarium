package com.carekeeperaquarium.business;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.carekeeperaquarium.model.AquariumState;

public class AquariumManager {
    private final AquariumState aquariumInstance;
    
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


    
}
