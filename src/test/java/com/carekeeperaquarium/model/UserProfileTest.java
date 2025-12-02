package com.carekeeperaquarium.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import com.carekeeperaquarium.exception.FishNotFound;
import com.carekeeperaquarium.exception.InsufficientPoints;

class UserProfileTest {
    private UserProfile profile;

    @BeforeEach
    void setUp() {
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
        fishList.add(new Fish("Fish1"));
        
        UserProfile customProfile = new UserProfile("Custom", 500, fishList);
        assertEquals("Custom", customProfile.getUserName());
        assertEquals(500, customProfile.getPoints());
        assertEquals(1, customProfile.getNumberOfFishOwned());
    }

    @Test
    void testChangeUserName() {
        assertTrue(profile.changeUserName("NewName"));
        assertEquals("NewName", profile.getUserName());
        
        assertFalse(profile.changeUserName("NewName"));
        assertEquals("NewName", profile.getUserName());
    }

    @Test
    void testAddFish() {
        Fish fish = new Fish("Nemo");
        profile.addFish(fish);
        
        assertEquals(1, profile.getNumberOfFishOwned());
    }

    @Test
    void testGetFishById() throws FishNotFound {
        Fish fish1 = new Fish("Fish1");
        Fish fish2 = new Fish("Fish2");
        
        profile.addFish(fish1);
        profile.addFish(fish2);
        
        Fish retrieved = profile.getFish(fish1.getId());
        assertEquals(fish1.getId(), retrieved.getId());
        assertEquals("Fish1", retrieved.getName());
    }

    @Test
    void testGetFishByIdThrowsException() {
        assertThrows(FishNotFound.class, () -> {
            profile.getFish(999);
        });
    }

    @Test
    void testRemoveFish() throws FishNotFound {
        Fish fish = new Fish("ToRemove");
        profile.addFish(fish);
        assertEquals(1, profile.getNumberOfFishOwned());
        
        profile.removeFish(fish.getId());
        assertEquals(0, profile.getNumberOfFishOwned());
    }

    @Test
    void testRemoveFishThrowsException() {
        assertThrows(FishNotFound.class, () -> {
            profile.removeFish(999);
        });
    }

    @Test
    void testGetFishReturnsCopy() {
        Fish fish = new Fish("Test");
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
    void testIncrementPointsWithFish() {
        Fish fish = new Fish("Test");
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
}
