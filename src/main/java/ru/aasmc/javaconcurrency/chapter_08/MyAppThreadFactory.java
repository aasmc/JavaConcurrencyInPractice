package ru.aasmc.javaconcurrency.chapter_08;

import java.util.concurrent.ThreadFactory;

public class MyAppThreadFactory implements ThreadFactory {
    private final String poolName;

    public MyAppThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new MyAppThread(r, poolName);
    }
}
