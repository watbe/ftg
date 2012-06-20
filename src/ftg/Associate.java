package ftg;

import java.awt.Point;

/**
 * WildWest - � 2011 Contains all methods unique to Associates
 * 
 * @author Tom Sautelle & Thomas Lillywhite
 * 
 */
public class Associate extends Enemy {

	int speed = 2;

	public Associate(Game game) {

		super(game);
		this.charType = Type.ASSOCIATE;
		this.size = 30;
		this.maxSpeed = Config.getConfig("AssMaxSpeed");
	}
	@Override
	public void update() {
        // The distance to the player.
		distance = Math.sqrt(Math.pow(this.xPos - game.player.xPos, 2)
				+ Math.pow(this.yPos - game.player.yPos, 2));

		if (distance <= game.player.radarRange) {
			this.inRange = true;
		} else {
			this.inRange = false;
		}

		// Timer to freeze and thaw the associate.
		if (timerStart) {
			timer += 0.1;
			if (timer > 1.5) {
				timer = 0;
				timerStart = false;
				frozen = false;
			}

		}

		// Cannot move if he is frozen.
		if (!frozen) {
			// Player who we are running from/to
			Player player = game.player;
			hasLineOfSight(player); // generate hasLOS

			// If path is empty, remove existing path
			if (path != null) {
				if (path.path == null) {
					path = null;
				} else if (path.path.size() == 0) {
					path = null;
				}
			}

			if (hasLOS) { // if ass can see player (hasLOS), new path = to
							// player, get move on new path
				
				
				path = new Path(xPos / game.mapPointSize, yPos
						/ game.mapPointSize, player.xPos / game.mapPointSize,
						player.yPos / game.mapPointSize, game);

				// The next move to make
				if (path.hasPath) {

					getMove(path.path.get(0));

				} else {

					getMove(player.xPos, player.yPos);

				}

			} else // if ass can't see player (!hasLOS) and has path, get move
					// on existing path

			if (!hasLOS && path != null) {
				
			

				// The next move to make
				if (path.hasPath) {

					getMove(path.path.get(0));

				} else {

					getMove(player.xPos, player.yPos);

				}

			} else { // else if ass can't see player and doesn't has a path,
						// stop. (random later)

				Point character = new Point(xPos / game.mapPointSize, yPos
						/ game.mapPointSize);

				RandomPoint randomPoint = new RandomPoint(game);

				path = new Path(character, randomPoint, game, null);

				// The next move to make
				if (path.hasPath) {

					getMove(path.path.get(0));

				}

			}

			checkMaxSpeed();

		}

	}

	@Override
	public void getMove(AINode nextMove) {

		int xDest = nextMove.xPos * game.mapPointSize + game.mapPointSize / 2;
		int yDest = nextMove.yPos * game.mapPointSize + game.mapPointSize / 2;
		
		// Steering Function

		double desiredVelocityX = (xDest - xPos)
				* maxSpeed
				/ Math.sqrt(Math.pow(xDest - xPos, 2)
						+ Math.pow(yDest - yPos, 2));
		double desiredVelocityY = (yDest - yPos)
				* maxSpeed
				/ Math.sqrt(Math.pow(xDest - xPos, 2)
						+ Math.pow(yDest - yPos, 2));

		aX = desiredVelocityX - xVel / 10;
		aY = desiredVelocityY - yVel / 10;

		xVel += aX;
		yVel += aY;

		checkMaxSpeed();

		xPos += xVel;
		yPos += yVel;

		if (Math.abs(this.xPos - xDest) < 10
				&& Math.abs(this.yPos - yDest) < 10) {
			Game.d("reached node");
			this.path.path.remove(0);
		}

	}

	@Override
	public void getMove(int charX, int charY) {

		if (this.xPos < charX) {
			this.xPos += maxSpeed;
		}

		if (this.yPos < charY) {
			this.yPos += maxSpeed;
		}

		if (this.xPos > charX) {
			this.xPos -= maxSpeed;
		}

		if (this.yPos > charY) {
			this.yPos -= maxSpeed;
		}

		checkObstacleCollision();

	}

	@Override
	public void onDeath() {

		path = null;

		// If the player is immune the associate goes straight through. 
		if (!game.player.immune) {

			game.player.health -= 1;
			game.player.hit = true;
			game.player.xVel = 0;
			game.player.yVel = 0;
			game.player.immune = true;
			game.bodies.timerStart = true;

			relocate(game);
			//crash sound
			Sound.playAudio(Sound.crash);

		}

	}

	@Override
	public void reset() {
		game.bodies.remove(this);
	}

}
