package ru.aasmc.javaconcurrency.chapter_08.parallel_computations.puzzles;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SequentialPuzzleSolver<P, M> {
    private final Puzzle<P, M> puzzle;
    private final Set<P> seen = new HashSet<>();

    public SequentialPuzzleSolver(Puzzle<P, M> puzzle) {
        this.puzzle = puzzle;
    }

    public List<M> solve() {
        P pos = puzzle.initialPosition();
        return search(new Node<>(pos, null, null));
    }

    /**
     * Sequentially and recursively performs a depth-first search for a solution
     * of the puzzle.
     * @param node starting point to find a solution from.
     * @return Linked list of nodes, that represent a path from
     *         starting point to the goal point (the path is not
     *         necessarily the shortest one), or null if no such path
     *         exists.
     */
    private List<M> search(Node<P, M> node) {
        if (!seen.contains(node.pos)) {
            seen.add(node.pos);
            if (puzzle.isGoal(node.pos)) {
                return node.asMoveList();
            }
            for (M move : puzzle.legalMoves(node.pos)) {
                P pos = puzzle.move(node.pos, move);
                Node<P, M> child = new Node<>(pos, move, node);
                List<M> result = search(child);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Represents a position that has been reached through some
     * series of moves, or an initial position in the puzzle.
     */
    static class Node<P, M> {
        /**
         * Current position of the node.
         */
        final P pos;
        /**
         * Reference to the move that created the position.
         */
        final M move;
        /**
         * Node that we moved from to reach current position.
         */
        final Node<P, M> prev;

        public Node(P pos, M move, Node<P, M> prev) {
            this.pos = pos;
            this.move = move;
            this.prev = prev;
        }

        List<M> asMoveList() {
            List<M> solution = new LinkedList<>();
            for (Node<P, M> n = this; n.move != null; n = n.prev) {
                solution.add(0, n.move);
            }
            return solution;
        }
    }
}
