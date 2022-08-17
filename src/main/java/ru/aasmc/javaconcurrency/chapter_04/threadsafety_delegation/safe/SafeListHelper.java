package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.safe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SafeListHelper<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<>());

    /**
     * This method is safe since we use the lock of synchronizedList to protect
     * the entire operation.
     */
    public boolean putIfAbsent(E x) {
        synchronized (list) {
            boolean absent = !list.contains(x);
            if (absent) {
                list.add(x);
            }
            return absent;
        }
    }
}
