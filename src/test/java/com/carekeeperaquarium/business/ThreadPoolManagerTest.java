package com.carekeeperaquarium.business;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Tests for ThreadPoolManager.
 * Note: We avoid testing scheduler execution in unit tests to prevent
 * interference with the shared static scheduler.
 */
class ThreadPoolManagerTest {

    @Test
    void testGetScheduler() {
        ScheduledExecutorService scheduler = ThreadPoolManager.getScheduler();
        assertNotNull(scheduler);
    }

    @Test
    void testSchedulerIsSingleton() {
        ScheduledExecutorService scheduler1 = ThreadPoolManager.getScheduler();
        ScheduledExecutorService scheduler2 = ThreadPoolManager.getScheduler();
        assertTrue(scheduler1 == scheduler2, "Scheduler should be a singleton");
    }

    @Test
    void testGetClientExecutor() {
        ExecutorService clientExecutor = ThreadPoolManager.getClientExecutor();
        assertNotNull(clientExecutor, "Client executor should not be null");
    }

    @Test
    void testClientExecutorIsSingleton() {
        ExecutorService executor1 = ThreadPoolManager.getClientExecutor();
        ExecutorService executor2 = ThreadPoolManager.getClientExecutor();
        assertTrue(executor1 == executor2, "Client executor should be a singleton");
    }

    @Test
    void testClientExecutorIsNotShutdown() {
        ExecutorService clientExecutor = ThreadPoolManager.getClientExecutor();
        assertFalse(clientExecutor.isShutdown(), "Client executor should not be shutdown initially");
    }

    @Test
    void testSchedulerIsNotShutdown() {
        ScheduledExecutorService scheduler = ThreadPoolManager.getScheduler();
        assertFalse(scheduler.isShutdown(), "Scheduler should not be shutdown initially");
    }
}
