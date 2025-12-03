package com.carekeeperaquarium.business;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
}
