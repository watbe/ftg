package ftg;

import java.util.HashMap;
import java.util.Random;

/* 
 * WildWest - © 2011
 * Map class
 * Generates maps and checks they are solvable.
 * Thomas Lillywhite
 */

public class Map {

	Game game;
	HashMap<String, Type> hashMap = new HashMap<String, Type>();

	Integer mapPointSize;
	Integer mapWidth;
	Integer mapHeight;
	// How often it checks to draw an obstacle.
	Integer obstacleStep;
	Random generator = new Random();
	Integer maxLength = 10;
	Integer numObstructions = 1000;

	/**
	 * Constructor for maps
	 */
	public Map(Game ga) {
		game = ga;
		mapPointSize = game.mapPointSize;
		mapWidth = game.mapWidth;
		mapHeight = game.mapHeight;
		obstacleStep = game.simplicity;
		Integer type = game.mapType;
		generateMap(type);
	}

	/**
	 * Creates an new map by setting all points to Type.EMPTY
	 */
	public void initialiseMap() {
		for (Integer y = 0; y < mapHeight; y++) {
			for (Integer x = 0; x < mapWidth; x++) {
				setType(x, y, Type.EMPTY);
			}
		}
	}

	/**
	 * Creates an Obstacle at the points
	 * 
	 * @param posX
	 *            x coordinate
	 * @param posY
	 *            y coordinate
	 * @param width
	 *            width of obstacle
	 * @param height
	 *            height of obstacle
	 */
	public void generateObstruction(Integer posX, Integer posY, Integer width,
			Integer height) {
		for (Integer y = posY; y < posY + height; y++) {
			for (Integer x = posX; x < posX + width; x++) {
				setType(x, y, Type.OBSTRUCTION);
			}
		}
	}

	// new obstruction generation, trying to build walls rather than blocks.
	public void generateObstruction(Integer posX, Integer posY, Integer length) {
		Integer direction = generator.nextInt(4);

		// Wall going up
		if (direction == 0) {
			for (int i = 0; i < length; i++) {
				if (!emptyStartingPosition(posX, posY, direction)) {
					break;
				}
				if (getType(posX, posY - i) == Type.OBSTRUCTION) {
					break;
				}
				setType(posX, posY - i, Type.OBSTRUCTION);
			}
		}

		// Wall going right
		if (direction == 1) {
			for (int i = 0; i < length; i++) {
				if (!emptyStartingPosition(posX, posY, direction)) {
					break;
				}
				if (getType(posX + i, posY) == Type.OBSTRUCTION) {
					break;
				}
				setType(posX + i, posY, Type.OBSTRUCTION);

			}
		}

		// Wall going down
		if (direction == 2) {
			for (int i = 0; i < length; i++) {
				if (!emptyStartingPosition(posX, posY, direction)) {
					break;
				}
				if (getType(posX, posY + i) == Type.OBSTRUCTION) {
					break;
				}
				setType(posX, posY + i, Type.OBSTRUCTION);

			}
		}

		// Wall going left
		if (direction == 3) {
			for (int i = 0; i < length; i++) {
				if (!emptyStartingPosition(posX, posY, direction)) {
					break;
				}
				if (getType(posX - i, posY) == Type.OBSTRUCTION) {
					break;
				}
				setType(posX - i, posY, Type.OBSTRUCTION);
			}
		}
	}

	/**
	 * Generates a random map blocky type.
	 */
	public void generateObstructions(Integer type2) {

		for (Integer y = 0; y < mapHeight; y += generator.nextInt(obstacleStep)) {
			for (Integer x = 0; x < mapWidth; x += generator
					.nextInt(obstacleStep)) {
				Integer width = 1 + generator.nextInt(5);
				Integer height = 1 + generator.nextInt(5);
				Integer xWidth = (x + width);
				Integer yHeight = (y + height);
				Integer centerX = x + (width / 2);
				Integer centerY = y + (height / 2);

				// Check if the top left point is empty
				Type point1 = ((getType(x, y))) == null ? Type.OBSTRUCTION
						: (hashMap.get(x + "," + y));
				// Check if the top right point is empty
				Type point2 = ((hashMap.get(xWidth + "," + y))) == null ? Type.OBSTRUCTION
						: (hashMap.get(xWidth + "," + y));
				// Check if the bottom left point is empty
				Type point3 = ((hashMap.get(x + "," + yHeight))) == null ? Type.OBSTRUCTION
						: (hashMap.get(x + "," + yHeight));
				// Check if the bottom right point is empty
				Type point4 = ((hashMap.get(xWidth + "," + yHeight))) == null ? Type.OBSTRUCTION
						: (hashMap.get(xWidth + "," + yHeight));
				// Check the centre is empty
				Type point5 = ((hashMap.get(centerX + "," + centerY)));
				// if points are empty draw an obstruction
				if (point1 == Type.EMPTY && point2 == Type.EMPTY
						&& point3 == Type.EMPTY && point4 == Type.EMPTY
						&& point5 == Type.EMPTY) {
					generateObstruction(x, y, width, height);
				}
			}
		}
	}

