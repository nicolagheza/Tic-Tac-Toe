package com.nicolagheza.tictactoe;

public abstract class AIPlayer {
    protected int ROWS = GameMain.ROWS; // number of rows
    protected int COLS = GameMain.COLS; // number of cols

    protected Cell[][] cells; // the board's ROWs-by-COLs array of Cells
    protected Seed mySeed; // computer's seed
    protected Seed oppSeed; // opponent's seed

    /**  Constructor with reference to game board */
    public AIPlayer(Board board) {
        cells = board.cells;
    }

    /** Set/change the seed used by computer and opponent */
    public void setSeed(Seed seed) {
        this.mySeed = seed;
        oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    public abstract int[] move();
}
