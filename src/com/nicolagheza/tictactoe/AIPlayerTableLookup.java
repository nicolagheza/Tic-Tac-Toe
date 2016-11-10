package com.nicolagheza.tictactoe;


public class AIPlayerTableLookup extends AIPlayer {

    // Moves {row, col} in order of preferences. {0,0} at top-left corner
    private int[][] preferredMoves = {
            {1,1}, {0,0}, {0,2}, {2,0}, {2,2},
            {0,1}, {1,0}, {1,2}, {2,1}};

    /**
     * Constructor with reference to game board
     *
     * @param board
     */
    public AIPlayerTableLookup(Board board) {
        super(board);
    }

    @Override
    public int[] move() {
        for (int[] move : preferredMoves) {
            if (cells[move[0]][move[1]].content == Seed.EMPTY) {
                return move;
            }
        }
        assert false : "No empty cell?!";
        return null;
    }
}
