package com.nicolagheza.tictactoe;

import java.util.ArrayList;
import java.util.List;

public class AIPlayerRuleBased extends AIPlayer {
    /**
     * Constructor with reference to game board
     *
     * @param board
     */
    public AIPlayerRuleBased(Board board) {
        super(board);
    }


    private List<int[]> generatePossibleMoves() {
        List<int[]> nextMoves = new ArrayList<int[]>();
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.EMPTY)
                    nextMoves.add(new int[] {row, col});
            }
        }
        return nextMoves;
    }

    @Override
    public int[] move() {
        List<int[]> nextPossibleMoves = generatePossibleMoves();
        // Rule 1: If I have a winning move, take it.
        for (int[] nextMove : nextPossibleMoves) {
            // Try this move
            cells[nextMove[0]][nextMove[1]].content = mySeed;
            if (hasWon(mySeed)) {
                cells[nextMove[0]][nextMove[1]].content = Seed.EMPTY; // Undo move
                return nextMove;
            }
            cells[nextMove[0]][nextMove[1]].content = Seed.EMPTY; // Undo move
        }

        // Rule 2: If the opponent has a winning move, block it
        for (int[] nextMove: nextPossibleMoves) {
            // Try this move
            cells[nextMove[0]][nextMove[1]].content = oppSeed;
            if (hasWon(oppSeed)) {
                cells[nextMove[0]][nextMove[1]].content = Seed.EMPTY; // Undo move
                return nextMove;
            }
            cells[nextMove[0]][nextMove[1]].content = Seed.EMPTY; // Undo move
        }

        // Moves {row, col} in order of preferences. {0,0} at top-left corner
        int[][] preferredMoves = {
                {1,1}, {0,0}, {0,2}, {2,0}, {2,2},
                {0,1}, {1,0}, {1,2}, {2,1}};


        for (int[] move : preferredMoves) {
            if (cells[move[0]][move[1]].content == Seed.EMPTY) {
                return move;
            }
        }
        assert false : "No empty cell?!";
        return null;
    }

    private int[] winningPatterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // cols
            0b100010001, 0b001010100               // diagonals
    };

    /** Returns true if thePlayer wins */
    private boolean hasWon(Seed thePlayer) {
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
