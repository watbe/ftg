package ftg;

import java.awt.Point;

/*WildWest - � 2011
 * Bandit Class
 *Contains all methods unique to the bandit
 *Created by Wayne Tsai
 */

public class Bandit extends Enemy {

	boolean safePath;
	Point destination;
	int speed;

	public Bandit(Game game) {
		super(game);
		this.charType = Type.BANDIT;
		this.size = 40;
		this.maxSpeed = Config.getConfig("BanditMaxSpeed");
		relocate(game);
	}

	@Override
	public void onDeath() {
		Player player = game.player;
		xVel = 0;
		yVel = 0;
		player.score = 50 * (player.health + 1) + player.score;
		player.score = player.score >= 9999999 ? 9999999 : player.score;
		Game.victory = true;
		game.score = player.score;
	}

	/*
	 * Moves the Bandit to a new point based its AI pathing and whether it is
	 * frozen
	 */
	public void update() {
		
		hasLineOfSight(game.player);
		// Distance to player
		distance = Math.sqrt(Math.pow(this.xPos - game.player.xPos, 2)
				+ Math.pow(this.yPos - game.player.yPos, 2));

		if (distance <= game.player.radarRange) {
			this.inRange = true;
		} else {
			this.inRange = false;
		}

			// Player who we are running from/to
			Player player = game.player;

			Integer YdestinationDistanceToPlayer = 0;
			Integer XdestinationDistanceToPlayer = 0;

			if (this.safePath) {

				XdestinationDistanceToPlayer = Math.abs(player.xPos
						- destination.x * game.mapPointSize);
				YdestinationDistanceToPlayer = Math.abs(player.yPos
						- destination.y * game.mapPointSize);

			} else {

				destination = new RandomPoint(game);
				path = new Path(new Point(xPos / game.mapPointSize, yPos
						/ game.mapPointSize), destination, game, game.player);
				this.safePath = true;

			}

			while (XdestinationDistanceToPlayer < game.resX / 3
					&& YdestinationDistanceToPlayer < game.resY / 3) {

				destination = new RandomPoint(game);

				path = new Path(new Point(xPos / game.mapPointSize, yPos
						/ game.mapPointSize), destination, game, game.player);

				while (path.hasPath == false) {
					destination = new RandomPoint(game);
					path = new Path(new Point(xPos / game.mapPointSize, yPos
							/ game.mapPointSize), destination, game,
							game.player);
				}

				XdestinationDistanceToPlayer = Math.abs(player.xPos
						- destination.x * game.mapPointSize);
				YdestinationDistanceToPlayer = Math.abs(player.yPos
						- destination.y * game.mapPointSize);

			}

			path = new Path(xPos / game.mapPointSize, yPos / game.mapPointSize,
					destination.x, destination.y, game, game.player);

			if (!path.hasPath) {
				this.safePath = false;
			} else {
				getMove(path.path.get(0));
			}

			checkMaxSpeed();

		

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
	public void getMove(int x, int y) {

		if (this.xPos < x) {
			this.xPos += speed;
		}

		if (this.yPos < y) {
			this.yPos += speed;
		}

		if (this.xPos > x) {
			this.xPos -= speed;
		}

		if (this.yPos > y) {
			this.yPos -= speed;
		}

		checkObstacleCollision();
	}

	@Override
	public void reset() {
		game.bodies.remove(this);
	}

}
