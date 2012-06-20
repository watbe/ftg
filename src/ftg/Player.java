package ftg;

/*
 * WildWest - (c) 2011
 * Player Class
 * Extends the Character class to translate user input into moves
 * Created by Tom Sautelle & Thomas Lillywhite
 */

public class Player extends Body {
    // Input booleans. 
	static boolean left = false;
	static boolean right = false;
	static boolean up = false;
	static boolean down = false;
	static boolean r = false;
	static boolean usePickup = false;
	
	public Integer radarRange;

	boolean hit; // Whether the player has been hit.
	Integer health;

	Integer currentPickup;
	boolean hasPickup;
	boolean boosting = false;
	boolean timerStart = false;
	double timer = 0;
	Integer score;

	Integer startX, startY;

	double rotation;
	double thrust;

	Game game;

	/**
	 * Constructs a player using the character superclass
	 * 
	 * @param game
	 * @param maxSpeed
	 * @param charX
	 * @param charY
	 * @param charID
	 * @param Acceleration
	 * @param charType
	 * @param vX
	 * @param vY
	 */
	public Player(Game game) {

		super(game);

		this.startX = this.xPos;
		this.startY = this.yPos;
		this.maxSpeed = Config.getConfig("playerMaxSpeed");
		this.radarRange = Config.getConfig("playerRadarRange");
		this.charType = Type.PLAYER;
		this.game = game;
		this.size = 30;
		health = 3;
		hit = false;
		hasPickup = false;
		score = 1;
		rotation = 0;
		thrust = 0;
		immune = true ;

	}

	/**
	 * Moves the player to a new point based on its velocity and acceleration.
	 * This is modified if it hits an obstruction on any of its four points.
	 */

	public void update() {

		if (health < 0) {

			xPos = this.startX;
			yPos = this.startY;
			this.xVel = 0;
			this.yVel = 0;
			this.aX = 0;
			this.aY = 0;
			this.hit = false;
			this.thrust = 0;
			this.rotation = 0;

			health = 3;

			if (score > 0) {
				score = score / 2;
			}
			timer = -2.0;
		}

		if (timerStart) {
			timer += 0.1;
			if (timer > 3) {
				timer = 0;
				timerStart = false;
				boosting = false;
				maxSpeed -= 4;
				Acceleration -= 1;
			}
		}

		// collision based on static box model
		Type top = game.map.pixelToGrid(xPos + size / 4, yPos - size / 2);
		Type top1 = game.map.pixelToGrid(xPos - size / 4, yPos - size / 2);
		
		Type bottom = game.map.pixelToGrid(xPos + size / 4, yPos + size / 2);
		Type bottom1 = game.map.pixelToGrid(xPos - size / 4, yPos + size / 2);
		
		Type left = game.map.pixelToGrid(xPos - size / 2, yPos + size / 4);
		Type left1 = game.map.pixelToGrid(xPos - size / 2, yPos - size / 4);
		
		Type right = game.map.pixelToGrid(xPos + size / 2, yPos + size / 4);
		Type right1 = game.map.pixelToGrid(xPos + size / 2, yPos - size / 4);
		
		Type middle = game.map.pixelToGrid(xPos, yPos);

		if (middle.equals(Type.OBSTRUCTION)) {
			relocate(game);
		}

		if (top.equals(Type.OBSTRUCTION) || top1.equals(Type.OBSTRUCTION)) {
			rotation = Math.PI - rotation;
			yVel = -yVel;
			do {
				yPos++;
				top = game.map.pixelToGrid(xPos, yPos - size / 2);
			} while (top.equals(Type.OBSTRUCTION));
		} else if (bottom.equals(Type.OBSTRUCTION) || bottom1.equals(Type.OBSTRUCTION)) {
			rotation = Math.PI - rotation;
			yVel = -yVel;
			do {
				yPos--;
				bottom = game.map.pixelToGrid(xPos, yPos + size / 2);
			} while (bottom.equals(Type.OBSTRUCTION));
		}

		if (left.equals(Type.OBSTRUCTION) || left1.equals(Type.OBSTRUCTION)) {
			rotation = 2 * Math.PI - rotation;
			xVel = -xVel;
			do {
				xPos++;
				left = game.map.pixelToGrid(xPos - size / 2, yPos);
			} while (left.equals(Type.OBSTRUCTION));
		} else if (right.equals(Type.OBSTRUCTION) || right1.equals(Type.OBSTRUCTION)) {
			rotation = 2 * Math.PI - rotation;
			xVel = -xVel;
			do {
				xPos--;
				right = game.map.pixelToGrid(xPos + size / 2, yPos);
			} while (right.equals(Type.OBSTRUCTION));
		}

		xPos = (int) (xPos + xVel);
		yPos = (int) (yPos + yVel);

		GetMove();

	}

