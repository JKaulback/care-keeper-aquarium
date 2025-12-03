package com.carekeeperaquarium.business;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPoolManager {
    private static final ScheduledExecutorService scheduler = 
        java.util.concurrent.Executors.newScheduledThreadPool(2);
    
    public static final ExecutorService clientExecutor = 
        java.util.concurrent.Executors.newFixedThreadPool(10);

    private ThreadPoolManager() {
        // Private constructor to prevent instantiation
    }

    public static ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public static ExecutorService getClientExecutor() {
        return clientExecutor;
    }

    public static void shutdown() {
        scheduler.shutdown();
        clientExecutor.shutdown();
    }
}
