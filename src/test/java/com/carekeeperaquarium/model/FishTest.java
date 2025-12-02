package com.carekeeperaquarium.model;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FishTest {
    private Fish fish;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random();
        fish = new Fish("Nemo", random);
    }

    @Test
    void testConstructor() {
        assertNotNull(fish);
        assertEquals("Nemo", fish.getName());
        assertEquals(100, fish.getHealth());
        assertEquals(0, fish.getAge());
        assertEquals(1, fish.getSize());
        assertEquals(0.1, fish.getSoilRate());
        assertNotNull(fish.getSpecies());
    }

    @Test
    void testGetId() {
        Fish fish1 = new Fish("Fish1", random);
        Fish fish2 = new Fish("Fish2", random);
        assertNotEquals(fish1.getId(), fish2.getId());
        assertNotNull(fish1.getId());
        assertNotNull(fish2.getId());
    }

    @Test
    void testChangeName() {
        fish.changeName("Dory");
        assertEquals("Dory", fish.getName());
    }

    @Test
    void testChangeNameToSameThrowsException() {
        fish.changeName("Dory");
        assertThrows(IllegalArgumentException.class, () -> {
            fish.changeName("Dory");
        });
    }

    @Test
    void testIsDead() {
        assertFalse(fish.isDead());
        
        // Reduce health to 0
        for (int i = 0; i < 34; i++) {
            fish.takeDamage();
        }
        assertTrue(fish.isDead());
    }

    @Test
    void testTakeDamage() {
        int initialHealth = fish.getHealth();
        fish.takeDamage();
        assertEquals(initialHealth - 3, fish.getHealth());
        
        // Test health doesn't go below 0
        for (int i = 0; i < 50; i++) {
            fish.takeDamage();
        }
        assertEquals(0, fish.getHealth());
    }

    @Test
    void testFeed() {
        fish.takeDamage();
        int healthAfterDamage = fish.getHealth();
        fish.feed(1);
        assertEquals(healthAfterDamage + 1, fish.getHealth());
    }

    @Test
    void testFeedMaxCap() {
        fish.feed(50);
        assertEquals(100, fish.getHealth());
    }

    @Test
    void testFeedWithZeroThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            fish.feed(0);
        });
    }

    @Test
    void testFeedWithNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            fish.feed(-5);
        });
    }

    @Test
    void testGrowWhenHealthy() {
        fish.grow();
        assertEquals(1, fish.getAge());
        assertEquals(1, fish.getSize());
        
        // Fish should grow when age > size and health >= 50
        fish.grow();
        assertEquals(0, fish.getAge());
        assertEquals(2, fish.getSize());
    }

    @Test
    void testGrowWhenUnhealthy() {
        // Reduce health below 50
        for (int i = 0; i < 18; i++) {
            fish.takeDamage();
        }
        assertTrue(fish.getHealth() < 50);
        
        fish.grow();
        fish.grow();
        // Should not grow at all when health < 50
        assertEquals(0, fish.getAge());
        assertEquals(1, fish.getSize());
    }

    @Test
    void testGetPointsWorth() {
        assertEquals(1, fish.getPointsWorth());
        
        fish.grow();
        fish.grow();
        assertEquals(2, fish.getPointsWorth());
    }

    @Test
    void testGrowStopsAtMaxSize() {
        // Grow fish to max size (10)
        // Each grow call increments age, and when age > size, size increases by 1
        // So we need size increments from 1 to 10 = 9 growth cycles
        // Each cycle needs (size + 1) grow calls
        for (int i = 0; i < 60; i++) {
            fish.grow();
        }
        
        assertEquals(10, fish.getSize());
        
        // Try to grow beyond max
        for (int i = 0; i < 10; i++) {
            fish.grow();
        }
        assertEquals(10, fish.getSize());
    }

    @Test
    void testEquals() {
        Fish sameFish = fish;
        Fish differentFish = new Fish("Other", random);
        
        assertEquals(fish, sameFish);
        assertNotEquals(fish, differentFish);
        assertNotEquals(fish, null);
        assertNotEquals(fish, "Not a fish");
    }

    @Test
    void testHashCode() {
        Fish fish1 = new Fish("Fish1", random);
        Fish fish2 = new Fish("Fish2", random);
        
        assertNotEquals(fish1.hashCode(), fish2.hashCode());
        assertEquals(fish1.hashCode(), fish1.hashCode());
    }

    @Test
    void testConstructorWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Fish(null, random);
        });
    }

    @Test
    void testConstructorWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Fish("", random);
        });
    }

    @Test
    void testConstructorWithBlankName() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Fish("   ", random);
        });
    }

    @Test
    void testConstructorWithTooLongName() {
        String longName = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> {
            new Fish(longName, random);
        });
    }

    @Test
    void testConstructorTrimsName() {
        Fish fishWithSpaces = new Fish("  Nemo  ", random);
        assertEquals("Nemo", fishWithSpaces.getName());
    }

    @Test
    void testChangeNameWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            fish.changeName(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            fish.changeName("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            fish.changeName("   ");
        });
    }

    @Test
    void testToString() {
        String result = fish.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Nemo"));
        assertTrue(result.contains("Health: 100/100"));
        assertTrue(result.contains("Size: 1"));
        assertTrue(result.contains("Age: 0"));
    }
}
