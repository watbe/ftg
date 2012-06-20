package ftg;

import java.awt.Point;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * WildWest - (c) 2011 This class handles the actual game when it's played.
 * 
 * @author Wayne Tsai
 * 
 */

public class Game {

	/**
	 * Variables taken from Config
	 */
	static Boolean debug = (Config.getConfig("debug") == 1) ? true : false;

	Integer mapPointSize; // The size of each square on the map.
	Integer simplicity; // How often it tries to draw obstructions.
	Integer mapWidth;
	Integer mapHeight;
	Integer numberBaddies;
	Integer resX; // The screen resolution
	Integer resY; // The screen resolution
	Integer mapType; // The type of map, 1 blocky, 2 maze, 3 x-maps
	Integer maxPickups; // Total pickups ever on the map
	Integer pickupChance; // Larger the number lower the chance of a pickup being created
	Integer mapPixelWidth; 
	Integer mapPixelHeight;

	Random generator = new Random();
	Integer playerStartX;
	Integer playerStartY;

	Bodies bodies;
	Bodies pickups;

	// Key input booleans.
	public static boolean keyReset = false;
	public static boolean exit = false;
	public static boolean paused = false;
	public static boolean enter = false;
	public static boolean spacePressed = false;

	public Integer currentLevel;
	public Integer wonLevel;
	public Integer wonTime;
	public int score;

	public static String gameState;

	public static boolean victory;

	Map map;
	Window window;
	Timer timer;
	Integer time;
	Player player;

	
	
	public Game() {

		currentLevel = Launcher.currentLevel;

		score = 0;

		window = new Window(this);

		initGame();

		// Initialise Timers
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				drawWindow();
			}
		}, 0, (long) 1000 * 1 / Config.getConfig("framerate"));
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (!paused) {
					runGame();
				}
			}
		}, 0, Config.getConfig("gameSpeed"));
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (!paused && gameState.equals("playing")) {
					time--;
				}
			}
		}, 0, 1000);
	
		
		
		Sound.playAudio(Sound.backgroundMusic);
		Sound.playAudio(Sound.backgroundMusic2);
		Sound.playAudio(Sound.backgroundMusic3);

		spacePressed = false;

	}

	/**
	 * Initialises the game.
	 */
	public void initGame() {

		Launcher.levels.loadLevel(currentLevel);

		// Loads from config file.
		mapPointSize = Config.getConfig("mapPointSize");
		simplicity = Config.getConfig("simplicity");
		mapWidth = Config.getConfig("mapWidth");
		mapHeight = Config.getConfig("mapHeight");
		numberBaddies = Config.getConfig("numberBaddies");
		resX = Config.getConfig("resX");
		resY = Config.getConfig("resY");
		mapType = Config.getConfig("mapType");
		maxPickups = Config.getConfig("maxPickups");
		pickupChance = Config.getConfig("pickupChance");
		
		//If it's reloading the same level, the time counts down (doesn't reset)
		if(wonLevel != currentLevel) {
			time = Config.getConfig("timeLimit");
		}
		
		
		mapPixelWidth = mapWidth * mapPointSize;
		mapPixelHeight = mapHeight * mapPointSize;

		Path path1;
		Path path2;

		do {

			map = new Map(this);
			Point topLeft = new Point(4, 4);
			Point topRight = new Point(mapWidth - 4, 4);
			Point bottomLeft = new Point(4, mapHeight - 4);
			Point bottomRight = new Point(mapWidth - 4, mapHeight - 4);
			path1 = new Path(topLeft, bottomRight, this, null);
			path2 = new Path(topRight, bottomLeft, this, null);

		} while (!path1.hasPath || !path2.hasPath);

		bodies = new Bodies();
		pickups = new Bodies();
		createCharacters();
		gameState = "ready";
		window.initWindow();
	}

	/**
	 * Resets the game.
	 */
	public void resetGame() {
		player.reset();
		map.hashMap.clear();
		bodies.clear();
		pickups.clear();
		bodies = null;
		pickups = null;

		if (currentLevel != Launcher.levels.numberOfLevels) {
			currentLevel++;
		} 

		initGame();
		spacePressed = false;
		gameState = "ready";
	}

	/**
	 * This method is to update graphics after the initial drawing of the map
	 */
	public void runGame() {
		
		

		if (gameState.equals("win")) {
			if (spacePressed && wonTime == 0) {
				gameState = "ready";
				spacePressed = false;
			}
		}

		if (gameState.equals("ready")) {
			
			if (spacePressed) {
				Sound.themeMusic.stopAudio();
				
				gameState = "playing";
				spacePressed = false;
			
			}
		}

		// If game is running
		if (gameState.equals("playing") && !paused) {

			// Move all the bodies
			for (int i = 0; i < bodies.size(); i++) {
				bodies.get(i).update();
			}

			player.update();

			generatePickup();

			bodies.characterCollision(this);
			pickups.characterCollision(this);

		}

		// resets the map.
		if (keyReset) {
			wonLevel = currentLevel;
			drawWindow();
			gameState = "lose";
			if(score > 0) {
				Launcher.scores.sortedAdd(Launcher.name, score);
			}
			Launcher.quitGame();
			Sound.backgroundMusic.stopAudio();
			Sound.backgroundMusic2.stopAudio();
			Sound.backgroundMusic3.stopAudio();
			
		}

		// resets the map.
		if (victory) {
			wonLevel = currentLevel;
			wonTime = time;
			gameState = "win";
			window.madeMinimap = false;
			drawWindow();
			victory = false;
			spacePressed = false;
			resetGame();
			gameState = "win";
		}

		if (time <= 0) {
			wonLevel = currentLevel;
			drawWindow();
			gameState = "lose";
			if (spacePressed) {
				if(score > 0) {
					Launcher.scores.sortedAdd(Launcher.name, score);
				}
				Launcher.quitGame();
			}
		}

	}

	/**
	 * Draws the different types of window.
	 */
	public void drawWindow() {

		if (gameState.equals("playing")) {
			window.drawFrame();
		} else if (gameState.equals("win")) {
			window.drawFrame("win");
		} else if (gameState.equals("ready")) {
			window.drawFrame("start");
		} else if (gameState.equals("lose")) {
			window.drawFrame("lose");
		}

	}

	/**
	 * Sets up the characters
	 */
	public void createCharacters() {

		player = new Player(this);

		player.score = score;

		for (int i = 0; i < numberBaddies; i++) {
			bodies.add(new Associate(this));
		}

		bodies.add(new Bandit(this));

	}

	/**
	 * Generates a pickup, boost and health have more chance of happening then radar or freeze.
	 */
	public void generatePickup() {

		Integer pickupChance =  generator.nextInt(this.pickupChance);
		if (pickupChance == 0 || pickupChance == 1 || pickupChance == 2
				|| pickupChance == 3 || pickupChance == 4 || pickupChance == 8
				|| pickupChance == 5) {
			Pickup pickup = new Pickup(this, pickupChance % 4);
			if (pickups.size() > maxPickups) {
				pickups.remove(0);
			}
			pickups.add(pickup);
			Game.d("Picked up a pickup");
			pickup.relocate(this);
		}

	}

	/**
	 * These are the debugging functions. Use d(String) to print a debug
	 * message.
	 * 
	 * @param string
	 */
	public static void d(String string) {
		if (debug) {
			System.out.println(string);
		}
	}

	public static void d(Integer i) {
		if (debug) {
			System.out.println(i);
		}
	}

}