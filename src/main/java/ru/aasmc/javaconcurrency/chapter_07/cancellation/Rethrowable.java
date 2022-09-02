package ru.aasmc.javaconcurrency.chapter_07.cancellation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Rethrowable {

    private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(5);

    public static void timedRun(
            final Runnable r,
            long timeout,
            TimeUnit unit) throws InterruptedException {

        class RethrowableTask implements Runnable {
            // must be volatile to be safely published from task thread to the timedRun thread.
            private volatile Throwable t;

            public void run() {
                try {
                    r.run();
                } catch (Throwable t) {
                    this.t = t;
                }
            }

            void rethrow() {
                if (t != null) {
                    throw launderThrowable(t);
                }
            }

            /**
             * If the Throwable is an Error, throw it, if it a RuntimeException return it,
             * otherwise throw IllegalStateException.
             */
            private RuntimeException launderThrowable(Throwable cause) {
                if (cause instanceof RuntimeException) {
                    return (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException("Not checked", cause);
                }
            }
        }

        RethrowableTask task = new RethrowableTask();
        final Thread taskThread = new Thread(task);
        taskThread.start();
        // interrupt the task thread after specified amount of time
        cancelExec.schedule(taskThread::interrupt, timeout, unit);
        // wait for the task to finish for the specified amount of time
        taskThread.join(unit.toMillis(timeout));
        // if the task threw an exception in another thread, it will be safely rethrown
        // in the caller thread.
        task.rethrow();
    }
}
