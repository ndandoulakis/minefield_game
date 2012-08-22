package minefield;

import java.awt.BorderLayout;

import javax.swing.JApplet;

public class GameApplet extends JApplet {
	private static final long serialVersionUID = 1L;
	
	private Game game = new Game();

	public void init() {
		
		setSize(476, 440);
		add(game, BorderLayout.CENTER);
		
	}
}
