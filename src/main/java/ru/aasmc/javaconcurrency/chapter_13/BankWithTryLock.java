package ru.aasmc.javaconcurrency.chapter_13;

import ru.aasmc.javaconcurrency.chapter_10.bank.Account;
import ru.aasmc.javaconcurrency.chapter_10.bank.Bank;
import ru.aasmc.javaconcurrency.chapter_10.bank.DollarAmount;
import ru.aasmc.javaconcurrency.chapter_10.bank.InsufficientFundsException;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BankWithTryLock extends Bank {

    private final Random rnd = new Random(System.currentTimeMillis());

    public boolean transferMoney(Account fromAccount,
                                 Account toAccount,
                                 DollarAmount amount,
                                 long timeout,
                                 TimeUnit unit) throws InsufficientFundsException, InterruptedException {
        // fixed component and random component are needed to reduce the likelihood of livelock.
        long fixedDelay = getFixedDelayComponentNanos(timeout, unit);
        long randMod = getRandomDelayModulusNanos(timeout, unit);
        long stopTime = System.nanoTime() + unit.toNanos(timeout);
        while (true) {
            if (fromAccount.lock.tryLock()) {
                try {
                    if (toAccount.lock.tryLock()) {
                        try {
                            if (fromAccount.getBalance().compareTo(amount) > 0) {
                                throw new InsufficientFundsException();
                            } else {
                                fromAccount.debit(amount);
                                toAccount.credit(amount);
                                return true;
                            }
                        } finally {
                            toAccount.lock.unlock();
                        }
                    }
                } finally {
                    fromAccount.lock.unlock();
                }
            }
            if (System.nanoTime() < stopTime) {
                return false;
            }
            TimeUnit.NANOSECONDS.sleep(fixedDelay + rnd.nextLong() % randMod);
        }
    }

    private long getFixedDelayComponentNanos(long timeout, TimeUnit unit) {
        // stub method
        return 0L;
    }

    private long getRandomDelayModulusNanos(long timeout, TimeUnit unit) {
        // stub method
        return 0L;
    }
}
