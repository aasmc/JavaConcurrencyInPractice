package ru.aasmc.javaconcurrency.chapter_08.parallel_computations;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    private final List<Node<T>> children;
    private final T data;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void addChild(T child) {
        children.add(new Node<>(child));
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    T compute() {
        return data;
    }
}
