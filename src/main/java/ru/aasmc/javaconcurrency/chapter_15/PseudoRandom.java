package ru.aasmc.javaconcurrency.chapter_15;

import java.util.Random;

public abstract class PseudoRandom {
    public abstract int nextInt(int n);

    protected int calculateNext(int seed) {
        // STUB method
        return new Random(seed).nextInt();
    }
}
