package ru.aasmc.javaconcurrency.chapter_08.parallel_computations.puzzles;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class PuzzleSolver<P, M> extends ConcurrentPuzzlerSolver<P, M> {
    public PuzzleSolver(Puzzle<P, M> puzzle, ExecutorService exec) {
        super(puzzle, exec);
    }

    private final AtomicInteger taskCount = new AtomicInteger(0);

    @Override
    protected Runnable newTask(P p, M m, SequentialPuzzleSolver.Node<P, M> n) {
        return new CountingSolverTask(p, m, n);
    }

    class CountingSolverTask extends SolverTask {

        public CountingSolverTask(P pos, M move, SequentialPuzzleSolver.Node<P, M> prev) {
            super(pos, move, prev);
            taskCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                if (taskCount.decrementAndGet() == 0) {
                    solution.setValue(null);
                }
            }
        }
    }
}
