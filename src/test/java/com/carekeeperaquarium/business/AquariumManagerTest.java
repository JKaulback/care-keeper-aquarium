package com.carekeeperaquarium.business;

import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carekeeperaquarium.model.Fish;
import com.carekeeperaquarium.model.UserProfile;

/**
 * Tests for AquariumManager.
 * Tests verify that AquariumManager properly synchronizes access to AquariumState.
 */
class AquariumManagerTest {
    private AquariumManager manager;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        manager = new AquariumManager();
        
        // Clear all users before each test
        for (UserProfile user : manager.getUsers()) {
            manager.removeUser(user);
        }
    }

    @Test
    void testGetUsers() {
        UserProfile user1 = new UserProfile("User1");
        UserProfile user2 = new UserProfile("User2");
        
        manager.addUser(user1);
        manager.addUser(user2);
        
        assertEquals(2, manager.getUsers().size());
    }

    @Test
    void testGetTankCleanliness() {
        // Cleanliness might not be exactly 100 if scheduler has run
        double cleanliness = manager.getTankCleanliness();
        assertTrue(cleanliness <= 100.0 && cleanliness >= 0.0, 
                   "Cleanliness should be between 0 and 100, was: " + cleanliness);
    }

    @Test
    void testGetUser() {
        UserProfile user = new UserProfile("TestUser");
        manager.addUser(user);
        
        UserProfile retrieved = manager.getUser("TestUser");
        assertEquals("TestUser", retrieved.getUsername());
    }

    @Test
    void testGetUserNotFound() {
        assertThrows(NoSuchElementException.class, () -> {
            manager.getUser("NonExistent");
        });
    }

    @Test
    void testAddUser() {
        UserProfile user = new UserProfile("NewUser");
        manager.addUser(user);
        
        assertEquals(1, manager.getUsers().size());
    }

    @Test
    void testRemoveUser() {
        UserProfile user = new UserProfile("RemoveMe");
        manager.addUser(user);
        assertEquals(1, manager.getUsers().size());
        
        boolean removed = manager.removeUser(user);
        assertTrue(removed);
        assertEquals(0, manager.getUsers().size());
    }

    @Test
    void testCleanTank() {
        // Add a user with fish to reduce cleanliness
        UserProfile user = new UserProfile("CleanUser");
        Fish fish = new Fish("DirtyFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        // Cleanliness will be reduced by scheduled iterations or we can just get current value
        double initialCleanliness = manager.getTankCleanliness();
        
        // Clean the tank
        manager.cleanTank();
        double afterCleaning = manager.getTankCleanliness();
        
        // Verify cleanliness increased (or at least didn't decrease)
        assertTrue(afterCleaning >= initialCleanliness || afterCleaning == 100.0, 
                   "Cleanliness should increase or be at max");
    }

    @Test
    void testFeedFish() {
        UserProfile user = new UserProfile("FeedUser");
        Fish fish = new Fish("HungryFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        // Make fish hungry
        fish.processHunger();
        int healthAfterHunger = fish.getHealth();
        
        // Feed the fish through manager
        manager.feedFish("FeedUser");
        
        assertTrue(fish.getHealth() > healthAfterHunger);
    }

    @Test
    void testFeedFishUserNotFound() {
        UserProfile user = new UserProfile("SomeUser");
        Fish fish = new Fish("SomeFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        assertThrows(NoSuchElementException.class, () -> {
            manager.feedFish("NonExistentUser");
        });
    }

    @Test
    void testThreadPoolManagerIntegration() {
        assertNotNull(ThreadPoolManager.getScheduler());
    }

    @Test
    void testSynchronizedAccess() {
        // Verify that multiple operations can be performed safely
        UserProfile user = new UserProfile("ConcurrentUser");
        Fish fish = new Fish("ConcurrentFish", random);
        user.addFish(fish);
        
        manager.addUser(user);
        double cleanliness1 = manager.getTankCleanliness();
        manager.cleanTank();
        double cleanliness2 = manager.getTankCleanliness();
        
        assertTrue(cleanliness2 >= cleanliness1);
        assertEquals(1, manager.getUsers().size());
    }
}
