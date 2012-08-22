package minefield;

import java.awt.Dimension;

import java.awt.Graphics;

import javax.swing.JPanel;

public class GridPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	Grid grid;

	public GridPanel(Grid grid) {
		this.grid = grid;

		Dimension size = new Dimension(grid.getCols() * Cell.WIDTH,
				grid.getRows() * Cell.HEIGHT);
		
		setPreferredSize(size);

	}

	public void paintComponent(Graphics g) {
		int ofsX = grid.getX();
		int ofsY = grid.getY();

		for (int row = 0; row < grid.getRows(); row++) {
			for (int col = 0; col < grid.getCols(); col++) {
				int i = row * grid.getCols() + col;
				Cell c = grid.getCell(i);
				c.paintAt(g, ofsX + col * Cell.WIDTH, ofsY + row * Cell.HEIGHT);
			}
		}

	}

}
