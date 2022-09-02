package ru.aasmc.javaconcurrency.chapter_07.cancellation;

import java.util.concurrent.*;

public class FutureCancel {

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public static void timedRun(Runnable r,
                                long timeout,
                                TimeUnit unit) throws InterruptedException {
        Future<?> task = threadPool.submit(r);
        try {
            task.get(timeout, unit);
        } catch (TimeoutException e) {
            // task will be cancelled below
        } catch (ExecutionException e) {
            // exception thrown in task, rethrow it
            throw launderThrowable(e);
        } finally {
            // Harmless if task already completed.
            task.cancel(true); // interrupt if running
        }
    }

    /**
     * If the Throwable is an Error, throw it, if it a RuntimeException return it,
     * otherwise throw IllegalStateException.
     */
    private static RuntimeException launderThrowable(Throwable cause) {
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        } else if (cause instanceof Error) {
            throw (Error) cause;
        } else {
            throw new IllegalStateException("Not checked", cause);
        }
    }
}
