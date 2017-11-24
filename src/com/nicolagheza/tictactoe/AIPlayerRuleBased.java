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
}
