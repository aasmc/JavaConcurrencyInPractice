package ru.aasmc.javaconcurrency.chapter_05.cellularautomata;

public class Board {
    public void commitNewValues() {

    }

    public Board getSubBoard(int count, int num) {
        return new Board();
    }

    public boolean hasConverged() {
        return false;
    }

    public int getMaxX() {
        return Integer.MAX_VALUE;
    }

    public void setNewValue(int x, int y, int newValue) {

    }

    public void waitForConvergence() {

    }

    public int getMaxY() {
        return 0;
    }
}