package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.delegating_version;

/**
 * Immutable class.
 */
public class Point {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
