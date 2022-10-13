package ru.aasmc.javaconcurrency.chapter_15;

import java.util.concurrent.atomic.AtomicReference;

public class LinkedQueue<E> {
    private static class Node<E> {
        final E item;
        final AtomicReference<Node<E>> next;

        public Node(E item, Node<E> next) {
            this.item = item;
            this.next = new AtomicReference<>(next);
        }
    }

    private final Node<E> dummy = new Node<>(null, null);
    private final AtomicReference<Node<E>> head = new AtomicReference<>(dummy);
    private final AtomicReference<Node<E>> tail = new AtomicReference<>(dummy);

    public boolean put(E item) {
        Node<E> newNode = new Node<>(item, null);
        while (true) {
            Node<E> currTail = tail.get();
            Node<E> tailNext = currTail.next.get();
            if (currTail == tail.get()) {
                if (tailNext != null) {
                    // queue is in the intermediate state, advance tail
                    tail.compareAndSet(currTail, tailNext);
                } else {
                    // in quiescent state, try inserting the new node
                    if (currTail.next.compareAndSet(null, newNode)) {
                        // insertion succeeded, try advancing tail
                        tail.compareAndSet(currTail, newNode);
                        return true;
                    }
                }
            }
        }
    }
}
