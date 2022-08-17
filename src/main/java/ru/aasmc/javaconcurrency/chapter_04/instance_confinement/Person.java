package ru.aasmc.javaconcurrency.chapter_04.instance_confinement;

/**
 * A thread-safe class Person.
 */
public class Person {
    private final String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
