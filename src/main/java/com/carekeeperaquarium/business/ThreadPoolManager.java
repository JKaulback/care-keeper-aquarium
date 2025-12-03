package com.carekeeperaquarium.business;

import java.util.concurrent.ScheduledExecutorService;

public class ThreadPoolManager {
    private static final ScheduledExecutorService scheduler = 
        java.util.concurrent.Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        private ThreadPoolManager() {
            // Private constructor to prevent instantiation
        }

        public static ScheduledExecutorService getScheduler() {
            return scheduler;
        }

        public static void shutdown() {
            scheduler.shutdown();
        }
}
