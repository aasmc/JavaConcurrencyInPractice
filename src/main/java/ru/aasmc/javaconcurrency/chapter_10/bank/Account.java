package ru.aasmc.javaconcurrency.chapter_10.bank;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
    public final Lock lock = new ReentrantLock();

    public void debit(DollarAmount dollarAmount) {
        // stub
    }

    public void credit(DollarAmount dollarAmount) {
        // stub
    }

    public DollarAmount getBalance() {
        // stub
        return null;
    }
}
