package minefield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public final class GameFSM {

	State state = new State() {
	};

	GameData data;

	public GameFSM(GameData data) {
		assert (data != null);
		this.data = data;
	}

	public void autoFlagMines() {
		data.grid.autoFlagMines();
		data.updateUnflaggedCounter();
	}

	private void setState(State s) {
		state.leaveState();
		state = s;
		state.enterState();
	}

	public void setActiveState() {
		setState(Active);
	}

	public void setRestartedState() {
		setState(Restarted);
	}

	public void setExplodedState() {
		setState(Exploded);
	}

	public void setClearedState() {
		setState(Cleared);
	}

	// States Setup //

	private State Active = new Active(this);

	private State Exploded = new GameState(this) {
		private Timer quakeTimer = new Timer(55, null) {
			private static final long serialVersionUID = 1L;
			private final int[][] frames = { { +3, -2 }, { -4, +2 }, { +2, +1 } };
			private int i = 0;
			{
				addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (i >= frames.length) {
							stop();
							data.grid.resetPos();
							return;
						}

						data.grid.translatePos(frames[i][0], frames[i][1]);
						i++;
					}
				});
			}

			@Override
			public void start() {
				i = 0;
				super.start();
			}
		};

		@Override
		public void enterState() {
			data.stopGameTimer();
			data.grid.openMinesSafely();
			data.grid.markWrongFlags();
			quakeTimer.start();
		}

		@Override
		public void leaveState() {
			quakeTimer.stop();
			data.grid.resetPos();
		}
	};

	private State Cleared = new GameState(this) {
		private Timer animTimer = new Timer(150, null);
		{
			animTimer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					data.grid.animateFlags();
				}
			});
		}

		@Override
		public void enterState() {
			data.stopGameTimer();
			autoFlagMines();
			animTimer.start();
		}

		@Override
		public void leaveState() {
			animTimer.stop();
		}

	};

	private State Restarted = new Active(this) {

		@Override
		public void enterState() {
			data.resetGameTimer();
			data.grid.reset();
			data.updateUnflaggedCounter();
		}

		@Override
		public void leftReleased(int x, int y) {
			cell = data.grid.getCellAt(x, y);

			if (cell != null && !cell.isFlagged()) {

				data.grid.makeCellSafe(cell);
				setActiveState();
			}

			super.leftReleased(x, y);
		}
	};

}

class GameState extends State {
	protected GameFSM fsm;

	public GameState(GameFSM fsm) {
		assert (fsm != null);
		this.fsm = fsm;
	}
}

class Active extends GameState {
	protected Cell cell = null;

	public Active(GameFSM fsm) {
		super(fsm);
	}

	@Override
	public void enterState() {
		fsm.data.startGameTimer();
	}

	public boolean isCleared() {
		return Grid.countHidden(fsm.data.grid) == Game.MINES;
	}

	public boolean wasExploded() {
		return Grid.countExplodedMines(fsm.data.grid) > 0;
	}

	@Override
	public void leftDoublePressed(int x, int y) {
		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.leftPressed();

		if (!cell.isFlagged()) {
			// in leftDoubleReleased() the user might notice
			// a small delay so we open the cells here instead.
			fsm.data.grid.openAdjacentCells(cell);

			if (wasExploded())
				fsm.setExplodedState();
			else if (isCleared())
				fsm.setClearedState();

		}
	}

	@Override
	public void leftDoubleReleased(int x, int y) {
		if (cell != null)
			cell.state.undoPress();

		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.leftReleased();

		fsm.autoFlagMines();
	}

	@Override
	public void leftPressed(int x, int y) {
		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.leftPressed();
	}

	@Override
	public void leftReleased(int x, int y) {
		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		boolean wasCellOpened = cell.isOpen();

		cell.state.leftReleased();

		if (!wasCellOpened && !cell.isFlagged()) {
			fsm.data.grid.openConnectedCells(cell);

			if (wasExploded())
				fsm.setExplodedState();
			else if (isCleared())
				fsm.setClearedState();
			else
				fsm.autoFlagMines();

		}
	}

	@Override
	public void rightPressed(int x, int y) {
		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.rightPressed();
	}

	@Override
	public void rightReleased(int x, int y) {
		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.rightReleased();

		fsm.data.updateUnflaggedCounter();
	}

	@Override
	public void leftDragged(int x, int y) {
		if (cell != null)
			cell.state.undoPress();

		cell = fsm.data.grid.getCellAt(x, y);
		if (cell == null)
			return;

		cell.state.leftPressed();
	}

}