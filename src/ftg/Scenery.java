package ftg;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * WildWest - � 2011 This class renders the scenery for the game.
 * 
 * @author Wayne Tsai
 * 
 */
public class Scenery {

	Map map;
	Graphics2D g;

	/**
	 * This method
	 * 
	 * @param paintMap
	 * @param g
	 */

	public Scenery(Map paintMap, Graphics2D g) {
		map = paintMap;
		this.g = g;
		renderScenery();
	}

	/**
	 * This method creates the ids for the sprites. The ids are assigned as
	 * follows:
	 * 
	 * Where O is an obstacle, E is empty and X is the sprite being drawn:
	 * 
	 * O O O E X O => 111 01 001 => 11101001 E E O
	 */
	public void renderScenery() {

		for (Integer y = 0; y < map.mapHeight; y++) {

			for (Integer x = 0; x < map.mapWidth; x++) {

				if (map.getType(x, y) == Type.OBSTRUCTION) {

					String id = "";

					for (int i = 0; i < 9; i++) {

						int j;
						int k;

						switch (i) {

						case 5:
							j = -1;
							k = 1;
							break;

						case 6:
							j = 0;
							k = 1;
							break;

						case 7:
							j = 1;
							k = 1;
							break;

						case 3:
							j = -1;
							k = 0;
							break;

						case 4:
							j = 1;
							k = 0;
							break;

						case 0:
							j = -1;
							k = -1;
							break;

						case 1:
							j = 0;
							k = -1;
							break;

						case 2:
							j = 1;
							k = -1;
							break;

						default:
							continue;

						} // End Switch Case

						if (map.getType(x + j, y + k) == Type.EMPTY) {
							id = id + "0";
						} else {
							id = id + "1";
						}

					} // End For Adjacent search

					drawSceneSprite(id, x * map.mapPointSize, y
							* map.mapPointSize);

				}

			}

		}

	}

	/**
	 * This method draws the required sprite based on the id passed to it.
	 * 
	 * @param id
	 * @param x
	 * @param y
	 */
	public void drawSceneSprite(String id, int x, int y) {

		if (idEquals(id, "0x0x1010")) {
			g.drawImage(getImage("corner1", 0), x, y, null);

		} else if (idEquals(id, "0x01x010")) {
			g.drawImage(getImage("corner1", 1), x, y, null);

		} else if (idEquals(id, "0101x0x0")) {
			g.drawImage(getImage("corner1", 2), x, y, null);

		} else if (idEquals(id, "010x10x0")) {
			g.drawImage(getImage("corner1", 3), x, y, null);

		} else if (idEquals(id, "0x011010")) {
			g.drawImage(getImage("edge1", 0), x, y, null);

		} else if (idEquals(id, "0101x010")) {
			g.drawImage(getImage("edge1", 1), x, y, null);

		} else if (idEquals(id, "010110x0")) {
			g.drawImage(getImage("edge1", 2), x, y, null);

		} else if (idEquals(id, "010x1010")) {
			g.drawImage(getImage("edge1", 3), x, y, null);

		} else if (idEquals(id, "0x0110x0")) {
			g.drawImage(getImage("edge2", 0), x, y, null);

		} else if (idEquals(id, "010xx010")) {
			g.drawImage(getImage("edge2", 1), x, y, null);

		} else if (idEquals(id, "0x0110x0")) {
			g.drawImage(getImage("edge2", 2), x, y, null);

		} else if (idEquals(id, "010xx010")) {
			g.drawImage(getImage("edge2", 3), x, y, null);

		} else if (idEquals(id, "0x000010")) {
			g.drawImage(getImage("cap1", 0), x, y, null);

		} else if (idEquals(id, "0001x000")) {
			g.drawImage(getImage("cap1", 1), x, y, null);

		} else if (idEquals(id, "010000x0")) {
			g.drawImage(getImage("cap1", 2), x, y, null);

		} else if (idEquals(id, "000x1000")) {
			g.drawImage(getImage("cap1", 3), x, y, null);

		} else if (idEquals(id, "0x0xx0x0")) {
			g.drawImage(getImage("cactus1"), x, y, null);

		} else {
			// draw default square
			g.setColor(new Color(255, 155, 19));
			g.fillRect(x, y, 80, 80);
		}
		// Choose a random decoration to draw
		Random ran = new Random();
		if (ran.nextInt(6) == 5) {

			ArrayList<String> obstacleDecorations = new ArrayList<String>();

			obstacleDecorations.add("bone1");
			obstacleDecorations.add("bone1");
			obstacleDecorations.add("bone2");
			obstacleDecorations.add("bone2");
			obstacleDecorations.add("skull1");

			g.drawImage(getImage(obstacleDecorations), x + 13, y + 13, null);

		} else if (ran.nextInt(6) == 1) {
			g.drawImage(getImage("tree1"), x + 13, y + 13, null);
		}

	}

