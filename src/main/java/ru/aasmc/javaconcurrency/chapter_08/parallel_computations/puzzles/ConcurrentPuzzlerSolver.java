package ru.aasmc.javaconcurrency.chapter_08.parallel_computations.puzzles;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * This class doesn't deal well with the case where there is no solution:
 * if all possible moves and positions have been evaluated and no solution has been found,
 * ConcurrentPuzzleSolver.solve() waits forever in the call to getSolution().
 * One possible solution to the problem is to keep a count of active solver tasks
 * and set the solution to null when the count drops to zero as in PuzzleSolver.
 * @param <P>
 * @param <M>
 */
public class ConcurrentPuzzlerSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    /**
     * Executor that is used for parallelizing the work.
     * To avoid having to deal with RejectedExecutionException, the
     * rejected execution handler should be set to discard submitted tasks.
     */
    private final ExecutorService exec;
    private final ConcurrentMap<P, Boolean> seen;

    final ValueLatch<SequentialPuzzleSolver.Node<P, M>> solution =
            new ValueLatch<>();

    public ConcurrentPuzzlerSolver(Puzzle<P, M> puzzle,
                                   ExecutorService exec) {
        this.puzzle = puzzle;
        this.exec = exec;
        this.seen = new ConcurrentHashMap<>();
    }

    public List<M> solve() throws InterruptedException {
        try {
            P p = puzzle.initialPosition();
            exec.execute(newTask(p, null, null));
            // block until solution is found
            SequentialPuzzleSolver.Node<P, M> solNode = solution.getValue();
            return (solNode == null) ? null : solNode.asMoveList();
        } finally {
            // the first thread that finds a solution shuts down the Executor.
            exec.shutdown();
        }
    }

    protected Runnable newTask(P p, M m, SequentialPuzzleSolver.Node<P, M> n) {
        return new SolverTask(p, m, n);
    }

    class SolverTask extends SequentialPuzzleSolver.Node<P, M> implements Runnable {

        public SolverTask(P pos,
                          M move,
                          SequentialPuzzleSolver.Node<P, M> prev) {
            super(pos, move, prev);
        }

        @Override
        public void run() {
            // first consult a solution latch, and stop if the solution has already been found
            // then if no solution is found, go to the cache to check and put current position
            // to the cache if it is not present.
            // seen.putIfAbsent atomically puts position to the map if it is not present there
            // this allows us to avoid race condition inherent in conditionally updating
            // a shared collection
            if (solution.isSet() || seen.putIfAbsent(pos, true) != null) {
                return; // already solved or seen this position
            }
            if (puzzle.isGoal(pos)) {
                solution.setValue(this);
            } else {
                for (M m : puzzle.legalMoves(pos)) {
                    exec.execute(newTask(puzzle.move(pos, m), m, this));
                }
            }
        }
    }
}
