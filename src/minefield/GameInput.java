package minefield;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class GameInput implements MouseInputListener {

	private boolean leftDragging = false;

	private GameFSM fsm = null;

	public GameInput(GameFSM fsm) {
		this.fsm = fsm;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (e.getButton() == MouseEvent.BUTTON1) {
			leftDragging = e.getClickCount() == 1;

			if (e.getClickCount() == 1)
				fsm.state.leftPressed(x, y);
			else
				fsm.state.leftDoublePressed(x, y);

		} else if (e.getButton() == MouseEvent.BUTTON3) {
			fsm.state.rightPressed(x, y);
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (e.getButton() == MouseEvent.BUTTON1) {
			leftDragging = false;

			if (e.getClickCount() == 1)
				fsm.state.leftReleased(x, y);
			else
				fsm.state.leftDoubleReleased(x, y);

		} else if (e.getButton() == MouseEvent.BUTTON3) {
			fsm.state.rightReleased(x, y);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (leftDragging)
			fsm.state.leftDragged(e.getX(), e.getY());

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
