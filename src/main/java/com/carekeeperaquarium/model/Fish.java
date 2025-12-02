package com.carekeeperaquarium.model;

import java.util.Random;
import java.util.UUID;

public class Fish {
    private enum Species {
        ANGEL_FISH("Angel Fish"),
        YELLOW_TANG("Yellow Tang"),
        NEON_GOBY("Neon Goby"),
        CLOWN_LOACH("Clown Loach"),
        SWORDTAIL("Swordtail"),
        CARDINAL_TETRA("Cardinal Tetra"),
        BRISTLENOSE_PLECO("Bristlenose Pleco"),
        BETTA("Betta"),
        TIGER_BARB("Tiger Barb"),
        WHITE_CLOUD_MOUNTAIN_MINNOW("White Cloud Mountain Minnow"),
        CONVICT_CICHLID("Convict Cichlid"),
        CLOWNFISH("Clownfish");
        
        private final String displayName;
        
        Species(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return this.displayName;
        }
    }
    private static final Species[] speciesArray = Species.values();
    private static final int NUMBER_OF_SPECIES = speciesArray.length;

    private static final int MAX_HEALTH = 100;
    private static final int MIN_HEALTH_TO_GROW = 50;
    private static final int DEFAULT_HUNGER_RATE = 3;

    private final UUID id;
    private String name;
    private final Species species;
    private int health;
    private final int hungerRate;
    private int age;
    private int size;
    private final double soilRate;

    // --- CONSTRUCTOR ---
    public Fish(String newName, Random random) {
        validateName(newName);
        this.id = UUID.randomUUID();
        this.name = newName.trim();
        this.species = speciesArray[random.nextInt(NUMBER_OF_SPECIES)];
        this.health = MAX_HEALTH;
        this.hungerRate = DEFAULT_HUNGER_RATE;
        this.age = 0;
        this.size = 1;
        this.soilRate = 0.1;
    }

    // --- ACCESSORS ---
    public UUID getId() { return this.id; }
    
    public String getName() { return this.name; }
    
    public String getSpecies() { return this.species.getDisplayName(); }

    public int getHealth() { return this.health; }

    public int getAge() { return this.age; }
    
    public int getSize() { return this.size; }

    public double getSoilRate() { return this.soilRate; }

    public boolean isDead() { return this.health <= 0; }

    public int getPointsWorth() { return this.size; }

    // --- MODIFIERS ---
    public void changeName(String newName) {
        validateName(newName);
        if (this.name.equals(newName))
            throw new IllegalArgumentException("New fish name can not be the same as old name");
        this.name = newName;
    }

    public void takeDamage() {
        this.health -= this.hungerRate;
        if (this.health < 0)
            this.health = 0;
    }

    public void grow() {
        if (this.health >= MIN_HEALTH_TO_GROW) {
            this.age++;
            if (this.age > this.size) {
                this.age = 0;
                this.size++;
            }
        }
    }

    public void feed(int food) {
        if (food > 0) {
            this.health += food;
            if (this.health > MAX_HEALTH)
                this.health = MAX_HEALTH;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fish otherFish = (Fish) obj;
        return this.id.equals(otherFish.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
            "ID: %s, Name: %s, Species: %s, Health: %d/%d, Age: %d" +
            ", Size: %d, Hunger Rate: %d, Soil Rate: %.2f", 
            this.id.toString(), this.name, this.getSpecies(),this.health, 
            Fish.MAX_HEALTH, this.age, this.size, this.hungerRate, this.soilRate
            );
    }

    // --- HELPERS ---
    private void validateName(String newName) throws IllegalArgumentException {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Fish name cannot be null or empty");
        }
        if (newName.trim().length() > 50) {
            throw new IllegalArgumentException("Fish name cannot exceed 50 characters");
        }
    }
}
