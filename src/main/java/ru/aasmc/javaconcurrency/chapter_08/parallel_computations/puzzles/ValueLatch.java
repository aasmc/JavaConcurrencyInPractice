package ru.aasmc.javaconcurrency.chapter_08.parallel_computations.puzzles;

import java.util.concurrent.CountDownLatch;

/**
 * A result-bearing latch that allows us to retrieve a
 * value that has been computed only once by a thread.
 *
 * If value is not yet available, the latch is blocked
 * until it becomes available.
 * @param <T>
 */
public class ValueLatch<T> {
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet() {
        return done.getCount() == 0;
    }

    public synchronized void setValue(T newValue) {
        if (!isSet()) {
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this) {
            return value;
        }
    }
}
