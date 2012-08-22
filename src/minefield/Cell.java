package minefield;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public final class Cell {

	static BufferedImage image;
	static {
		try {
			image = ImageIO
					.read(Cell.class.getResourceAsStream("/tiles.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static final int WIDTH = 18;
	public static final int HEIGHT = 18;

	public static final int ZERO = 0;
	public static final int MINE = 9;
	public static final int EXPLODED_MINE = 10;
	public static final int SQUARE = 11;
	public static final int PRESSED_SQUARE = 12;
	public static final int FLAG = 13;
	public static final int FLAG_2 = 14;
	public static final int PRESSED_FLAG_2 = 15;
	public static final int WRONG_FLAG = 16;

	private Observer observer = null;

	private int n; // 0..8 or MINE
	State state = new CellState(this, ZERO);

	public Cell(int value) {
		setValue(value);
		restore();
	}

	public void setValue(int n) {
		assert (n >= ZERO && n <= MINE);
		this.n = n;
	}

	public int getValue() {
		return n;
	}

	public void setStateObserver(Observer o) {
		observer = o;
	}

	private void notifyStateObserver() {
		if (observer != null) {
			observer.update(this);
		}
	}

	public void paintAt(Graphics g, int x, int y) {

		((CellState) state).paintAt(g, x, y);
	}

	public void setState(State s) {
		state.leaveState();
		state = s;
		state.enterState();

		notifyStateObserver();
	}

	public void restore() {
		setState(Square);
	}

	public void open() {
		setState(isMineCell() ? ExplodedMine : Number);
	}

	public void openSafely() {
		setState(isMineCell() ? Mine : Number);
	}

	public void flag() {
		setState(Flag);
	}
	
	public void wrongFlag() {
		setState(WrongFlag);
	}
	
	public void animateFlag() {
		setState(AnimatedFlag);
	}

	public boolean isZeroCell() {
		return n == ZERO;
	}

	public boolean isMineCell() {
		return n == MINE;
	}

	public boolean isOpen() {
		return state == Number || state == Mine || state == ExplodedMine;
	}

	public boolean isFlagged() {
		return state == Flag || state == PressedFlag || state == AnimatedFlag || state == WrongFlag;
	}

	// States Setup //

	State Number = new CellState(this, ZERO);

	State Mine = new CellState(this, MINE);

	State ExplodedMine = new CellState(this, EXPLODED_MINE);

	State WrongFlag = new CellState(this, WRONG_FLAG);

	State Square = new CellState(this, SQUARE) {
		@Override
		public void leftPressed() {
			setState(PressedSquare);
		}

		@Override
		public void rightReleased() {
			flag();
		}
	};

	State PressedSquare = new CellState(this, PRESSED_SQUARE) {
		@Override
		public void leftReleased() {
			open();
		}

		@Override
		public void rightReleased() {
			flag();
		}

		@Override
		public void undoPress() {
			restore();
		}
	};

	State Flag = new CellState(this, FLAG) {
		@Override
		public void leftPressed() {
			setState(PressedFlag);
		}

		@Override
		public void rightReleased() {
			restore();
		}
	};

	State PressedFlag = new CellState(this, PRESSED_FLAG_2) {
		@Override
		public void leftReleased() {
			flag();
		}

		@Override
		public void rightReleased() {
			restore();
		}

		@Override
		public void undoPress() {
			flag();
		}

	};

	State AnimatedFlag = new CellState(this, FLAG) {
		int frame = 0;

		@Override
		public int getTile() {
			frame = (frame + 1) % 2;
			return frame == 0 ? FLAG : FLAG_2;
		}

	};

}

class CellState extends State {
	private Cell cell;
	private int tile;

	public CellState(Cell cell, int tile) {
		this.cell = cell;
		this.tile = tile;
	}

	public int getTile() {
		// Cells/Numbers can change value/tile so we need to get it on demand.
		return this == cell.Number ? cell.getValue() : this.tile;
	}

	public void paintAt(Graphics g, int x, int y) {
		int tile = getTile();

		g.drawImage(Cell.image, x, y, x + Cell.WIDTH, y + Cell.HEIGHT, tile
				* Cell.WIDTH, 0, (tile + 1) * Cell.WIDTH, Cell.HEIGHT, null);
	}
}
