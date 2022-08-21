package ru.aasmc.javaconcurrency.chapter_05.cache;

public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
