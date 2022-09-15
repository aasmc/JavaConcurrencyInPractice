package ru.aasmc.javaconcurrency.chapter_08;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread with extended statistics capabilities, custom
 * UncaughtExceptionHandler and a proper name.
 */
public class MyAppThread extends Thread {
    private static final String DEFAULT_NAME = "MyAppThread";
    private static volatile boolean debugLifecycle = false;
    private static final AtomicInteger created = new AtomicInteger();
    private static final AtomicInteger alive = new AtomicInteger();
    private static final Logger log = Logger.getAnonymousLogger();

    public MyAppThread(Runnable r) {
        this(r, DEFAULT_NAME);
    }

    public MyAppThread(Runnable runnable, String name) {
        super(runnable, name + "-" + created.incrementAndGet());
        setUncaughtExceptionHandler((t, e) -> {
            log.log(Level.SEVERE, "UNCAUGHT in thread " + t.getName(), e);
        });
    }

    public void run() {
        // copy debug flag to ensure consistent value throughout
        boolean debug = debugLifecycle;
        if (debug) {
            log.log(Level.FINE, "Created " + getName());
        }
        try {
            alive.incrementAndGet();
            super.run();
        } finally {
            alive.decrementAndGet();
            if (debug) {
                log.log(Level.FINE, "Exiting " + getName());
            }
        }
    }

    public static int getThreadsCreated() {
        return created.get();
    }

    public static int getThreadsAlive() {
        return alive.get();
    }

    public static boolean getDebug() {
        return debugLifecycle;
    }

    public static void setDebug(boolean isDebugging) {
        debugLifecycle = isDebugging;
    }
}
