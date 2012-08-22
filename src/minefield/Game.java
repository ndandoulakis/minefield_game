package minefield;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;

	static final String TITLE = "Minefield";
	static final Color BACKGROUND = Color.black;
	static final int COLS = 26;
	static final int ROWS = 21;
	static final int MINES = 75;

	GameData data = new GameData();
	GameFSM fsm = new GameFSM(data);

	GridPanel gridPanel = null;
	DisplayPanel mineDisplay = new DisplayPanel();
	DisplayPanel timeDisplay = new DisplayPanel();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Game game = new Game();

		JFrame frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public Game() {

		setBackground(BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(15, 4, 4, 4));
		setLayout(new BorderLayout(0, 15));

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		centerPanel.setBackground(BACKGROUND);

		JButton startBtn = new JButton("New!");
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				fsm.setRestartedState();
			}
		});

		centerPanel.add(startBtn);

		GameInput gameInput = new GameInput(fsm);
		gridPanel = new GridPanel(data.grid);
		gridPanel.addMouseListener(gameInput);
		gridPanel.addMouseMotionListener(gameInput);

		add(gridPanel, BorderLayout.SOUTH);
		add(centerPanel, BorderLayout.CENTER);
		add(mineDisplay, BorderLayout.WEST);
		add(timeDisplay, BorderLayout.EAST);

		data.setObserver(this);
		fsm.setRestartedState();

	}

	@Override
	public void update(Object subject) {
		mineDisplay.setNumber(data.getUnflaggedCounter());
		timeDisplay.setNumber(data.getSeconds());

		// the Panel isn't actually painted on each update call
		// so a lot of repeated updates do not hurt performance
		repaint();
	}

}
