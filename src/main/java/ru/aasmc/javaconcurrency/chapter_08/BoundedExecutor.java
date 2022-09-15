package ru.aasmc.javaconcurrency.chapter_08;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

/**
 * This class can be used with an Executor that uses Unbounded queue.
 * To achieve proper saturation policy:
 * - use Executor with unbounded blocking queue
 * - set bound parameter to the number of tasks you want to allow executing concurrently.
 * BoundedExecutor.submitTask blocks if the bound limit is reached until
 * a worker thread releases the semaphore or is interrupted.
 */
public class BoundedExecutor {
    private final Executor exec;
    private final Semaphore semaphore;

    public BoundedExecutor(Executor exec, int bound) {
        this.exec = exec;
        this.semaphore = new Semaphore(bound);
    }

    public void submitTask(final Runnable task) throws InterruptedException {
        semaphore.acquire();
        try {
            exec.execute(() -> {
                try {
                    task.run();
                } finally {
                    semaphore.release();
                }
            });
        } catch (RejectedExecutionException e) {
            semaphore.release();
        }
    }
}
