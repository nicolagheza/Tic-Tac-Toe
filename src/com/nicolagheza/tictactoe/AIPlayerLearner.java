package com.nicolagheza.tictactoe;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by nicolagheza on 22/11/2017.
 */
public class AIPlayerLearner extends AIPlayer {

    private static final float WIN_SCORE = 100, LOSE_SCORE = -WIN_SCORE, DRAW_SCORE = 0;

    private float n = 0.4f;

    private float weights[];
    private Board board;

    /**
     * Constructor with reference to game board
     *
     * @param board
     */
    public AIPlayerLearner(Board board, float[] weights) {
        super(board);
        this.board = board;
        this.weights = weights;
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
        return findBestMove();
    }

    private int[] findBestMove() {
        float score = -Float.MAX_VALUE, test_score;
        int[] bestMove = {-1 , -1};

        for (int[] move : generatePossibleMoves()) {
            cells[move[0]][move[1]].content = mySeed;
            test_score = predictBoardValue(board);

            if (test_score > score) {
                score = test_score;
                bestMove = move.clone();
            }
            cells[move[0]][move[1]].content = Seed.EMPTY;
        }

        if (bestMove == null) {
            System.out.println("Player " + mySeed + " bestMove is null");
            return generatePossibleMoves().get(0);
        }
        return bestMove;
    }

    private int getNumberOfOccupiedRows(Board boardState, Seed seed) {
        Cell cells[][] = boardState.cells;
        int counter = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == seed) {
                    counter++;
                    continue;
                }
            }
        }
        return counter;
    }

    private int getNumberOfOccupiedCols(Board boardState, Seed seed) {
        Cell cells[][] = boardState.cells;
        int counter = 0;
        for (int col = 0; col < ROWS; ++col) {
            for (int row = 0; row < COLS; ++row) {
                if (cells[row][col].content == seed) {
                    counter++;
                    continue;
                }
            }
        }
        return counter;
    }

    private int getNumberOfOccupiedDiags(Board boardState, Seed seed) {
        Cell cells[][] = boardState.cells;
        int counter = 0;
        if (cells[0][0].content == seed)
            counter++;
        if (cells[1][1].content == seed)
            counter++;
        if (cells[2][2].content == seed)
            counter++;

        return counter;
    }

    private int[] getBoardFeatures(Board boardState) {
        int x1 = getNumberOfOccupiedCols(boardState, mySeed);
        int x2 = getNumberOfOccupiedCols(boardState, oppSeed);
        int x3 = getNumberOfOccupiedRows(boardState, mySeed);
        int x4 = getNumberOfOccupiedRows(boardState, oppSeed);
        int x5 = getNumberOfOccupiedDiags(boardState, mySeed);
        int x6 = getNumberOfOccupiedDiags(boardState, oppSeed);

        return new int[] {x1, x2, x3, x4, x5, x6};
    }

    public float predictBoardValue(Board boardState) {
        // V(b) = V'(Successor(b))
        // Linear combination of board features

        int[] X = getBoardFeatures(boardState);


        return weights[0] + weights[1]*X[0] + weights[2]*X[1] + weights[3]*X[2] + weights[4]*X[3] + weights[5]*X[4] + weights[6]*X[5];
    }

    // Generalizer
    public void updateWeights(ArrayList<Board> history){
        Queue<Float> trainingValues = this.getActualBoardValues(history);
        double error;
        int[] boardStateAnalysis;

        Board currBoard;

        double errors = 0.0;
        for (int i = 0; i < history.size(); i += 2){
            currBoard = history.get(i);


            boardStateAnalysis = this.getBoardFeatures(currBoard);
            error = trainingValues.remove() - this.predictBoardValue(currBoard);
            errors += error;
            weights[1] += n * error * boardStateAnalysis[0];
            weights[2] += n * error * boardStateAnalysis[1];
            weights[3] += n * error * boardStateAnalysis[2];
            weights[4] += n * error * boardStateAnalysis[3];
            weights[5] += n * error * boardStateAnalysis[4];
            weights[6] += n * error * boardStateAnalysis[5];
        }
        System.out.println("Error: " + Math.pow(errors,2));
        System.out.println("New weights:");
        System.out.println("weights[1] " + weights[1]);
        System.out.println("weights[2] " + weights[2]);
        System.out.println("weights[3] " + weights[3]);
        System.out.println("weights[4] " + weights[4]);
        System.out.println("weights[5] " + weights[5]);
        System.out.println("weights[6] " + weights[6]);
    }

    private Queue<Float> getActualBoardValues(ArrayList<Board> history){
        int historyCount = history.size();
        Queue<Float> historyScores = new LinkedList<Float>();
        for (int i = 0; i < historyCount; i += 2){
            if (i + 2 >= historyCount - 1){
				/*
				 * This condition reflects the case where the next entry in the game history
				 * is the last state (i.e. the opponent's move has ended the game).
				 */
                if (i + 1 == historyCount - 1 && history.get(i + 1).hasWon(oppSeed)){
                    if (history.get(i+1).isDraw()){
                        historyScores.add(DRAW_SCORE);
                        continue;
                    }
                    historyScores.add(LOSE_SCORE);
                    continue;
                }

				/*
				 * The following two cases are caught in this condition:
				 * 	1. The successor state is the end of the game.
				 * 	2. The current state is the end of the game.
				 */
                Board currBoard = history.get(i);

                if (i + 2 == historyCount - 1){
                    currBoard = history.get(i + 2);
                }

                if (currBoard.hasWon(mySeed)) {
                    historyScores.add(WIN_SCORE);
                    continue;
                }

                if (currBoard.isDraw()) {
                    historyScores.add(DRAW_SCORE);
                    continue;
                }

                if (currBoard.hasWon(oppSeed)) {
                    historyScores.add(LOSE_SCORE);
                    continue;
                }
            }

            historyScores.add(this.predictBoardValue(history.get(i+2)));
        }

        return historyScores;
    }

}
