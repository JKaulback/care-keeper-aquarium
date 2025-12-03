package com.carekeeperaquarium.model;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.InsufficientPoints;
import com.carekeeperaquarium.exception.TooManyFish;

class UserProfileTest {
    private UserProfile profile;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        profile = new UserProfile("TestUser");
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(profile);
        assertEquals("TestUser", profile.getUserName());
        assertEquals(100, profile.getPoints());
        assertEquals(0, profile.getNumberOfFishOwned());
    }

    @Test
    void testParameterizedConstructor() {
        ArrayList<Fish> fishList = new ArrayList<>();
        fishList.add(new Fish("Fish1", random));
        
        UserProfile customProfile = new UserProfile("Custom", 500, fishList);
        assertEquals("Custom", customProfile.getUserName());
        assertEquals(500, customProfile.getPoints());
        assertEquals(1, customProfile.getNumberOfFishOwned());
    }

    @Test
    void testChangeUserName() {
        profile.changeUserName("NewName");
        assertEquals("NewName", profile.getUserName());
    }

    @Test
    void testChangeUserNameToSameThrowsException() {
        profile.changeUserName("NewName");
        assertThrows(IllegalArgumentException.class, () -> {
            profile.changeUserName("NewName");
        });
    }

    @Test
    void testAddFish() throws TooManyFish {
        Fish fish = new Fish("Nemo", random);
        profile.addFish(fish);
        
        assertEquals(1, profile.getNumberOfFishOwned());
    }

    @Test
    void testGetFishById() throws FishNotFound, TooManyFish {
        Fish fish1 = new Fish("Fish1", random);
        Fish fish2 = new Fish("Fish2", random);
        
        profile.addFish(fish1);
        profile.addFish(fish2);
        
        Fish retrieved = profile.getFish(fish1.getId());
        assertEquals(fish1.getId(), retrieved.getId());
        assertEquals("Fish1", retrieved.getName());
    }

    @Test
    void testGetFishByIdThrowsException() {
        assertThrows(FishNotFound.class, () -> {
            profile.getFish(java.util.UUID.randomUUID());
        });
    }

    @Test
    void testRemoveFish() throws FishNotFound, TooManyFish {
        Fish fish = new Fish("ToRemove", random);
        profile.addFish(fish);
        assertEquals(1, profile.getNumberOfFishOwned());
        
        profile.removeFish(fish.getId());
        assertEquals(0, profile.getNumberOfFishOwned());
    }

    @Test
    void testRemoveFishThrowsException() {
        assertThrows(FishNotFound.class, () -> {
            profile.removeFish(java.util.UUID.randomUUID());
        });
    }

    @Test
    void testGetFishReturnsCopy() throws TooManyFish {
        Fish fish = new Fish("Test", random);
        profile.addFish(fish);
        
        ArrayList<Fish> fishList = profile.getFish();
        fishList.clear();
        
        // Original list should still have the fish
        assertEquals(1, profile.getNumberOfFishOwned());
    }

    @Test
    void testIncrementPointsNoFish() {
        int initialPoints = profile.getPoints();
        profile.incrementPoints();
        assertEquals(initialPoints + 1, profile.getPoints());
    }

    @Test
    void testIncrementPointsWithFish() throws TooManyFish {
        Fish fish = new Fish("Test", random);
        profile.addFish(fish);
        
        int initialPoints = profile.getPoints();
        int expectedIncrease = 1 + fish.getPointsWorth();
        
        profile.incrementPoints();
        assertEquals(initialPoints + expectedIncrease, profile.getPoints());
    }

    @Test
    void testSpendPoints() throws InsufficientPoints {
        profile.spendPoints(50);
        assertEquals(50, profile.getPoints());
    }

    @Test
    void testSpendPointsInsufficientThrowsException() {
        assertThrows(InsufficientPoints.class, () -> {
            profile.spendPoints(200);
        });
    }

    @Test
    void testSpendPointsExactAmount() throws InsufficientPoints {
        profile.spendPoints(100);
        assertEquals(0, profile.getPoints());
    }

    @Test
    void testSpendNegativePointsThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            profile.spendPoints(-10);
        });
    }

    @Test
    void testAddNullFishThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            profile.addFish(null);
        });
    }

    @Test
    void testAddFishWhenFullThrowsException() throws TooManyFish {
        // Add 10 fish to reach max capacity
        for (int i = 0; i < 10; i++) {
            profile.addFish(new Fish("Fish" + i, random));
        }
        
        assertTrue(profile.isFull());
        
        assertThrows(TooManyFish.class, () -> {
            profile.addFish(new Fish("TooMany", random));
        });
    }

    @Test
    void testIsFull() throws TooManyFish {
        assertFalse(profile.isFull());
        
        for (int i = 0; i < 10; i++) {
            profile.addFish(new Fish("Fish" + i, random));
        }
        
        assertTrue(profile.isFull());
    }

    @Test
    void testGetMaxFish() {
        assertEquals(10, profile.getMaxFish());
    }

    @Test
    void testHasDeadFish() throws TooManyFish {
        assertFalse(profile.hasDeadFish());
        
        Fish fish = new Fish("Dying", random);
        profile.addFish(fish);
        
        // Kill the fish
        for (int i = 0; i < 34; i++) {
            fish.processHunger();
        }
        
        assertTrue(profile.hasDeadFish());
    }

    @Test
    void testConstructorWithNullUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile(null);
        });
    }

    @Test
    void testConstructorWithEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile("");
        });
    }

    @Test
    void testConstructorWithBlankUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile("   ");
        });
    }

    @Test
    void testConstructorWithNegativePoints() {
        ArrayList<Fish> fishList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile("User", -10, fishList);
        });
    }

    @Test
    void testConstructorWithTooManyFish() {
        ArrayList<Fish> fishList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            fishList.add(new Fish("Fish" + i, random));
        }
        
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile("User", 100, fishList);
        });
    }

    @Test
    void testConstructorWithNullFishInList() {
        ArrayList<Fish> fishList = new ArrayList<>();
        fishList.add(new Fish("Fish1", random));
        fishList.add(null);
        
        assertThrows(IllegalArgumentException.class, () -> {
            new UserProfile("User", 100, fishList);
        });
    }

    @Test
    void testToString() throws TooManyFish {
        String result = profile.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("TestUser"));
        assertTrue(result.contains("Points: 100"));
        
        // Add a fish and check it appears
        Fish fish = new Fish("Nemo", random);
        profile.addFish(fish);
        
        result = profile.toString();
        assertTrue(result.contains("Nemo"));
    }

    @Test
    void testEquals() {
        UserProfile profile1 = new UserProfile("User1");
        UserProfile profile2 = new UserProfile("User1");
        UserProfile profile3 = new UserProfile("User2");
        
        assertEquals(profile1, profile2);
        assertNotEquals(profile1, profile3);
        assertNotEquals(profile1, null);
        assertNotEquals(profile1, "Not a profile");
    }

    @Test
    void testHashCode() {
        UserProfile profile1 = new UserProfile("User1");
        UserProfile profile2 = new UserProfile("User1");
        
        assertEquals(profile1.hashCode(), profile2.hashCode());
    }
}
