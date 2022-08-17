package ru.aasmc.javaconcurrency.chapter_04.instance_confinement;

import java.util.HashSet;
import java.util.Set;

/**
 * The class is thread-safe because [mySet] field doesn't leak, and Person class
 * is immutable, while all the methods on the class are synchronized.
 */
public class PersonSet {
    private final Set<Person> mySet = new HashSet<>();

    public synchronized void addPerson(Person p) {
        mySet.add(p);
    }

    public synchronized boolean containsPerson(Person p) {
        return mySet.contains(p);
    }
}
