package ru.aasmc.javaconcurrency.chapter_10.taxi;

import java.awt.*;

public class Taxi {
    private final Dispatcher dispatcher;
    private Point location;
    private Point destination;

    public Taxi(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public synchronized Point getLocation() {
        return location;
    }

    /**
     * Use open call (no synchronization) and restrict the synchronized
     * block inside the method. It prevents us from needing to acquire
     * two locks: One from Taxi, and One from Dispatcher at the same time.
     * Here we acquire Taxi lock for a small period of time, then release it.
     * Then we acquire Dispatcher lock.
     */
    public void setLocation(Point location) {
        boolean reachedDestination;
        synchronized (this) {
            this.location = location;
            reachedDestination = location.equals(destination);
        }
        if (reachedDestination) {
            dispatcher.notifyAvailable(this);
        }
    }
}
