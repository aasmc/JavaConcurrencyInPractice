package ru.aasmc.javaconcurrency.chapter_15;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicPseudoRandom extends PseudoRandom {
    private AtomicInteger seed;

    public AtomicPseudoRandom(int seed) {
        this.seed = new AtomicInteger(seed);
    }

    @Override
    public int nextInt(int n) {
        while (true) {
            int oldSeed = seed.get();
            int nextSeed = calculateNext(oldSeed);
            if (seed.compareAndSet(oldSeed, nextSeed)) {
                int remainder = oldSeed % n;
                return remainder > 0 ? remainder : remainder + n;
            }
        }
    }
}
