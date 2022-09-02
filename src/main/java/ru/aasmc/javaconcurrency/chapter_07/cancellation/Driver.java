package ru.aasmc.javaconcurrency.chapter_07.cancellation;

import java.math.BigInteger;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Driver {
    private static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator primeGenerator = new PrimeGenerator();
        new Thread(primeGenerator).start();
        try {
            SECONDS.sleep(1);
        } finally {
            primeGenerator.cancel();
        }
        return primeGenerator.get();
    }

    public static void main(String[] args) throws InterruptedException {
        aSecondOfPrimes().forEach(System.out::println);
    }
}
