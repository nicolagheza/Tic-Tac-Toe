package com.nicolagheza.tictactoe;

import java.awt.*;

public class BoardView {

    private Cell[][] cells;

    public BoardView(Cell cells[][]) {
        this.cells = cells;
    }


    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Draw the grid-lines
        g.setColor(Color.GRAY);
        for (int row = 1; row < GameMain.ROWS; ++row) {
            g.fillRoundRect(0, GameMain.CELL_SIZE * row - GameMain.GRID_WIDHT_HALF,
                    GameMain.CANVAS_WIDTH - 1, GameMain.GRID_WIDTH,
                    GameMain.GRID_WIDTH, GameMain.GRID_WIDTH);
        }
        for (int col = 1; col < GameMain.COLS; ++col) {
            g.fillRoundRect(GameMain.CELL_SIZE * col - GameMain.GRID_WIDHT_HALF, 0,
                    GameMain.GRID_WIDTH, GameMain.CANVAS_HEIGHT - 1,
                    GameMain.GRID_WIDTH, GameMain.GRID_WIDTH);
        }
        // Draw all the cells
        for (int row = 0; row < GameMain.ROWS; ++row) {
            for (int col = 0; col < GameMain.COLS; ++col) {
                cells[row][col].paint(g);  // ask the cell to paint itself
            }
        }
    }
}
