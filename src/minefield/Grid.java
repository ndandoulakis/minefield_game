package minefield;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public final class Grid implements Iterable<Cell>, Observer {

	private int cols;
	private int rows;

	private Point pos = new Point(0, 0);

	private Observer observer = null;

	private ArrayList<Cell> grid = new ArrayList<Cell>();
	private Map<Cell, Integer> indices = new HashMap<Cell, Integer>();

	public Grid(int cols, int rows, int mines) {
		this.cols = cols;
		this.rows = rows;

		int size = cols * rows;

		// at least one free cell must exist
		assert (mines < size);
		for (int i = 0; i < mines; i++) {
			grid.add(new Cell(Cell.MINE));
		}
		for (int i = mines; i < size; i++) {
			grid.add(new Cell(Cell.ZERO));
		}

		reset();

	}

	public void reset() {
		resetPos();

		Collections.shuffle(grid);
		setIndices();
		setNumbers();
		setInitialStates();
	}

	@Override
	public Iterator<Cell> iterator() {
		return grid.iterator();
	}

	public void setObserver(Observer o) {
		observer = o;
		for (Cell cell : grid) {
			cell.setStateObserver(this);
		}
	}

	@Override
	public void update(Object subject) {
		notifyObserver();
	}

	private void notifyObserver() {
		if (observer != null)
			observer.update(this);
	}

	public void resetPos() {
		pos.move(0, 0);
		notifyObserver();
	}

	public void translatePos(int dx, int dy) {
		pos.translate(dx, dy);
		notifyObserver();
	}
	
	public int getX() {
		return pos.x;
	}

	public int getY() {
		return pos.y;
	}

	public Cell getCell(int i) {
		return grid.get(i);
	}

	public int getSize() {
		return grid.size();
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public Cell getCellAt(int x, int y) {

		if (isCellAt(x, y)) {
			int i = (y / Cell.HEIGHT) * cols + x / Cell.WIDTH;
			return grid.get(i);
		} else
			return null;
	}

	public boolean isCellAt(int x, int y) {

		return x >= 0 && x < cols * Cell.WIDTH && y >= 0
				&& y < rows * Cell.HEIGHT;

	}

	private void setIndices() {
		indices.clear();
		for (int i = 0; i < grid.size(); i++) {
			indices.put(grid.get(i), i);
		}

	}

	private void setInitialStates() {
		for (Cell c : grid) {
			c.restore();
		}

	}

	private int getIndex(Cell c) {
		return c == null ? -1 : indices.get(c);
	}

	private void setNumbers() {
		for (Cell c : grid) {
			if (!c.isMineCell()) {
				c.setValue(Cell.ZERO);

			}
		}

		for (Cell c : grid) {
			if (c.isMineCell()) {
				for (Cell a : getAdjacentCells(c)) {
					if (!a.isMineCell())
						a.setValue(a.getValue() + 1);

				}
			}
		}
	}

	public Iterable<Cell> getConnectedCells(Cell cell) {

		HashSet<Cell> explored = new HashSet<Cell>();

		if (cell != null) {
			Stack<Cell> stack = new Stack<Cell>();
			stack.push(cell);
			while (!stack.isEmpty()) {
				Cell c = stack.pop();
				explored.add(c);
				if (c.isZeroCell()) {
					for (Cell a : getAdjacentCells(c)) {
						if (!explored.contains(a))
							stack.push(a);
					}
				}
			}
		}

		return explored;
	}

	public Iterable<Cell> getAdjacentCells(Cell c) {
		return getAdjacentCells(getIndex(c));
	}

	public Iterable<Cell> getAdjacentCells(int i) {

		Collection<Cell> cells = new ArrayList<Cell>();
		if (i >= 0 && i < getSize()) {

			int x = i % cols;
			int y = i / cols;

			if (y > 0 && x > 0)
				cells.add(grid.get((y - 1) * cols + (x - 1)));
			if (x > 0)
				cells.add(grid.get((y + 0) * cols + (x - 1)));
			if (y < rows - 1 && x > 0)
				cells.add(grid.get((y + 1) * cols + (x - 1)));

			if (y > 0)
				cells.add(grid.get((y - 1) * cols + (x + 0)));
			if (y < rows - 1)
				cells.add(grid.get((y + 1) * cols + (x + 0)));

			if (y > 0 && x < cols - 1)
				cells.add(grid.get((y - 1) * cols + (x + 1)));
			if (x < cols - 1)
				cells.add(grid.get((y + 0) * cols + (x + 1)));
			if (y < rows - 1 && x < cols - 1)
				cells.add(grid.get((y + 1) * cols + (x + 1)));

		}

		return cells;
	}

	public void makeCellSafe(Cell c) {
		if (c == null)
			return;

		if (c.isMineCell()) {
			for (Cell cell : grid) {
				if (cell.isMineCell())
					continue;

				cell.setValue(Cell.MINE);
				c.setValue(Cell.ZERO);
				setNumbers();
				return;

			}

		}

	}

	public void openConnectedCells(Cell c) {
		for (Cell connected : getConnectedCells(c)) {
			if (!connected.isFlagged())
				connected.open();
		}
	}

	public void openAdjacentCells(Cell c) {
		if (c == null)
			return;

		Iterable<Cell> cells = getAdjacentCells(c);

		if (countFlagged(cells) != c.getValue()) {
			return;
		}

		for (Cell adjacent : cells) {
			if (adjacent.isOpen())
				continue;
			openConnectedCells(adjacent);
		}

	}

	public void autoFlagMines() {
		for (Cell cell : grid) {
			if (cell.isZeroCell() || !cell.isOpen())
				continue;

			Iterable<Cell> cells = getAdjacentCells(cell);

			if (!(countHidden(cells) == cell.getValue() && countHiddenMines(cells) == cell
					.getValue()))
				continue;

			for (Cell adjacent : cells) {
				if (adjacent.isMineCell())
					adjacent.flag();
			}
		}

	}

	public void animateFlags() {
		for (Cell cell : grid) {
			if (cell.isMineCell() && !cell.isOpen())
				cell.animateFlag();
		}
	}

	public void markWrongFlags() {
		for (Cell cell : grid) {
			if (!cell.isMineCell() && cell.isFlagged())
				cell.wrongFlag();
		}
	}

	public void openMinesSafely() {
		for (Cell cell : grid) {
			if (cell.isMineCell() && !cell.isOpen() && !cell.isFlagged())
				cell.openSafely();
		}
	}

	public static int countFlagged(Iterable<Cell> cells) {
		int count = 0;
		for (Cell c : cells)
			if (c.isFlagged())
				count++;
		return count;
	}

	public static int countExplodedMines(Iterable<Cell> cells) {
		int count = 0;
		for (Cell cell : cells) {
			if (cell.isMineCell() && cell.isOpen())
				count++;
		}
		return count;
	}

	public static int countHidden(Iterable<Cell> cells) {
		int count = 0;
		for (Cell cell : cells)
			if (!cell.isOpen())
				count++;
		return count;
	}

	public static int countHiddenMines(Iterable<Cell> cells) {
		int count = 0;
		for (Cell cell : cells)
			if (!cell.isOpen() && cell.isMineCell())
				count++;
		return count;
	}

}
