package com.carekeeperaquarium.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import com.carekeeperaquarium.exception.UserNotFound;

class AquariumStateTest {
    private AquariumState aquarium;
    private UserProfile user1;
    private UserProfile user2;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        aquarium = AquariumState.getAquarium();
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
    void testRecalculateCleanlinessWithFish() {
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
    void testRecalculateCleanlinessMultipleFish() {
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
    void testRecalculateCleanlinessWithLargerFish() {
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
}
