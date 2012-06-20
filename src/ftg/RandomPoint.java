/**
 * 
 */
package ftg;

import java.awt.Point;
import java.lang.IllegalArgumentException;
import java.util.Random;

/**
 * WildWest - � 2011
 * 
 * @author Wayne Tsai
 * 
 */
public class RandomPoint extends Point {

	private static final long serialVersionUID = -2523564353139118622L;

	Game game;
	Type type;
	Integer xPos, yPos;

	/**
	 * 
	 * @param game
	 * 
	 *            Generates a random point in an empty location
	 * 
	 */
	public RandomPoint(Game game) {

		Game.d("Trying to generate RandomPoint...");

		this.game = game;

		Random random = new Random();

		int x, y;
		Type type;

		do {

			x = 2 + random.nextInt(game.mapWidth - 4); // for the borders
			y = 2 + random.nextInt(game.mapHeight - 4); // for the borders
			type = game.map.getType(x, y);

		} while (type != Type.EMPTY);

		Point point = new Point(x, y);

		this.x = point.x;
		this.y = point.y;

		this.xPos = point.x * game.mapPointSize + game.mapPointSize / 2;
		this.yPos = point.y * game.mapPointSize + game.mapPointSize / 2;

		Game.d("New RandomPoint at: " + this.x + ", " + this.y);

	}

	/**
	 * Generates a not-so-random point based on a point.
	 * 
	 * @param game
	 * @param centre
	 *            Point that is used in the calculation of a new point location
	 * @param mode
	 *            - Integer denoting the type of generation <li>0 - Generates a
	 *            point as close as possible to the centre Point</li> <li>1 -
	 *            Generates a point as far as possible from the centre Point</li>
	 */
	public RandomPoint(Game game, Point focus, Integer mode) {

		this.game = game;

		if (mode == 0) {
			closetAvailablePoint(focus);
		} else if (mode == 1) {
			furthestAvailablePoint(focus);
		} else {
			throw new IllegalArgumentException(
					"RandomPoint only takes 0 or 1 as a third argument!");
		}

	}

	public void furthestAvailablePoint(Point focus) {

		Game.d("Trying to generate furthestAvailablePoint...");

		Random random = new Random();

		Point generatedPoint = null;

		int x = 0, y = 0;

		int i = game.mapPointSize;

		Path path = null;

		int iterations = 0;

		do {
			Game.d("tryin' so hard");
			if (random.nextBoolean() == true) {
				x = random.nextInt(i);
			} else {
				x = -random.nextInt(i);
			}

			if (random.nextBoolean() == true) {
				y = random.nextInt(i);
			} else {
				y = -random.nextInt(i);
			}

			// x = focus.x + x;
			// y = focus.y + y;

			if (game.map.getType(x, y) != Type.OBSTRUCTION) {

				generatedPoint = new Point(x, y);
				this.type = game.map.getType(x, y);
				path = new Path(generatedPoint, new Point(game.player.xPos
						/ game.mapPointSize, game.player.yPos
						/ game.mapPointSize), game, null);

			}

			if (iterations > 200) {
				break;
			}

		} while (path == null || !path.hasPath
		//
		// || (Math.sqrt(Math.pow(Math.abs(x - focus.x), 2) +
		// Math.pow(Math.abs(y - focus.y), 2)) < game.mapPointSize/3)
		);

		if (iterations > 200) {
			generatedPoint = new RandomPoint(game);
		}

		this.x = generatedPoint.x;
		this.y = generatedPoint.y;

		this.xPos = generatedPoint.x * game.mapPointSize + game.mapPointSize
				/ 2;
		this.yPos = generatedPoint.y * game.mapPointSize + game.mapPointSize
				/ 2;

		Game.d("New furthestAvailablePoint at: " + this.x + ", " + this.y);

	}

	public void closetAvailablePoint(Point focus) {

		Game.d("Trying to generate closetAvailablePoint...");

		Random random = new Random();

		Point generatedPoint = null;

		int x = 0, y = 0;

		int i = 1;

		Path path;

		do {

			for (int k = 0; k < 8; k++) {

				if (random.nextBoolean() == true) {
					x = random.nextInt(i);
				} else {
					x = -random.nextInt(i);
				}

				if (random.nextBoolean() == true) {
					y = random.nextInt(i);
				} else {
					y = -random.nextInt(i);
				}

				if (game.map.getType(focus.x + x, focus.y + y) == Type.EMPTY) {
					x = focus.x + x;
					y = focus.y + y;
					generatedPoint = new Point(x, y);
					this.type = game.map.getType(focus.x + x, focus.y + y);
					break;
				}

			}

			i++;

			path = new Path(new Point(x, y), new Point(game.player.xPos
					/ game.mapPointSize, game.player.yPos / game.mapPointSize),
					game, null);

		} while (!path.hasPath);

		this.x = generatedPoint.x;
		this.y = generatedPoint.y;

		this.xPos = generatedPoint.x * game.mapPointSize + game.mapPointSize
				/ 2;
		this.yPos = generatedPoint.y * game.mapPointSize + game.mapPointSize
				/ 2;

		Game.d("New closetAvailablePoint at: " + this.x + ", " + this.y);

	}

}
