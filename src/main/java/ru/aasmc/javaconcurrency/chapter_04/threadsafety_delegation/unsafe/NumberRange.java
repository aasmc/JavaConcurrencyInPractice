package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.unsafe;

import java.util.concurrent.atomic.AtomicInteger;

public class NumberRange {
    // Class holds invariant that lower <= upper
    private final AtomicInteger lower = new AtomicInteger(0);
    private final AtomicInteger upper = new AtomicInteger(0);

    public void setLower(int i) {
        // this is an unsafe operation "check then act"
        if (i < upper.get()) {
            throw new IllegalArgumentException("Can't set lower = " + i + " > upper");
        }
        lower.set(i);
    }

    public void setUpper(int i) {
        // this is an unsafe operation "check then act"
        if (i < lower.get()) {
            throw new IllegalArgumentException("Can't set upper = " + i + " < lower");
        }
        upper.set(i);
    }

    public boolean isInRange(int i) {
        return (i >= lower.get() && i <= upper.get());
    }
}
