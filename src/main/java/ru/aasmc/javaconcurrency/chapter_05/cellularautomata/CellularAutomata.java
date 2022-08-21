package ru.aasmc.javaconcurrency.chapter_05.cellularautomata;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CellularAutomata {
    private final Board mainBoard;
    private final CyclicBarrier barrier;
    private final Worker[] workers;

    public CellularAutomata(Board mainBoard) {
        this.mainBoard = mainBoard;
        int count = Runtime.getRuntime().availableProcessors();
        this.barrier = new CyclicBarrier(count, mainBoard::commitNewValues);
        this.workers = new Worker[count];
        for (int i = 0; i < count; i++) {
            workers[i] = new Worker(mainBoard.getSubBoard(count, i));
        }
    }

    private class Worker implements Runnable {
        private final Board board;

        public Worker(Board board) {
            this.board = board;
        }

        @Override
        public void run() {
            while (!board.hasConverged()) {
                for (int x = 0; x < board.getMaxX(); ++x) {
                    for (int y = 0; y < board.getMaxY(); ++y) {
                        board.setNewValue(x, y, computeNewValue(x, y));
                    }
                }
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    return;
                }
            }
        }
    }

    private int computeNewValue(int x, int y) {
        return 0;
    }

    public void start() {
        for (Worker worker : workers) {
            new Thread(worker).start();
        }
        mainBoard.waitForConvergence();
    }
}
