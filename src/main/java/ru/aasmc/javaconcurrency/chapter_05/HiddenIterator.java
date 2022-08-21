package ru.aasmc.javaconcurrency.chapter_05;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HiddenIterator {
    private final Set<Integer> set = new HashSet<>();

    public synchronized void add(Integer i) {
        set.add(i);
    }

    public synchronized void remove(Integer i) {
        set.remove(i);
    }

    public void addTenThings() {
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            add(r.nextInt());
        }
        // This line uses hidden iterator, because when we
        // concatenate strings, compiler transforms the method call to StringBuilder.append(Object)
        // while StringBuilder uses Object.toString() method which for standard collections
        // uses iterative traversal of the collection.
        System.out.println("DEBUG: added ten elements to set: " + set);
    }
}
