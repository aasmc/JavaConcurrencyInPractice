package ru.aasmc.javaconcurrency.chapter_15;

import java.util.concurrent.atomic.AtomicReference;

public class CasNumberRange {
    private static class IntPair {
        // invariant: lower <= upper
        int lower;
        int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0, 0));

    public int getLower() {
        return values.get().lower;
    }

    public int getUpper() {
        return values.get().upper;
    }

    public void setLower(int i) {
        while (true) {
            IntPair oldValues = values.get();
            if (i > oldValues.upper) {
                throw new IllegalArgumentException("Can't set lower to " + i + " > upper");
            }
            IntPair newValues = new IntPair(i, oldValues.upper);
            if (values.compareAndSet(oldValues, newValues)) {
                return;
            }
        }
    }

    public void setUpper(int i) {
        while (true) {
            IntPair oldValues = values.get();
            if (i < oldValues.lower) {
                throw new IllegalArgumentException("Can't set upper " + i + " < lower");
            }
            IntPair newValues = new IntPair(oldValues.lower, i);
            if (values.compareAndSet(oldValues, newValues)) {
                return;
            }
        }
    }
}
