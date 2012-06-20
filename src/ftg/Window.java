package ftg;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * WildWest - (c) 2011 Contains the functions to render specific sprites and
 * details
 * 
 * @author Wayne Tsai
 */
public class Window {

	public static boolean showMinimap;
	Frame frame;
	// Screen screen;
	Game game;
	Integer time;
	BufferStrategy bufferStrategy;
	Graphics2D g;
	BufferedImage scenery;
	boolean madeMinimap;
	Integer offsetX;
	Integer offsetY;
	Integer explosion;
	Point collision;
	BufferedImage minimap = null;
	Integer drawSwitch = 0;
	double radarRotation = 0;

	public Window(Game game) {

		this.game = game;

		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		frame = new Frame("Wild West");
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setVisible(true);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Launcher.class.getResource("/game/player.png")));
		InputHandler input = new InputHandler();
		frame.addKeyListener(input);
		frame.setResizable(false);
		frame.createBufferStrategy(2);
		bufferStrategy = frame.getBufferStrategy();
		device.setFullScreenWindow(frame);

	}

	public void initWindow() {

		scenery = drawScenery();
		explosion = 0;
		collision = new Point(0, 0);
		time = game.time;

	}

	public void drawFrame() {

		g = (Graphics2D) this.bufferStrategy.getDrawGraphics();

		if (game.player.hit) {
			game.player.hit = false;
			explosion = 20;
			collision = new Point(game.player.xPos, game.player.yPos);
		}

		setOffset();
		drawBackdrop();
		drawBackground();
		// drawObstacles();
		drawRadar();
		g.drawImage(scenery, offsetX, offsetY, null);

		drawBodies();
		drawInfoOverlay();

		if (showMinimap) {
			drawMinimap();
		}

		if (Game.paused) {
			drawPaused();
		}

		if (explosion > 0) {
			explosion--;
			drawExplosion();
		}

		if (!bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			g.dispose();
		}
	}

	public void drawFrame(String type) {

		this.g = (Graphics2D) this.bufferStrategy.getDrawGraphics();

		Game.d("drawing screen: " + type);

		if (type.equals("win")) {
			drawWinScreen();
		} else if (type.equals("start")) {
			drawStartScreen();
		} else if (type.equals("lose")) {
			drawLoseScreen();
		}

		if (!bufferStrategy.contentsLost()) {
			bufferStrategy.show();
			g.dispose();
		}

	}

	public void drawStartScreen() {
		g.setColor(new Color(212, 85, 0));
		g.fillRect(0, 0, game.resX, game.resY);
		g.drawImage(Scenery.getImage("start"), game.resX / 2 - 400,
				game.resY / 2 - 300, null);

		g.setColor(Color.BLACK);
		Font font = new Font("Georgia", Font.PLAIN, 36);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		drawCenteredString("Welcome back, " + Launcher.name, game.resX / 2,
				game.resY / 2 - 250, g);

		font = new Font("Georgia", Font.PLAIN, 40);
		g.setFont(font);
		drawCenteredString("Level " + game.currentLevel + ": "
				+ Levels.getLevelName(game.currentLevel - 1).substring(2),
				game.resX / 2, game.resY / 2 - 130, g);

		font = new Font("Georgia", Font.PLAIN, 24);
		g.setFont(font);
		drawCenteredString(Levels.getLevelDescription(game.currentLevel - 1),
				game.resX / 2, game.resY / 2 - 90, g);

	}

	public void drawPaused() {
		g.setColor(new Color(Integer.parseInt("361e00", 16)));
		Font font = new Font("Georgia", Font.BOLD, 48);
		g.setFont(font);
		drawCenteredString("Paused", game.resX / 2, game.resY / 2 - 200, g);
		g.drawImage(Scenery.getImage("help"), game.resX / 2 - 150,
				game.resY / 2 - 150, null);
	}

	public void drawWinScreen() {

		g.setColor(new Color(212, 85, 0));
		g.fillRect(0, 0, game.resX, game.resY);
		g.drawImage(Scenery.getImage("win"), game.resX / 2 - 400,
				game.resY / 2 - 300, null);

		g.setColor(Color.BLACK);
		Font font = new Font("Georgia", Font.PLAIN, 40);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		drawCenteredString("You have completed Level " + (game.wonLevel) + "!",
				game.resX / 2, game.resY / 2 - 160, g);

		font = new Font("Georgia", Font.PLAIN, 18);
		g.setFont(font);

		if (game.wonLevel == game.currentLevel) {

			drawCenteredString("Loading next level...", game.resX / 2,
					game.resY / 2 + 255, g);
		} else if (game.wonTime <= 0) {
			drawCenteredString("Level Loaded! {Space}", game.resX / 2,
					game.resY / 2 + 255, g);
		} else {
			drawCenteredString("Calculating score", game.resX / 2,
					game.resY / 2 + 255, g);
		}

		font = new Font("Georgia", Font.PLAIN, 40);
		g.setFont(font);

		drawCenteredString("Time: " + timeToString(game.wonTime),
				game.resX / 2, game.resY / 2 - 50, g);

		drawCenteredString("Score: " + game.score, game.resX / 2,
				game.resY / 2, g);

		if (game.wonTime > 10) {
			game.wonTime -= 10;
			game.score += 10;
		} else if (game.wonTime > 0) {
			game.wonTime -= 1;
			game.score += 1;
		}

	}

	public void drawLoseScreen() {

		g.setColor(new Color(212, 85, 0));
		g.fillRect(0, 0, game.resX, game.resY);
		g.drawImage(Scenery.getImage("lose"), game.resX / 2 - 400,
				game.resY / 2 - 300, null);

		g.setColor(Color.BLACK);
		Font font = new Font("Georgia", Font.PLAIN, 20);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		drawCenteredString("You ran out of time on Level " + (game.wonLevel)
				+ "!", game.resX / 2, game.resY / 2, g);

		font = new Font("Georgia", Font.PLAIN, 40);
		g.setFont(font);

		drawCenteredString("Final Score: " + game.score, game.resX / 2,
				game.resY / 2 + 50, g);

		font = new Font("Georgia", Font.PLAIN, 18);
		g.setFont(font);
		drawCenteredString("Press {SPACE} to exit", game.resX / 2,
				game.resY / 2 + 255, g);

	}

	public void drawExplosion() {
		g.drawImage(
				Scenery.setOpacity(Scenery.getImage("explosion",
						game.generator.nextInt(7)), ((float) explosion) / 20f),
				collision.x + offsetX - game.player.size / 2, collision.y
						+ offsetY - game.player.size / 2, null);
	}

	public void drawRadar() {
//		radarRotation -= 0.2;
//		if (game.player.radarRange <= 250) {
//			g.drawImage(Scenery.getImage("radar_250", radarRotation),
//					game.player.xPos + offsetX - 500 / 2, game.player.yPos
//							+ offsetY - 500 / 2, null);
//		} else if (game.player.radarRange > 250) {
//			g.drawImage(Scenery.getImage("radar_500", radarRotation),
//					game.player.xPos + offsetX - 500 / 2, game.player.yPos
//							+ offsetY - 500 / 2, null);
//		}
	}

	public void setOffset() {

		Player player = game.player;

		// BIGGER THAN EVERYTHING ROAOAOAOAOAR!
		if (game.mapPixelWidth >= game.resX && game.mapPixelHeight >= game.resY) {

			Integer pX = player.xPos;
			Integer pY = player.yPos;

			Integer topLeftCornerX = (pX - (game.resX / 2));
			Integer topLeftCornerY = (pY - (game.resY / 2));

			Integer bottomRightCornerX = (pX + (game.resX / 2));
			Integer bottomRightCornerY = (pY + (game.resY / 2));

			offsetX = null;

			if (topLeftCornerX < 0) {
				offsetX = 0;
			}

			if (bottomRightCornerX >= (game.mapPixelWidth)) {
				offsetX = (int) (game.mapPixelWidth - game.resX);
			}

			if (topLeftCornerX >= 0
					&& bottomRightCornerX < (game.mapPixelWidth)) {
				offsetX = topLeftCornerX;
			}

			offsetY = null;

			if (topLeftCornerY < 0) {
				offsetY = 0;
			}

			if (bottomRightCornerY >= (game.mapPixelHeight)) {
				offsetY = (int) (game.mapPixelHeight - game.resY);
			}

			if (topLeftCornerY >= 0
					&& bottomRightCornerY < (game.mapPixelHeight)) {
				offsetY = topLeftCornerY;
			}

			offsetX = -offsetX;
			offsetY = -offsetY;

		}

		// HE'S SO THIN!
		if (game.mapPixelWidth < game.resX && game.mapPixelHeight >= game.resY) {
			Integer pY = player.yPos;

			Integer topLeftCornerY = (pY - (game.resY / 2));
			Integer bottomRightCornerY = (pY + (game.resY / 2));

			offsetY = null;
			offsetX = (game.resX - game.mapPixelWidth) / 2;

			if (topLeftCornerY < 0) {
				offsetY = 0;
			}

			if (bottomRightCornerY >= (game.mapPixelHeight)) {
				offsetY = (int) (game.mapPixelHeight - game.resY);
			}

			if (topLeftCornerY >= 0
					&& bottomRightCornerY < (game.mapPixelHeight)) {
				offsetY = topLeftCornerY;
			}

			offsetY = -offsetY;
		}

		// HE'S SO SHORT~~~!
		if (game.mapPixelWidth >= game.resX && game.mapPixelHeight < game.resY) {
			Integer pX = player.xPos;
			Integer topLeftCornerX = (pX - (game.resX / 2));
			Integer bottomRightCornerX = (pX + (game.resX / 2));

			offsetX = null;
			offsetY = (game.resY - game.mapPixelHeight) / 2;

			if (topLeftCornerX < 0) {
				offsetX = 0;
			}

			if (bottomRightCornerX >= (game.mapPixelWidth)) {
				offsetX = (int) (game.mapPixelWidth - game.resX);
			}

			if (topLeftCornerX >= 0
					&& bottomRightCornerX < (game.mapPixelWidth)) {
				offsetX = topLeftCornerX;
			}
			offsetX = -offsetX;

		}

		// TINY MAP >.< ITS SO CUUUUTE
		if (game.mapPixelWidth < game.resX && game.mapPixelHeight < game.resY) {

			offsetX = (game.resX - game.mapPixelHeight) / 2;
			offsetY = (game.resY - game.mapPixelWidth) / 2;

		}

		if (explosion > 0) {

			if (game.generator.nextBoolean()) {
				offsetX += game.generator.nextInt(10);
			} else {
				offsetX -= game.generator.nextInt(10);
			}

			if (game.generator.nextBoolean()) {
				offsetY += game.generator.nextInt(10);
			} else {
				offsetY -= game.generator.nextInt(10);
			}

		}

	}

	public BufferedImage drawScenery() {
		BufferedImage scene = new BufferedImage(game.mapPixelWidth,
				game.mapPixelHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = scene.createGraphics();
		@SuppressWarnings("unused")
		Scenery scenery = new Scenery(game.map, g);
		g.dispose();
		return scene;

	}

	/**
	 * Draws the background
	 */
	public void drawBackground() {

		g.setColor(new Color(255, 219, 170));
		g.fillRect(0 + offsetX, 0 + offsetY, game.mapPixelWidth,
				game.mapPixelHeight);

	}

	public void drawBackdrop() {

		g.setColor(new Color(255, 155, 19));
		g.fillRect(0, 0, game.resX, game.resY);

	}

	// makes the background yellow when you win
	public void drawVictoryBackground() {

		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, game.mapPixelWidth, game.mapPixelHeight);

	}

	/**
	 * This method draws the obstacles
	 */
	public void drawObstacles() {

		g.setColor(Color.GRAY);

		for (Integer y = 0; y < game.mapHeight; y++) {
			for (Integer x = 0; x < game.mapWidth; x++) {

				Type type = game.map.getType(x, y);
				if (type == Type.OBSTRUCTION) {
					g.fillRect((x * game.mapPointSize) + offsetX,
							(y * game.mapPointSize) + offsetY,
							game.mapPointSize, game.mapPointSize);
				}

			}
		}

	}

	/**
	 * Draws a minimap, its pretty cool.
	 */
	public void drawMinimap() {
		Player player = game.player;
		Integer size = 6;
		Integer miniOffset = game.resX - game.mapWidth * size;
		g.setColor(Color.WHITE);
		g.fillRect(miniOffset, 0, game.mapWidth * size, game.mapHeight * size);

		for (Body body : game.bodies) {
			if (body.charType == Type.BANDIT) {
				Bandit bandit = (Bandit) body ;
				if(bandit.distance <= player.radarRange+game.mapWidth || bandit.hasLOS) {
					g.setColor(Color.BLUE);
					g.fillRect(body.xPos / game.mapPointSize * size + miniOffset,
							body.yPos / game.mapPointSize * size, 4, 4);
				}
			}

			if (body.charType == Type.ASSOCIATE) { 
				Associate associate = (Associate) body ;
				if(associate.distance <= player.radarRange+game.mapWidth || associate.hasLOS) {
					g.setColor(Color.RED);
					g.fillRect(body.xPos / game.mapPointSize * size + miniOffset,
							body.yPos / game.mapPointSize * size, 4, 4);
				}
			}

		}
		
		g.setColor(Color.GREEN);
		g.fillRect(player.xPos / game.mapPointSize * size + miniOffset,
				player.yPos / game.mapPointSize * size, size, size);

		if (!madeMinimap) {
			minimap = new BufferedImage(game.mapPixelWidth,
					game.mapPixelHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D minimapG = minimap.createGraphics();
			minimapG.setColor(Color.BLACK);
			for (Integer y = 0; y < game.mapHeight; y++) {
				for (Integer x = 0; x < game.mapWidth; x++) {
					Type type = game.map.getType(x, y);
					if (type == Type.OBSTRUCTION) {
						minimapG.fillRect((x * size) + miniOffset, (y * size),
								size, size);
					}

				}
			}
			madeMinimap = true;
		}
		g.drawImage(minimap, 0, 0, null);

	}

	public double getRotation(double x, double y) {

		double PI = Math.PI;

		if (x == 0 && y < 0) {
			return 0;
		} else if (x == 0 && y > 0) {
			return PI;
		} else if (x > 0 && y == 0) {
			return PI / 2;
		} else if (x < 0 && y == 0) {
			return 3 * PI / 2;
		} else if (x > 0 && y < 0) {
			x = Math.abs(x);
			y = Math.abs(y);
			return Math.atan(x / y);
		} else if (x > 0 && y > 0) {
			x = Math.abs(x);
			y = Math.abs(y);
			return PI / 2 + Math.atan(y / x);
		} else if (x < 0 && y > 0) {
			x = Math.abs(x);
			y = Math.abs(y);
			return PI + Math.atan(x / y);

		} else if (x < 0 && y < 0) {
			x = Math.abs(x);
			y = Math.abs(y);
			return 3 * PI / 2 + Math.atan(y / x);
		}

		return 0;

	}

	/**
	 * Draws the bodies
	 */
	public void drawBodies() {

		for (Body body : game.bodies) {

			if (body.charType == Type.BANDIT) {
				Bandit bandit = (Bandit) body;
				
				float opRange;
				if (!bandit.inRange
						&& bandit.distance < game.player.radarRange + 100) {

					opRange = 1 - ((float) Math.abs(bandit.distance
							- game.player.radarRange)) / 100f;
					
					if (opRange > 1) {
						opRange = 1f;
					} else if (opRange < 1) {
						opRange = 0f;
					}

					g.drawImage(Scenery.setOpacity(
							Scenery.getImage("bandit",
									getRotation(bandit.xVel, bandit.yVel)),
							opRange), body.xPos - body.size / 2 + offsetX,
							body.yPos - body.size / 2 + offsetY, null);
				}
				if (bandit.inRange || bandit.hasLOS) {
					g.drawImage(
							Scenery.getImage("bandit",
									getRotation(bandit.xVel, bandit.yVel)),
							body.xPos - body.size / 2 + offsetX, body.yPos
									- body.size / 2 + offsetY, null);

				}

			}

			if (body.charType == Type.ASSOCIATE) {
				Associate ass = (Associate) body;
				float opRange;
				String filename;
				if (!ass.hasLOS) {
					filename = "associate2";
				} else {
					filename = "associate";
				}
				
				if (!ass.inRange && ass.distance < game.player.radarRange + 100) {
					
					opRange = 1 - ((float) Math.abs(ass.distance - game.player.radarRange)) / 100f;
					
					if (opRange > 1) {
						opRange = 1f;
					} else if (opRange < 1) {
						opRange = 0f;
					}
					
					g.drawImage(Scenery.setOpacity(
							Scenery.getImage(filename,
									getRotation(ass.xVel, ass.yVel)), opRange),
							ass.xPos - ass.size / 2 + offsetX, ass.yPos
									- ass.size / 2 + offsetY, null);
				}
				if (ass.inRange || ass.hasLOS) {
					g.drawImage(
							Scenery.getImage(filename,
									getRotation(ass.xVel, ass.yVel)), ass.xPos
									- ass.size / 2 + offsetX, ass.yPos
									- ass.size / 2 + offsetY, null);
				}

			}

		}
		drawSwitch++;

		for (Body pick : game.pickups) {
			Pickup pickup = (Pickup) pick;
			if (pickup.effect == 0) {
				g.drawImage(Scenery.getImage("rocket" + (1 + drawSwitch % 3)),
						pickup.xPos - pickup.size / 2 + offsetX, pickup.yPos
								- pickup.size / 2 + offsetY, null);
			}
			if (pickup.effect == 1) {
				g.drawImage(
						Scenery.getImage("Health boost" + (1 + drawSwitch % 3)),
						pickup.xPos - pickup.size / 2 + offsetX, pickup.yPos
								- pickup.size / 2 + offsetY, null);
			}
			if (pickup.effect == 2) {

				g.drawImage(
						Scenery.getImage("freezeIcon" + (1 + drawSwitch % 3)),
						pickup.xPos - pickup.size / 2 + offsetX, pickup.yPos
								- pickup.size / 2 + offsetY, null);
			}
			if (pickup.effect == 3) {
				g.drawImage(Scenery.getImage("radar" + (1 + drawSwitch % 3)),
						pickup.xPos - pickup.size / 2 + offsetX, pickup.yPos
								- pickup.size / 2 + offsetY, null);
			}
		}

		Player player = game.player;

		g.drawImage(
				Scenery.getImage("player_shadow", -player.rotation + Math.PI),
				player.xPos + offsetX + 5 - player.size / 2, player.yPos
						+ offsetY + 5 - player.size / 2, null);

		if (Player.left) {
			g.drawImage(
					Scenery.getImage("player_left", -player.rotation + Math.PI),
					player.xPos + offsetX - player.size / 2, player.yPos
							+ offsetY - player.size / 2, null);
		}

		if (Player.right) {
			g.drawImage(Scenery.getImage("player_right", -player.rotation
					+ Math.PI), player.xPos + offsetX - player.size / 2,
					player.yPos + offsetY - player.size / 2, null);
		}

		if (Player.up) {
			g.drawImage(
					Scenery.getImage("player_thrust", -player.rotation
							+ Math.PI),
					player.xPos + offsetX - player.size / 2, player.yPos
							+ offsetY - player.size / 2, null);
		} else {
			g.drawImage(Scenery.getImage("player", -player.rotation + Math.PI),
					player.xPos + offsetX - player.size / 2, player.yPos
							+ offsetY - player.size / 2, null);
		}

		if (player.immune) {
			g.drawImage(Scenery.getImage("player_immune"), player.xPos
					+ offsetX - player.size / 2, player.yPos + offsetY
					- player.size / 2, null);
		}

	}

	/**
	 * Draws any path
	 * 
	 * @param path
	 */
	public void drawPath(Path path) {

		if (Game.debug == true && path != null && path.hasPath) {

			g.setColor(Color.RED);
			g.fillRect(path.startNode.xPos * game.map.mapPointSize,
					path.startNode.yPos * game.map.mapPointSize,
					game.map.mapPointSize, game.map.mapPointSize);

			for (AINode point : path.path) {

				g.setColor(Color.ORANGE);
				g.fillRect((point.xPos * game.map.mapPointSize) + offsetX,
						(point.yPos * game.map.mapPointSize) + offsetY,
						game.map.mapPointSize, game.map.mapPointSize);
				g.setColor(Color.BLACK);
				Font font = new Font("Arial", Font.BOLD, 9);
				g.setFont(font);
				int gcost = (int) (double) point.g;
				int hcost = (int) (double) point.h;
				int fcost = (int) (double) point.f;
				g.drawString("g: " + gcost, point.xPos * game.map.mapPointSize
						+ 10 + offsetX, point.yPos * game.map.mapPointSize + 15
						+ offsetY);
				g.drawString("h: " + hcost, point.xPos * game.map.mapPointSize
						+ 10 + offsetX, point.yPos * game.map.mapPointSize + 25
						+ offsetY);
				g.drawString("f: " + fcost, point.xPos * game.map.mapPointSize
						+ 10 + offsetX, point.yPos * game.map.mapPointSize + 35
						+ offsetY);

			}

			g.setColor(Color.GREEN);
			g.fillRect(path.endNode.xPos * game.map.mapPointSize + offsetX,
					path.endNode.yPos * game.map.mapPointSize + offsetY,
					game.map.mapPointSize, game.map.mapPointSize);

		}

		g.dispose();

	}

	public void drawInfoOverlay() {

		Player player = game.player;
		Integer health = player.health;
		int center = game.resX / 2 - 250;

		// Background
		g.drawImage(Scenery.getImage("overlay"), center, 0, null);

		// Health Bars
		g.setColor(Color.RED);
		for (int i = 0; i < health; i++) {
			g.drawImage(Scenery.getImage("blood"), center + 50 + (i * 22)
					+ (i * 5), 20, null);
		}
		// Empty Health
		for (int i = 0; i < 3 - health; i++) {
			g.drawImage(Scenery.getImage("bloodgrey"), center + 104 - (i * 22)
					- (i * 5), 20, null);
		}

		// Count down timer
		Font font;
		time = game.time;
		if (time > 60) {
			g.setColor(new Color(Integer.parseInt("361e00", 16)));
		} else {
			g.setColor(new Color(Integer.parseInt("ff0000", 16)));
		}
		font = new Font("Georgia", Font.BOLD, 36);
		g.setFont(font);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		drawCenteredString(timeToString(time), center + 250, 45, g);

		// Score
		String score = player.score.toString();

		g.setColor(new Color(Integer.parseInt("361e00", 16)));
		font = new Font("Georgia", Font.BOLD, 24);
		g.setFont(font);
		drawCenteredString(score, center + 350, 45, g);

		// Level
		String lvl = game.currentLevel.toString();
		drawCenteredString(lvl, center + 155, 45, g);

		// Pickup
		if (player.hasPickup) {
			if (player.currentPickup == 0) {
				g.drawImage(Scenery.getImage("boost"), center + 390, 22, null);
			}
			if (player.currentPickup == 1) {
				g.drawImage(Scenery.getImage("health"), center + 390, 22, null);
			}
			if (player.currentPickup == 2) {
				g.drawImage(Scenery.getImage("freeze"), center + 390, 22, null);
			}
			if (player.currentPickup == 3) {
				g.drawImage(Scenery.getImage("radarOverlay"), center + 390, 22,
						null);
			}
		}

	}

	public String timeToString(Integer inputTime) {

		String timePrint;
		time = inputTime;

		timePrint = "" + (time / 60 > 9 ? 1 : 0);
		if (time / 60 >= 1) {
			if (time / 60 > 9) {
				timePrint = timePrint + (time / 60) / 10;
			} else {
				timePrint = timePrint + (time / 60);
			}
		}
		timePrint = timePrint + ':';
		if (time % 60 > 9) {
			timePrint = timePrint + ((time % 60) / 10);
		}
		if (time % 60 <= 9) {
			timePrint = timePrint + (0);
		}

		if (time % 60 > 9) {
			timePrint = timePrint + ((time % 60) % 10);
		}
		if (time % 60 <= 9) {
			timePrint = timePrint + (time % 60);
		}

		return timePrint;

	}

	/**
	 * Clears graphics
	 * 
	 * @param g
	 *            Graphics2D to be cleared
	 */
	public void clear(Graphics2D g) {
		g.setBackground(new Color(255, 255, 255, 0));
		g.clearRect(0, 0, game.mapPixelWidth, game.mapPixelHeight);
	}

	public void drawCenteredString(String s, int w, int h, Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();
		int x = (w - fm.stringWidth(s) / 2);
		int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent()) / 2));
		g.drawString(s, x, y);
	}

}
