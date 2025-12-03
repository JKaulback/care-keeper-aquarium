package com.carekeeperaquarium.model;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carekeeperaquarium.exception.TooManyFish;
import com.carekeeperaquarium.exception.UserNotFound;

class AquariumStateTest {
    private AquariumState aquarium;
    private UserProfile user1;
    private UserProfile user2;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        aquarium = AquariumState.getInstance();
        aquarium.reset(); // Reset state before each test
        user1 = new UserProfile("User1");
        user2 = new UserProfile("User2");
    }

    @Test
    void testConstructor() {
        assertNotNull(aquarium);
        assertEquals(100.0, aquarium.getTankCleanliness());
        assertEquals(0, aquarium.getUsers().size());
    }

    @Test
    void testAddUser() {
        aquarium.addUser(user1);
        assertEquals(1, aquarium.getUsers().size());
        
        aquarium.addUser(user2);
        assertEquals(2, aquarium.getUsers().size());
    }

    @Test
    void testRemoveUser() {
        aquarium.addUser(user1);
        aquarium.addUser(user2);
        assertEquals(2, aquarium.getUsers().size());
        
        aquarium.removeUser(user1);
        assertEquals(1, aquarium.getUsers().size());
    }

    @Test
    void testGetUserByName() throws UserNotFound {
        aquarium.addUser(user1);
        aquarium.addUser(user2);
        
        UserProfile retrieved = aquarium.getUser("User1");
        assertEquals("User1", retrieved.getUserName());
    }

    @Test
    void testGetUserThrowsException() {
        aquarium.addUser(user1);
        
        assertThrows(UserNotFound.class, () -> {
            aquarium.getUser("NonExistent");
        });
    }

    @Test
    void testGetUsersReturnsCopy() {
        aquarium.addUser(user1);
        
        var usersList = aquarium.getUsers();
        usersList.clear();
        
        // Original list should still have the user
        assertEquals(1, aquarium.getUsers().size());
    }

    @Test
    void testCleanTank() {
        aquarium.recalculateCleanliness(); // Reduce from 100
        double cleanlinessAfterSoil = aquarium.getTankCleanliness();
        
        aquarium.cleanTank(1);
        assertEquals(cleanlinessAfterSoil + 1, aquarium.getTankCleanliness());
    }

    @Test
    void testCleanTankMaxCap() {
        aquarium.cleanTank(50);
        assertEquals(100.0, aquarium.getTankCleanliness());
    }

    @Test
    void testRecalculateCleanlinessNoUsers() {
        double initialCleanliness = aquarium.getTankCleanliness();
        aquarium.recalculateCleanliness();
        
        // Should decrease by base tank soil value (1)
        assertEquals(initialCleanliness - 1, aquarium.getTankCleanliness());
    }

    @Test
    void testRecalculateCleanlinessWithFish() throws TooManyFish {
        Fish fish = new Fish("Test", random);
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        aquarium.recalculateCleanliness();
        
        // Should decrease by 1 + (fish.size * fish.soilRate)
        // = 1 + (1 * 0.1) = 1.1
        assertEquals(initialCleanliness - 1.1, aquarium.getTankCleanliness(), 0.001);
    }

    @Test
    void testRecalculateCleanlinessMultipleFish() throws TooManyFish {
        Fish fish1 = new Fish("Fish1", random);
        Fish fish2 = new Fish("Fish2", random);
        user1.addFish(fish1);
        user1.addFish(fish2);
        aquarium.addUser(user1);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        aquarium.recalculateCleanliness();
        
        // 1 + (1 * 0.1) + (1 * 0.1) = 1.2
        assertEquals(initialCleanliness - 1.2, aquarium.getTankCleanliness(), 0.001);
    }

    @Test
    void testRecalculateCleanlinessMinimumZero() {
        // Soil the tank many times
        for (int i = 0; i < 200; i++) {
            aquarium.recalculateCleanliness();
        }
        
        assertEquals(0.0, aquarium.getTankCleanliness());
    }

    @Test
    void testRecalculateCleanlinessStopsAtZero() {
        // Soil until zero
        for (int i = 0; i < 200; i++) {
            aquarium.recalculateCleanliness();
        }
        
        assertEquals(0.0, aquarium.getTankCleanliness());
        
        // Further soiling should not go negative
        aquarium.recalculateCleanliness();
        assertEquals(0.0, aquarium.getTankCleanliness());
    }

    @Test
    void testRecalculateCleanlinessWithLargerFish() throws TooManyFish {
        Fish fish = new Fish("Growing", random);
        fish.grow();
        fish.grow(); // Size = 2
        
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        aquarium.recalculateCleanliness();
        
        // 1 + (2 * 0.1) = 1.2
        assertEquals(initialCleanliness - 1.2, aquarium.getTankCleanliness(), 0.001);
    }

    @Test
    void testAddUserWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.addUser(null);
        });
    }

    @Test
    void testAddDuplicateUser() {
        aquarium.addUser(user1);
        
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.addUser(user1);
        });
    }

    @Test
    void testRemoveUserReturnsTrue() {
        aquarium.addUser(user1);
        boolean result = aquarium.removeUser(user1);
        
        assertTrue(result);
        assertEquals(0, aquarium.getUsers().size());
    }

    @Test
    void testRemoveUserReturnsFalse() {
        boolean result = aquarium.removeUser(user1);
        
        assertFalse(result);
    }

    @Test
    void testRemoveUserWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.removeUser(null);
        });
    }

    @Test
    void testGetUserWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.getUser(null);
        });
    }

    @Test
    void testGetUserWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.getUser("  ");
        });
    }

    @Test
    void testCleanTankWithNegativeValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            aquarium.cleanTank(-10);
        });
    }

    @Test
    void testProcessHunger() throws TooManyFish {
        Fish fish = new Fish("HungryFish", random);
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        int initialHealth = fish.getHealth();
        aquarium.processHunger();
        
        assertTrue(fish.getHealth() < initialHealth, "Fish health should decrease after hunger processing");
    }

    @Test
    void testProcessFishGrowth() throws TooManyFish {
        Fish fish = new Fish("GrowingFish", random);
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        int initialAge = fish.getAge();
        aquarium.processFishGrowth();
        
        assertEquals(initialAge + 1, fish.getAge(), "Fish age should increase after growth processing");
    }

    @Test
    void testProcessPointAwards() throws TooManyFish {
        Fish fish = new Fish("PointFish", random);
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        int initialPoints = user1.getPoints();
        aquarium.processPointAwards();
        
        assertTrue(user1.getPoints() > initialPoints, "User points should increase after point awards");
    }

    @Test
    void testRunIteration() throws TooManyFish {
        Fish fish = new Fish("TestFish", random);
        user1.addFish(fish);
        aquarium.addUser(user1);
        
        double initialCleanliness = aquarium.getTankCleanliness();
        int initialPoints = user1.getPoints();
        int initialAge = fish.getAge();
        
        aquarium.runIteration();
        
        // Verify all iteration steps were executed
        assertTrue(aquarium.getTankCleanliness() < initialCleanliness, "Cleanliness should decrease");
        assertTrue(user1.getPoints() > initialPoints, "Points should increase");
        assertEquals(initialAge + 1, fish.getAge(), "Fish age should increase");
    }

    @Test
    void testRunIterationWithMultipleUsers() throws TooManyFish {
        Fish fish1 = new Fish("Fish1", random);
        Fish fish2 = new Fish("Fish2", random);
        
        user1.addFish(fish1);
        user2.addFish(fish2);
        
        aquarium.addUser(user1);
        aquarium.addUser(user2);
        
        int initialPoints1 = user1.getPoints();
        int initialPoints2 = user2.getPoints();
        
        aquarium.runIteration();
        
        assertTrue(user1.getPoints() > initialPoints1, "User1 points should increase");
        assertTrue(user2.getPoints() > initialPoints2, "User2 points should increase");
    }

    @Test
    void testRunIterationWithNoUsers() {
        double initialCleanliness = aquarium.getTankCleanliness();
        
        aquarium.runIteration();
        
        // Should still decrease by base soil value
        assertTrue(aquarium.getTankCleanliness() < initialCleanliness);
    }
}