	/**
	 * Uses key input to move player and do other button press things.
	 */
	public void GetMove() {
		if (validMove()) {
			if (right && !hit) {
				rotation -= Math.PI / 24;
				// aX += Acceleration;
			}

			if (left && !hit) {
				rotation += Math.PI / 24;
				// aX -= Acceleration;
			}
		}

		if (up && !hit) {
			thrust += 0.5;
			// aY -= Acceleration;
		}

		if (!up) {
			thrust -= 0.8;
		}

		if (down || hit) {
			thrust -= 1;

			// aY += Acceleration;
		}

		if (thrust < 0) {
			thrust = 0;
		} else if (thrust > maxSpeed) {
			thrust = maxSpeed;
		}

		if (r) {
			xPos = game.playerStartX;
			yPos = game.playerStartY;
		}

		if (usePickup) {
			if (hasPickup) {
				usePickup();
			}
		}

		xVel = Math.sin(rotation) * thrust;
		yVel = Math.cos(rotation) * thrust;

		checkMaxSpeed();
	}

	/**
	 * Checks that the player is in a valid position on the screen
	 * 
	 * @return
	 */
	public boolean validMove() {
		boolean rtn;
		int noseX = (int) (xPos - (size / 2) + Math.sin(rotation) * (size / 2) + size / 2);
		int noseY = (int) (yPos - (size / 2) + Math.cos(rotation) * (size / 2) + size / 2);

		int leftWingX = (int) (xPos - (size / 2)
				+ Math.sin(rotation + (Math.PI / 2)) * (size / 2) + size / 2);
		int leftWingY = (int) (yPos - (size / 2)
				+ Math.cos(rotation + (Math.PI / 2)) * (size / 2) + size / 2);

		int rightWingX = (int) (xPos - (size / 2)
				+ Math.sin(rotation - (Math.PI / 2)) * (size / 2) + size / 2);
		int rightWingY = (int) (yPos - (size / 2)
				+ Math.cos(rotation - (Math.PI / 2)) * (size / 2) + size / 2);

		Type currentPoint0 = game.map.pixelToGrid(noseX, noseY);
		Type currentPoint1 = game.map.pixelToGrid(leftWingX, leftWingY);
		Type currentPoint2 = game.map.pixelToGrid(rightWingX, rightWingY);

		rtn = (currentPoint0 == Type.EMPTY) && (currentPoint1 == Type.EMPTY)
				&& (currentPoint2 == Type.EMPTY);
		return rtn;
	}

	/**
	 * Uses a pickup, effects: 0 - SpeedBoost.....  1 - health kit..... 2 - Freeze.....
	 * 4 - Radar Range boost.....
	 */
	public void usePickup() {

		if (currentPickup == 0) {
			if (!boosting) {
				maxSpeed += 4;
				Acceleration += 1;
				timerStart = true;
				boosting = true;
				hasPickup = false;
				Sound.playAudio(Sound.boost);
			}
		}
		
		if (currentPickup == 1) {
			if (health < 3) {
				health += 1;
				hasPickup = false;
				Sound.playAudio(Sound.shield);
			}
		}

		if (currentPickup == 2) {
			for (int i = 1; i < game.bodies.size(); i++) {
				if (game.bodies.get(i).charType == Type.ASSOCIATE) {
					Associate character = (Associate) game.bodies.get(i);
					character.frozen = true;
					character.timerStart = true;
					hasPickup = false;
					Sound.playAudio(Sound.freeze);
				}
			}
		}
		
		if (currentPickup == 3) {
			radarRange = 500;/*(int) (radarRange * 1.5);*/
			hasPickup = false;
			Sound.playAudio(Sound.sonar);
		}
		
	}

	@Override
	public void relocate(Game game) {

		Game.d("Attempting to relocate player...");

		RandomPoint randomPoint = new RandomPoint(game);

		xPos = randomPoint.x * game.mapPointSize + game.mapPointSize / 2;
		yPos = randomPoint.y * game.mapPointSize + game.mapPointSize / 2;

	}

	@Override
	public void onDeath() {
	}

	@Override
	public void reset() {
		this.xPos = 0;
		this.yPos = 0;
		this.aX = 0;
		this.aY = 0;
		this.xVel = 0;
		this.yVel = 0;
		relocate(game);
	}
}