package ru.aasmc.javaconcurrency.chapter_04.threadsafety_delegation.unsafe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is not thread-safe because we use different locks. Implicit lock
 * of UnsafeListHelper is used when synchronizing the method putIfAbsent,
 * while the lock of wrapper collection synchronizedList is used to check
 * if the list contains element [x]. Therefore our locking is to no avail.
 * @param <E>
 */
public class UnsafeListHelper<E> {
    public List<E> list = Collections.synchronizedList(new ArrayList<>());

    public synchronized boolean putIfAbsent(E x) {
        boolean absent = !list.contains(x);
        if (absent) {
            list.add(x);
        }
        return absent;
    }
}
