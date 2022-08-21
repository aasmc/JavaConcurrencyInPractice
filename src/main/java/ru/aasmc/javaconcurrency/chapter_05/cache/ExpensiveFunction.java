package ru.aasmc.javaconcurrency.chapter_05.cache;

import java.math.BigInteger;

public class ExpensiveFunction implements Computable<String, BigInteger> {
    @Override
    public BigInteger compute(String arg) throws InterruptedException {
        // Pretend we are performing a computationally intensive operation
        Thread.sleep(1000);
        return new BigInteger(arg);
    }
}
