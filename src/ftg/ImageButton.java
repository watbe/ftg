package ftg;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

public class ImageButton extends JButton {

	private static final long serialVersionUID = 6287003256986872767L;
	Image image;

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}

	ImageButton(String ImageName) {
		this.image = Scenery.getImage(ImageName);
		this.setContentAreaFilled(false);
		this.setOpaque(false);
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setFocusPainted(false);
	}

}
