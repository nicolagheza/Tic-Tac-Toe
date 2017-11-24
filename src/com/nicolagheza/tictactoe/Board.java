package com.nicolagheza.tictactoe;

import java.util.ArrayList;

public class Board {
    protected int ROWS = GameMain.ROWS; // number of rows
    protected int COLS = GameMain.COLS; // number of cols

    ArrayList<Board> gameHistory;

    // package access
    Cell[][] cells; // 2D array of ROWS-by-COLS Cell instances

    /** Constructor to initialize the game board */
    public Board() {
        cells = new Cell[GameMain.ROWS][GameMain.COLS]; // allocate the array
        for (int row = 0; row < GameMain.ROWS; row++) {
            for (int col = 0; col < GameMain.COLS; col++) {
                cells[row][col] = new Cell(row, col); // allocate element of array
            }
        }
        this.gameHistory = new ArrayList<Board>();
    }

    public Board clone() {
        Board board = new Board();
        Board newBoard = new Board();
        newBoard.cells = copyBoard(this.cells, 3);

        return newBoard;
    }

    private Cell[][] copyBoard(Cell[][] original, int dimension) {
        Cell[][] copy = new Cell[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                copy[i][j] = original[i][j];
            }
        }
        return copy;
    }

    /** Initialize (or re-initialize) the game board */
    public void init() {
        for (int row = 0; row < GameMain.ROWS; row++) {
            for (int col = 0; col < GameMain.COLS; col++) {
                cells[row][col].clear(); // clear the cell content
            }
        }
    }

    /** Return true if it is a draw (i.e., no more EMPTY cell) */
    public boolean isDraw() {
        for (int row = 0; row < GameMain.ROWS; row++) {
            for (int col = 0; col < GameMain.COLS; col++) {
                if (cells[row][col].content == Seed.EMPTY) {
                    return false; // an empty seed found, not a draw, exit
                }
            }
        }
        return true; // no empty cell, it's a draw
    }

    /** Return true if the player with "seed" has won after placing at (seedRow, seedCol) */
    public boolean hasWon(Seed seed, int seedRow, int seedCol) {
        return (cells[seedRow][0].content == seed // 3-in-the-row
                   && cells[seedRow][1].content == seed
                   && cells[seedRow][2].content == seed
               || cells[0][seedCol].content == seed // 3-in-the-column
                   && cells[1][seedCol].content == seed
                   && cells[2][seedCol].content == seed
               || seedRow == seedCol              // 3-in-the-diagonal
                   && cells[0][0].content == seed
                   && cells[1][1].content == seed
                   && cells[2][2].content == seed
               || seedRow + seedCol == 2 // 3-in-the-opposite-diagonal
                   && cells[0][2].content == seed
                   && cells[1][1].content == seed
                   && cells[2][0].content == seed);
    }

    protected int[] winningPatterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // cols
            0b100010001, 0b001010100               // diagonals
    };

    public boolean hasWon(Seed thePlayer) {
        int pattern = 0b000000000;  // 9-bit pattern for the 9 cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == thePlayer) {
                    pattern |= (1 << (row * COLS + col));
                }
            }
        }
        for (int winningPattern : winningPatterns) {
            if ((pattern & winningPattern) == winningPattern) return true;
        }
        return false;
    }

}
