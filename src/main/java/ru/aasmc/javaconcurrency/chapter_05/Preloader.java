package ru.aasmc.javaconcurrency.chapter_05;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Preloader {
    private final FutureTask<ProductInfo> future =
            new FutureTask<ProductInfo>(this::loadProductInfo);

    private final Thread thread = new Thread(future);

    /**
     * It is bad practice and dangerous to start a thread in constructor or
     * static initialization block, so we use an explicit method to start a thread.
     */
    public void start() {
        thread.start();
    }

    public ProductInfo get() throws DataLoadException, InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException) {
                throw (DataLoadException) cause;
            } else {
                throw launderThrowable(cause);
            }
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

    private ProductInfo loadProductInfo() {
        return new ProductInfo();
    }
}