	public static boolean idEquals(String id, String compare) {

		String someString = "1";
		char one = someString.charAt(0);

		someString = "x";
		char x = someString.charAt(0);

		someString = "0";
		char zero = someString.charAt(0);

		for (int i = 0; i < 8; i++) {

			if (compare.charAt(i) == one) {
				if (id.charAt(i) != one) {
					return false;
				}
			} else if (compare.charAt(i) == x) {
				if (id.charAt(i) != zero) {
					return false;
				}
			}

		}

		return true;

	}

	/**
	 * This method draws the required image based on the file name. File
	 * extension is defined here to save characters...
	 * 
	 * @param filename
	 * @return
	 */
	public static BufferedImage getImage(String filename) {

		BufferedImage img = null;

		try {
			img = ImageIO.read(WildWest.class.getResource(filename + ".png"));
		} catch (Exception e) {
			Game.d("Error: " + filename + ".png does not exist!");
			e.printStackTrace();
		}

		return img;

	}

	/**
	 * This method extends on getImage by allowing an argument to define
	 * rotation in PI/2 increments
	 * 
	 * @param filename
	 * @param rotate
	 *            - 0 = no rotation, 1 = PI/2, 2 = PI, 3 = 3*PI/2
	 * @return
	 */
	public static BufferedImage getImage(String filename, int rotate) {

		BufferedImage img = null;

		try {
			img = ImageIO.read(WildWest.class.getResource(filename + ".png"));
		} catch (Exception e) {
			Game.d("Error: " + filename + ".png does not exist!");
			e.printStackTrace();
		}

		if (rotate != 0) {

			AffineTransform tx;
			AffineTransformOp op;

			switch (rotate) {

			case 1:
				tx = new AffineTransform();
				tx.rotate(Math.PI / 2, img.getWidth() / 2, img.getHeight() / 2);
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				img = op.filter(img, null);

				break;

			case 2:
				tx = new AffineTransform();
				tx.rotate(Math.PI, img.getWidth() / 2, img.getHeight() / 2);
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				img = op.filter(img, null);

				break;

			case 3:
				tx = new AffineTransform();
				tx.rotate(3 * Math.PI / 2, img.getWidth() / 2,
						img.getHeight() / 2);
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
				img = op.filter(img, null);

				break;

			default:
				break;
			}

		}

		return img;

	}

	/**
	 * This method extends on getImage by allowing an argument to define
	 * rotation in a double radian
	 * 
	 * @param filename
	 * @param rotate
	 *            - double
	 * @return
	 */
	public static BufferedImage getImage(String filename, double rotate) {

		BufferedImage img = null;

		try {
			img = ImageIO.read(WildWest.class.getResource(filename + ".png"));
		} catch (Exception e) {
			Game.d("Error: " + filename + ".png does not exist!");
			e.printStackTrace();
		}

		AffineTransform tx;
		AffineTransformOp op;

		tx = new AffineTransform();
		tx.rotate(rotate, img.getWidth() / 2, img.getHeight() / 2);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		img = op.filter(img, null);

		return img;

	}

	/**
	 * If a list of filenames are passed to getImage, it will pick a random one
	 * and return the image.
	 * 
	 * @param stringList
	 *            An ArrayList of filenames
	 * @return
	 */
	public static BufferedImage getImage(ArrayList<String> stringList) {
		Random ran = new Random();
		return getImage(stringList.get(ran.nextInt(stringList.size())),
				ran.nextInt(3) + 1);
	}

	/**
	 * Takes a BufferedImage and changes its transparency based on a number and
	 * returns it.
	 * 
	 * @param image
	 * @param transparency
	 * @return
	 */
	public static BufferedImage setOpacity(BufferedImage image,
			float transparency) {

		// Create a new image
		BufferedImage aimg = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		// Get the images graphics
		Graphics2D g = aimg.createGraphics();

		// Set the Graphics composite to Alpha
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				transparency));

		// Draw the image into the new image
		g.drawImage(image, null, 0, 0);

		// let go of all system resources in this Graphics
		g.dispose();

		// Return the image
		return aimg;

	}
}
