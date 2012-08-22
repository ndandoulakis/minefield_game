package minefield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class GameData implements Observer {

	private Timer timer = new Timer(995, null);
	private int unflagged = 0;
	private int seconds = 0;

	Grid grid = new Grid(Game.COLS, Game.ROWS, Game.MINES);

	private Observer observer = null;

	public GameData() {
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSeconds(++seconds);
			}
		});
	}

	public void updateUnflaggedCounter() {
		unflagged = Game.MINES - Grid.countFlagged(grid);
		notifyObserver();
	}

	public int getUnflaggedCounter() {
		return unflagged;
	}

	public void setSeconds(int n) {
		seconds = n;
		notifyObserver();
	}

	public int getSeconds() {
		return seconds;
	}

	public void resetGameTimer() {
		timer.stop();
		setSeconds(0);
	}

	public void startGameTimer() {
		timer.start();
	}

	public void stopGameTimer() {
		timer.stop();
	}

	public void setObserver(Observer o) {
		observer = o;
		grid.setObserver(this);
	}

	private void notifyObserver() {
		if (observer != null)
			observer.update(this);
	}

	@Override
	public void update(Object subject) {
		notifyObserver();
	}

}
