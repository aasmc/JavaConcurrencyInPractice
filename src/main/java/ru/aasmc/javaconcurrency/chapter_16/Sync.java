package ru.aasmc.javaconcurrency.chapter_16;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

class FutureTaskStub<V> extends FutureTask<V> {
    public FutureTaskStub(Callable<V> callable) {
        super(callable);
    }

    public FutureTaskStub(Runnable runnable, V result) {
        super(runnable, result);
    }

    private final class Sync extends AbstractQueuedSynchronizer {
        private static final int RUNNING = 1, RAN = 2, CANCELLED = 3;
        private V result;
        private Exception exception;

        void innerSet(V v) {
            while (true) {
                int s = getState();
                if (ranOrCancelled(s)) {
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    break;
                }
            }
            result = v;
            // happens before acquireSharedInterruptibly(), which means result will be
            // available after calling acquireSharedInterruptibly(), even though it is not
            // guarded by a lock and is not a volatile variable.
            releaseShared(0);
            done();
        }

        private boolean ranOrCancelled(int s) {
            // STUB method
            return false;
        }

        V innerGet() throws InterruptedException, ExecutionException {
            acquireSharedInterruptibly(0);
            if (getState() == CANCELLED) {
                throw new CancellationException();
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }

    }
}


