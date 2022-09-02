package ru.aasmc.javaconcurrency.chapter_07.cancellation;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;

public class PrimeProducer extends Thread {
    private final BlockingQueue<BigInteger> queue;

    public PrimeProducer(BlockingQueue<BigInteger> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            BigInteger p = BigInteger.ONE;
            // Interruption can be detected at this point
            while (!Thread.currentThread().isInterrupted()) {
                // and at this point (blocking method put checks for interruption)
                queue.put(p = p.nextProbablePrime());
            }
        } catch (InterruptedException e) {
            // allow the thread to exit
        }
    }

    public void cancel() {
        interrupt();
    }
}
