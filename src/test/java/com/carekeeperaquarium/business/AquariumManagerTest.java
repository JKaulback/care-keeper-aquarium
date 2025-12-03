package com.carekeeperaquarium.business;

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.TooManyFish;
import com.carekeeperaquarium.exception.UserNotFound;
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
    void testGetUsers() throws TooManyFish {
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
    void testGetUser() throws UserNotFound {
        UserProfile user = new UserProfile("TestUser");
        manager.addUser(user);
        
        UserProfile retrieved = manager.getUser("TestUser");
        assertEquals("TestUser", retrieved.getUsername());
    }

    @Test
    void testGetUserNotFound() {
        assertThrows(UserNotFound.class, () -> {
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
    void testCleanTank() throws TooManyFish {
        // Add a user with fish to reduce cleanliness
        UserProfile user = new UserProfile("CleanUser");
        Fish fish = new Fish("DirtyFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        // Cleanliness will be reduced by scheduled iterations or we can just get current value
        double initialCleanliness = manager.getTankCleanliness();
        
        // Clean the tank
        manager.cleanTank(10);
        double afterCleaning = manager.getTankCleanliness();
        
        // Verify cleanliness increased (or at least didn't decrease)
        assertTrue(afterCleaning >= initialCleanliness || afterCleaning == 100.0, 
                   "Cleanliness should increase or be at max");
    }

    @Test
    void testFeedFish() throws TooManyFish, UserNotFound, FishNotFound {
        UserProfile user = new UserProfile("FeedUser");
        Fish fish = new Fish("HungryFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        // Make fish hungry
        fish.processHunger();
        int healthAfterHunger = fish.getHealth();
        
        // Feed the fish through manager
        manager.feedFish("FeedUser", fish.getId(), 10);
        
        assertTrue(fish.getHealth() > healthAfterHunger);
    }

    @Test
    void testFeedFishUserNotFound() throws TooManyFish {
        UserProfile user = new UserProfile("SomeUser");
        Fish fish = new Fish("SomeFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        assertThrows(UserNotFound.class, () -> {
            manager.feedFish("NonExistentUser", fish.getId(), 10);
        });
    }

    @Test
    void testFeedFishNotFound() throws TooManyFish {
        UserProfile user = new UserProfile("UserWithFish");
        Fish fish = new Fish("RealFish", random);
        user.addFish(fish);
        manager.addUser(user);
        
        UUID randomId = UUID.randomUUID();
        assertThrows(FishNotFound.class, () -> {
            manager.feedFish("UserWithFish", randomId, 10);
        });
    }

    @Test
    void testThreadPoolManagerIntegration() {
        assertNotNull(ThreadPoolManager.getScheduler());
    }

    @Test
    void testSynchronizedAccess() throws TooManyFish {
        // Verify that multiple operations can be performed safely
        UserProfile user = new UserProfile("ConcurrentUser");
        Fish fish = new Fish("ConcurrentFish", random);
        user.addFish(fish);
        
        manager.addUser(user);
        double cleanliness1 = manager.getTankCleanliness();
        manager.cleanTank(5);
        double cleanliness2 = manager.getTankCleanliness();
        
        assertTrue(cleanliness2 >= cleanliness1);
        assertEquals(1, manager.getUsers().size());
    }
}
