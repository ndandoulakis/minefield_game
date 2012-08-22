package minefield;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class DisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static BufferedImage image;
	static {
		try {
			image = ImageIO.read(DisplayPanel.class
					.getResourceAsStream("/digits.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static final int WIDTH = 18;
	private static final int HEIGHT = 24;
	private static final int DIGITS = 3;
	
	private Observer observer = null;

	private int[] digits = { 0, 0, 0 };

	DisplayPanel() {
		Dimension size = new Dimension(DIGITS * DisplayPanel.WIDTH,
				DisplayPanel.HEIGHT);
		setPreferredSize(size);

	}
	
	public void setObserver(Observer o) {
		observer = o;
	}
	
	private void notifyObserver() {
		if (observer != null)
			observer.update(this);
	}

	public void setNumber(int n) {
		n = Math.max(-99, Math.min(n, 999));

		int abs = Math.abs(n);
		digits[2] = abs % 10;

		abs /= 10;
		digits[1] = abs % 10;

		abs /= 10;
		digits[0] = abs % 10;

		if (n < 0)
			digits[0] = 10;
		
		notifyObserver();

	}
	
	public void paintComponent(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < 3; i++) {
			int d = digits[i];
			g2d.drawImage(image, 1 + i * WIDTH, 1 + 0, 1 + (i + 1) * WIDTH,
					1 + HEIGHT, d * WIDTH, 0, (d + 1) * WIDTH, HEIGHT, null);

		}
	}

}