	/**
	 * Generates maze maps, is called in a loop by generate map, attempting to
	 * make numObstructions walls.
	 */
	public void generateObstructions() {
		Integer targetXLoactions = (mapWidth / obstacleStep) + 1;
		Integer targetYLoactions = (mapHeight / obstacleStep) + 1;
		Integer minLength = 2;

		Integer x = 1 + generator.nextInt(targetXLoactions) * obstacleStep;
		Integer y = 1 + generator.nextInt(targetYLoactions) * obstacleStep;

		Integer length = minLength + generator.nextInt(maxLength);
		generateObstruction(x, y, length);
	}

	/**
	 * Generates a border based on the map size
	 */
	public void generateBorder() {
		for (int x = 0; x < mapWidth; x++) {
			for (int y = 0; y < mapHeight; y++) {
				if (x == 0 || x == 1 || y == 0 || y == 1 || x == mapWidth - 1
						|| x == mapWidth - 2 || y == mapHeight - 1
						|| y == mapHeight - 2) {
					setType(x, y, Type.OBSTRUCTION);
				}
			}
		}

	}

	/**
	 * Generates a new map
	 */
	public void generateMap(Integer type) {
		initialiseMap();
		generateBorder();
		if (type == 1) {
			generateObstructions(type);
		}
		if (type == 2) {
			for (int i = 0; i < numObstructions; i++) {
				generateObstructions();
			}
		}
		if (type == 3) {
			obstacleStep = 10;
			generateObstructions(type);
			obstacleStep = game.simplicity;
			for (int i = 0; i < numObstructions; i++) {
				obstacleStep = game.simplicity;
				generateObstructions();
			}
		}

	}

	/**
	 * Method to find the type of point at (x, y)
	 * 
	 * @param x
	 * @param y
	 * @return Type of point at (x, y)
	 */
	public Type pixelToGrid(Integer x, Integer y) {
		Integer gridX = x / mapPointSize;
		Integer gridY = y / mapPointSize;
		Type pixelGridType = getType(gridX, gridY);
		return pixelGridType;
	}

	/**
	 * Sets the type of the point at (x, y)
	 * 
	 * @param x
	 * @param y
	 * @param type
	 */
	public void setType(Integer x, Integer y, Type type) {
		hashMap.put(x + "," + y, type);
	}

	/**
	 * Gets the type of the point at (x, y)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Type getType(Integer x, Integer y) {
		Type type = hashMap.get(x + "," + y);
		return type;
	}

	/**
	 * Checks if a map is good based on two booleans
	 * 
	 * @param path1
	 *            result of one bool
	 * @param path2
	 *            result of another bool
	 * @return
	 */
	public boolean goodMap(boolean path) {
		return path;
	}

	/**
	 * Clears a map and generates a new random map
	 */
	public void reset(Integer type) {
		hashMap.clear();
		generateMap(type);
	}

	/**
	 * Returns true when point is empty
	 * 
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	public boolean emptyStartingPosition(Integer x, Integer y, Integer direction) {
		Integer pointLX = x - 1;
		Integer pointRX = x + 1;
		Integer pointUY = y - 1;
		Integer pointDY = y + 1;
		// Going up, check left and right side;
		if (direction == 0) {
			return (getType(x, pointDY) == Type.EMPTY);
		}

		// Going right, check above or below
		if (direction == 1) {
			return (getType(pointLX, y) == Type.EMPTY);
		}

		// Going down, check left and right side;
		if (direction == 2) {
			return (getType(x, pointUY) == Type.EMPTY);
		}

		// Going left, check above or below
		if (direction == 3) {
			return (getType(x, pointRX) == Type.EMPTY);
		}

		else
			return false;
	}

}
