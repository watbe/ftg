package ftg;

//import java.awt.Point;

/**
 * WildWest - � 2011 Constructs Characters and has method for calculating
 * lineOfSight
 * 
 * @author Tom Sautelle
 * 
 */
public abstract class Body {

	Game game; // the game it belongs to

	int maxSpeed; // maximum speed of the character
	int xPos = 0, yPos = 0; // Character position
	int charID;
	double Acceleration;
	double xVel = 0, yVel = 0; // The characters velocity
	double aX, aY;

	Type charType; // The type of character, defined in the type class
	Integer size;
	Boolean hasLOS;
	Integer tick;
	Boolean hasPath;
	Boolean immune = false;

	static Integer IDCount = 0;

	/**
	 * Constructs a Character
	 * 
	 * @param game
	 * @param maxSpeed
	 * @param startX
	 * @param startY
	 * @param charID
	 * @param Acceleration
	 * @param charType
	 * @param vX
	 * @param vY
	 */
	public Body(Game game) {

		this.charID = IDCount;
		IDCount++;

		this.game = game;
		this.tick = 0;
		this.hasLOS = false;
		this.hasPath = false;

		relocate(game);

	}

	/**
	 * This method runs processes that happen when a body dies.
	 */
	public abstract void onDeath();

	public abstract void reset();

	/**
	 * Checks whether the maxSpeed has been exceeded and if so, reset to
	 * maxSpeed
	 */
	public void checkMaxSpeed() {

		if (xVel > maxSpeed) {
			xVel = maxSpeed;
		} else if (xVel < -maxSpeed) {
			xVel = -maxSpeed;
		}

		if (yVel > maxSpeed) {
			yVel = maxSpeed;
		} else if (yVel < -maxSpeed) {
			yVel = -maxSpeed;
		}

	}

	/**
	 * Checks to see if a character can see another character by checking the
	 * point between them for obstructions
	 * 
	 * @author Wayne Tsai & Tom Sautelle
	 */
	public boolean hasLineOfSight(Body target) {

		int increment = 10; // searches every 'number' pixels along the axis

		Boolean followX = null;

		Double grad = null;
		Double intercept = null;

		if (target.xPos == this.xPos) {

			followX = false;
			grad = 0.0;
			intercept = (double) this.xPos;

		} else if (target.yPos == this.yPos) {

			followX = true;
			grad = 0.0;
			intercept = (double) this.yPos;

		} else {

			grad = ((double) ((target.yPos - this.yPos)) / ((double) (target.xPos - this.xPos)));

			intercept = target.yPos - (grad * target.xPos);

			if (grad > 1) {

				followX = false;

			} else if (grad <= 1 && grad >= -1) {

				followX = true;

			} else if (grad < -1) {

				followX = false;

			}

		}

		if (followX) {

			int x = this.xPos;
			int y = this.yPos;

			do {

				if ((target.xPos - this.xPos) > 0) {
					x += increment;
				} else {
					x -= increment;
				}

				y = (int) ((grad * x) + intercept);

				if (game.map.pixelToGrid(x, y) == Type.OBSTRUCTION) {
					hasLOS = false;
					return false;
				}

			} while (Math.abs(target.xPos - x) > increment);

			hasLOS = true;
			return true;

		} else {

			int x = this.xPos;
			int y = this.yPos;

			do {

				if ((target.yPos - this.yPos) > 0) {
					y += increment;
				} else {
					y -= increment;
				}

				if (grad != 0) {
					x = (int) ((y - intercept) / grad);
				}

				if (game.map.pixelToGrid(x, y) == Type.OBSTRUCTION) {
					hasLOS = false;
					return false;
				}

			} while (Math.abs(target.yPos - y) > increment);

			hasLOS = true;
			return true;

		}

	}

	/**
	 * This method checks whether the character is inside an obstacle, and if it
	 * is, it moves in the opposite direction of the obstacle until it is
	 * outside
	 */
	public void checkObstacleCollision() {

		Type newPoint1;
		Type newPoint2;
		Type newPoint3;
		Type newPoint4;

		int i = 0;

		do {

			i++;
			newPoint1 = game.map.pixelToGrid(this.xPos, this.yPos + size / 2);
			newPoint2 = game.map.pixelToGrid(this.xPos + size / 2, this.yPos);
			newPoint3 = game.map.pixelToGrid(this.xPos + size, this.yPos + size
					/ 2);
			newPoint4 = game.map.pixelToGrid(this.xPos + size / 2, this.yPos
					+ size);

			if (newPoint1 == Type.OBSTRUCTION) {
				this.xPos--;
			}

			if (newPoint2 == Type.OBSTRUCTION) {
				this.yPos++;
			}

			if (newPoint3 == Type.OBSTRUCTION) {
				this.xPos++;
			}

			if (newPoint4 == Type.OBSTRUCTION) {
				this.yPos--;
			}

		} while ((newPoint1 == Type.OBSTRUCTION
				|| newPoint2 == Type.OBSTRUCTION
				|| newPoint3 == Type.OBSTRUCTION || newPoint4 == Type.OBSTRUCTION)
				&& i < 20);

	}

	/**
	 * This recursive function relocates (read: respawns) a body in a random
	 * available and valid location where there is a path to the player.
	 * 
	 * @param body
	 * @param game
	 */
	public void relocate(Game game) {

		Game.d("Attempting to relocate...");

		//Player player = game.player;
		RandomPoint randomPoint;

		//Point playerPoint = new Point(player.xPos / game.mapPointSize,
		//		player.yPos / game.mapPointSize);

		randomPoint = new RandomPoint(game/*, playerPoint, 1*/);

		xPos = randomPoint.x * game.mapPointSize + game.mapPointSize / 2;
		yPos = randomPoint.y * game.mapPointSize + game.mapPointSize / 2;

	}

	abstract public void update();

}
