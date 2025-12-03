package com.carekeeperaquarium.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import com.carekeeperaquarium.exception.TooManyFish;
import com.carekeeperaquarium.model.AquariumState;
import com.carekeeperaquarium.model.Fish;
import com.carekeeperaquarium.model.UserProfile;

/**
 * Tests for AquariumManager.
 * Note: We avoid creating AquariumManager instances in setUp to prevent
 * multiple scheduler threads from being created.
 */
class AquariumManagerTest {
    private AquariumState aquarium;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        aquarium = AquariumState.getInstance();
        // Clear users manually since reset() is protected
        for (UserProfile user : aquarium.getUsers()) {
            aquarium.removeUser(user);
        }
    }

    @Test
    void testScheduledTasksAreInitialized() throws TooManyFish {
        // Add a user with a fish
        UserProfile user = new UserProfile("TestUser");
        Fish fish = new Fish("TestFish", random);
        
        user.addFish(fish);
        aquarium.addUser(user);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        
        // The scheduled task runs every minute, but we can manually trigger the iteration
        aquarium.runIteration();
        
        // After one iteration, cleanliness should decrease
        assertTrue(aquarium.getTankCleanliness() < initialCleanliness);
    }

    @Test
    void testAquariumStateIntegration() throws TooManyFish {
        UserProfile user = new UserProfile("IntegrationUser");
        Fish fish = new Fish("IntegrationFish", random);
        user.addFish(fish);
        
        aquarium.addUser(user);
        
        assertEquals(1, aquarium.getUsers().size());
        assertEquals(1, user.getFish().size());
    }

    @Test
    void testMultipleIterations() throws TooManyFish {
        UserProfile user = new UserProfile("MultiUser");
        Fish fish = new Fish("MultiFish", random);
        user.addFish(fish);
        aquarium.addUser(user);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        int initialPoints = user.getPoints();
        
        // Run multiple iterations
        aquarium.runIteration();
        aquarium.runIteration();
        
        assertTrue(aquarium.getTankCleanliness() < initialCleanliness);
        assertTrue(user.getPoints() > initialPoints);
    }

    @Test
    void testThreadPoolManagerIntegration() {
        // Verify that ThreadPoolManager is accessible
        assertNotNull(ThreadPoolManager.getScheduler());
    }
}
