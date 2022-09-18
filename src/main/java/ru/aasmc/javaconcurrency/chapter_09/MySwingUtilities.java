package ru.aasmc.javaconcurrency.chapter_09;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

public class MySwingUtilities {
    private static volatile Thread swingThread;
    private static final ExecutorService exec =
            Executors.newSingleThreadExecutor(new SwingThreadFactory());

    private static class SwingThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            swingThread = new Thread(r);
            return swingThread;
        }
    }

    public static boolean isEventDispatchThread() {
        return Thread.currentThread() == swingThread;
    }

    public static void invokeLater(Runnable task) {
        exec.execute(task);
    }

    public static void invokeAndAwait(Runnable task) throws InterruptedException, InvocationTargetException {
        Future f = exec.submit(task);
        try {
            f.get();
        } catch (ExecutionException e) {
            throw new InvocationTargetException(e);
        }
    }
}
