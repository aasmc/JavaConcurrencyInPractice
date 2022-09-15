package ru.aasmc.javaconcurrency.chapter_08.parallel_computations;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class Driver {
    public <T> void parallelRecursive(final Executor exec,
                                      List<Node<T>> nodes,
                                      final Collection<T> results) {
        for (final Node<T> n : nodes) {
            exec.execute(() -> {
                results.add(n.compute());
            });
            parallelRecursive(exec, n.getChildren(), results);
        }
    }

    public <T> Collection<T> getParallelResults(List<Node<T>> nodes) throws InterruptedException {
        ExecutorService exec = Executors.newCachedThreadPool();
        Queue<T> resultQueue = new ConcurrentLinkedQueue<>();
        parallelRecursive(exec, nodes, resultQueue);
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        return resultQueue;
    }
}
