package com.carekeeperaquarium.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FishTest {
    private Fish fish;

    @BeforeEach
    void setUp() {
        fish = new Fish("Nemo");
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
        Fish fish1 = new Fish("Fish1");
        Fish fish2 = new Fish("Fish2");
        assertNotEquals(fish1.getId(), fish2.getId());
        assertTrue(fish2.getId() > fish1.getId());
    }

    @Test
    void testChangeName() {
        assertTrue(fish.changeName("Dory"));
        assertEquals("Dory", fish.getName());
        
        assertFalse(fish.changeName("Dory"));
        assertEquals("Dory", fish.getName());
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
        fish.feed(10);
        assertEquals(100, fish.getHealth());
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
        // Should not increase size when health < 50
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
    void testEquals() {
        Fish sameFish = fish;
        Fish differentFish = new Fish("Other");
        
        assertTrue(fish.equals(sameFish));
        assertFalse(fish.equals(differentFish));
        assertFalse(fish.equals(null));
        assertFalse(fish.equals("Not a fish"));
    }

    @Test
    void testHashCode() {
        Fish fish1 = new Fish("Fish1");
        Fish fish2 = new Fish("Fish2");
        
        assertNotEquals(fish1.hashCode(), fish2.hashCode());
        assertEquals(fish1.hashCode(), fish1.hashCode());
    }
}
