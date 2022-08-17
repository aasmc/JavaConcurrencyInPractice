package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.monitor_version;

/**
 * Not thread-safe class
 */
public class MutablePoint {
    public int x, y;

    public MutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MutablePoint(MutablePoint other) {
        this.x = other.x;
        this.y = other.y;
    }
}
