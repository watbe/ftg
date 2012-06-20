package ftg;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private static final long serialVersionUID = -6958342102545969303L;
	Image image;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}

	ImagePanel(String ImageName) {
		this.image = Scenery.getImage(ImageName);
	}

}