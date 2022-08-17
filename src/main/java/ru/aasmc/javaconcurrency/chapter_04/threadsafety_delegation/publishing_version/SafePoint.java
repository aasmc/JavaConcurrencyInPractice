package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.publishing_version;

/**
 * A thread-safe mutable class.
 */
public class SafePoint {
    private int x, y;

    public SafePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Private constructor capturing.
     */
    private SafePoint(int[] a) {
        this(a[0], a[1]);
    }

    public SafePoint(SafePoint p) {
        this(p.get());
    }

    public synchronized int[] get() {
        return new int[] { x, y };
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
