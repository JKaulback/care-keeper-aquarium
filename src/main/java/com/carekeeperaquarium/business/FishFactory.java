package com.carekeeperaquarium.business;

import java.util.concurrent.ThreadLocalRandom;

import com.carekeeperaquarium.model.Fish;

public class FishFactory {

    public static Fish createRandomFish() {
        return new Fish(ThreadLocalRandom.current());
    }

}
