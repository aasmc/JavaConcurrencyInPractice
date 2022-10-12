package ru.aasmc.javaconcurrency.chapter_14;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class OneShotLatch {

    private final Sync sync = new Sync();

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    private class Sync extends AbstractQueuedSynchronizer {
        @Override
        protected int tryAcquireShared(int ignored) {
            // success, if the latch is open (state == 1), failure otherwise
            return (getState() == 1) ? 1 : -1;
        }

        @Override
        protected boolean tryReleaseShared(int ignored) {
            setState(1); // the latch is open now
            return true; // other threads can acquire
        }
    }
}
