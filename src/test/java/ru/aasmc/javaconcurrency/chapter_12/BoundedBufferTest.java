package ru.aasmc.javaconcurrency.chapter_12;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BoundedBufferTest {

    private static final long LOOCKUP_DETECT_TIMEOUT = 1000;

    @Test
    void testIsEmptyWhenConstructed() {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
        assertTrue(buffer.isEmpty());
        assertFalse(buffer.isFull());
    }

    @Test
    void testIsFullAfterPuts() throws InterruptedException {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            buffer.put(i);
        }
        assertTrue(buffer.isFull());
        assertFalse(buffer.isEmpty());
    }

    /**
     * Tests that a thread blocks when trying to take from an empty buffer,
     * and that an InterruptedException is thrown if the taker thread is
     * interrupted.
     */
    @Test
    void testTakeBlocksWhenEmpty() {
        final BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
        Thread taker = new Thread(() -> {
            try {
                int unused = buffer.take();
                fail(); // if we get here, the test fails
            } catch (InterruptedException e) {
                // success
            }
        });

        try {
            taker.start();
            Thread.sleep(LOOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    void testConcurrentPutTake() throws Exception {
        int testPerThread = 100000; // tests per thread
        for (int cap = 10; cap <= 1000; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 32; pairs <= 128; pairs *= 2) {
                PutTakeTest test = new PutTakeTest(cap, pairs, testPerThread);
                System.out.println("Pairs: " + pairs + "\t");
                test.test();
                System.out.println("\t");
                Thread.sleep(1000);
                test.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        PutTakeTest.shutDown();
    }

    static class PutTakeTest {

        private static final ExecutorService pool =
                Executors.newCachedThreadPool();
        private final AtomicInteger putSum = new AtomicInteger(0);
        private final AtomicInteger takeSum = new AtomicInteger(0);
        private final CyclicBarrier barrier;
        private final BarrierTimer timer;
        private final BoundedBuffer<Integer> bb;
        private final int nTrials, nPairs;

        PutTakeTest(int capacity, int npairs, int ntrials) {
            this.timer = new BarrierTimer();
            this.bb = new BoundedBuffer<>(capacity);
            this.nTrials = ntrials;
            this.nPairs = npairs;
            this.barrier = new CyclicBarrier(npairs * 2 + 1, timer);
        }

        void test() {
            try {
                timer.clear();
                for (int i = 0; i < nPairs; ++i) {
                    pool.execute(new Producer());
                    pool.execute(new Consumer());
                }
                barrier.await(); // wait for all the threads to get ready (cycle 1)
                barrier.await(); // wait for all the threads to finish (cycle 2)
                long nsPerItem = timer.getTime() / (nPairs * (long)nTrials);
                System.out.println("Throughput: " + nsPerItem + " ns/item");
                assertEquals(putSum.get(), takeSum.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static void shutDown() {
            pool.shutdown();
        }

        class BarrierTimer implements Runnable {

            private boolean started;
            private long startTime;
            private long endTime;

            @Override
            public synchronized void run() {
                long t = System.nanoTime();
                if (!started) {
                    started = true;
                    startTime = t;
                } else {
                    endTime = t;
                }
            }

            public synchronized void clear() {
                started = false;
            }

            public synchronized long getTime() {
                return endTime - startTime;
            }
        }

        class Producer implements Runnable {
            @Override
            public void run() {
                try {
                    int seed = (this.hashCode() ^ (int) System.nanoTime());
                    int sum = 0;
                    // await arrival of all threads in cycle 1.
                    barrier.await();
                    for (int i = nTrials; i > 0; --i) {
                        bb.put(seed);
                        // compute the thread local sum
                        sum += seed;
                        seed = xorShiftRandomGenerator(seed);
                    }
                    putSum.getAndAdd(sum);
                    // await arrival of all threads in cycle 2.
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        class Consumer implements Runnable {
            @Override
            public void run() {
                try {
                    barrier.await();
                    int sum = 0;
                    for (int i = nTrials; i > 0; --i) {
                        sum += bb.take();
                    }
                    takeSum.getAndAdd(sum);
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * Produces a pseudo-random value based on [y].
         * This randomness is used to force the compiler not to
         * precompute answers in our multi-threaded computations.
         */
        int xorShiftRandomGenerator(int y) {
            y ^= (y << 6);
            y ^= (y >>> 21);
            y ^= (y << 7);
            return y;
        }
    }

}
