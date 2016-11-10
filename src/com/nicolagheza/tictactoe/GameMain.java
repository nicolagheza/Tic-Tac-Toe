package com.nicolagheza.tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameMain extends JPanel{

    private static final long serialVersionUID = 0L;
    private static final int TIMER_DELAY = 500;

    // Named-constants for the game board
    public static final int ROWS = 3;  // ROWS by COLS cells
    public static final int COLS = 3;
    public static final String TITLE = "Tic Tac Toe";

    // Name-constants for the various dimensions used for graphics drawing
    public static final int CELL_SIZE = 100; // cell width and height (square)
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 8;  // Grid-line's width
    public static final int GRID_WIDHT_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2;
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width

    private Board board; // the game board
    private BoardView boardView;
    private AIPlayer aiPlayer1;
    private AIPlayer aiPlayer2;
    private GameState currentState; // the current state of the game
    private Seed currentPlayer; // the current player
    private JLabel statusBar;  // for displaying status message

    /** Constructor to setup the UI and game components */
    public GameMain() {
        // This JPanel fires MouseEvent
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int rowSelected = mouseY / CELL_SIZE;
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == GameState.PLAYING) {
                    if (rowSelected >= 0 && rowSelected < ROWS
                          && colSelected >= 0 && colSelected < COLS
                          &&  board.cells[rowSelected][colSelected].content == Seed.EMPTY) {
                        board.cells[rowSelected][colSelected].content = currentPlayer; // move
                        updateGame(currentPlayer, rowSelected, colSelected); // update currentState
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        if (currentState == GameState.PLAYING)
                            makeAIMove(aiPlayer1);
                    }
                } else {    // game over
                    initGame();
                }
                // Refresh the drawing canvas
                repaint();
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("         ");
        statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 14));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
        statusBar.setOpaque(true);
        statusBar.setBackground(Color.LIGHT_GRAY);

        setLayout(new BorderLayout());
        add(statusBar, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 30));
        board = new Board(); // allocate the game-board
        boardView = new BoardView(board.cells);
        initGame();
        initAI();
    }

    private void initAI() {
        aiPlayer1 = new AIPlayerMinimax(board);
        aiPlayer1.setSeed(Seed.CROSS);
        aiPlayer2 = new AIPlayerMinimax(board);
        aiPlayer2.setSeed(Seed.NOUGHT);
    }

    /** Initialize the game-board contents and the current-state */
    public void initGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board.cells[row][col].content = Seed.EMPTY; // all cells empty
            }
        }
        currentState = GameState.PLAYING; // ready to play
        currentPlayer = Seed.CROSS; // cross plays first

    }

    public void makeAIMove(AIPlayer player) {

        int[] move = player.move();
        if (move != null) {
            board.cells[move[0]][move[1]].content = player.mySeed;
            updateGame(currentPlayer, move[0], move[1]);
            repaint();
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        }

    }

    /** Update the currentState after the player with "theSeed" has placed on (row, col) */
    public void updateGame(Seed theSeed, int row, int col) {
        if(board.hasWon(theSeed, row, col)) { // check for win
            currentState = (theSeed == Seed.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
        } else if (board.isDraw()) { // check for draw
            currentState = GameState.DRAW;
        }
        // Otherwise, no change to current state (PLAYING).
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // fill background
        setBackground(Color.WHITE); // set its background color

        boardView.paint(g); // ask the game board to paint itself

        // Print status-ba message
        if (currentState == GameState.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            if (currentPlayer == Seed.CROSS) {
                statusBar.setText("X's Turn");
            } else {
                statusBar.setText("O's Turn");
            }
        } else if (currentState == GameState.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == GameState.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == GameState.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    public void getNextState() {
        AIPlayer curPlayer = currentPlayer == aiPlayer1.mySeed ? aiPlayer1 : aiPlayer2;

        makeAIMove(curPlayer);


    }

    /** The entry "main" method */
    public static void main(String args[]) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                GameMain game = new GameMain();
                frame.setContentPane(game);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);


                new javax.swing.Timer(TIMER_DELAY, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (game.currentState != GameState.PLAYING)
                                return;
                        game.getNextState();
                    }
                }).start();


            }
        });
    }

}
