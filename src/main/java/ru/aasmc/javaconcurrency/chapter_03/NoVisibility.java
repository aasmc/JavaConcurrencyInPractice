package ru.aasmc.javaconcurrency.chapter_03;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that doesn't ensure visibility of private static members to all
 * threads due to compiler reordering. ReaderThread may block forever if it
 * never sees the [ready] member set to true, or it may print 0 if reordering
 * occurs and ready is set to true before number is set to 42.
 */
public class NoVisibility {
    private static boolean ready;
    private static int number;
    private static AtomicInteger counter = new AtomicInteger();
    private static final int SIZE = 200;

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }
            System.out.println(number + " From thread " + Thread.currentThread().getName());
            counter.incrementAndGet();
        }
    }

    public static void main(String[] args) {
        Thread[] t = new Thread[SIZE];
        for (int i = 0; i < SIZE; i++) {
            ReaderThread readerThread = new ReaderThread();
            readerThread.start();
            t[i] = readerThread;
        }
        new Thread() {
            @Override
            public void run() {
                number = 42;
                ready = true;
            }
        }.start();

        Arrays.stream(t)
                .forEach(thread -> {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Counter = " + counter.get());
    }
}
