package com.carekeeperaquarium.model;

import java.util.Random;

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
    
    private static int nextId = 1;
    private static final Random random = new Random();
    private static final Species[] speciesArray = Species.values();
    private static final int NUMBER_OF_SPECIES = speciesArray.length;

    private final int id;
    private String name;
    private final Species species;
    private int health;
    private final int hungerRate;
    private int age;
    private int size;
    private final double soilRate;

    // --- CONSTRUCTOR ---
    public Fish(String newName) {
        this.id = Fish.nextId++;
        this.name = newName;
        this.species = speciesArray[random.nextInt(NUMBER_OF_SPECIES)];
        this.health = 100;
        this.hungerRate = 3;
        this.age = 0;
        this.size = 1;
        this.soilRate = 0.1;
    }

    // --- GETTERS ---
    public int getId() { return this.id; }
    
    public String getName() { return this.name; }
    
    public String getSpecies() { return this.species.getDisplayName(); }

    public int getHealth() { return this.health; }

    public int getAge() { return this.age; }
    
    public int getSize() { return this.size; }

    public double getSoilRate() { return this.soilRate; }

    public boolean isDead() {
        return this.health <= 0;      
    }

    // --- MODIFIERS ---
    public boolean changeName(String newName) {
        if (this.name.equals(newName))
            return false;
        this.name = newName;
        return true;
    }

    public void takeDamage() {
        this.health -= this.hungerRate;
        if (this.health < 0)
            this.health = 0;
    }

    public void grow() {
        this.age++;
        if (this.age >= this.size && this.health >= 50) {
            this.age = 0;
            this.size++;
        }
    }

    public void feed(int food) {
        this.health += food;
    }

}
