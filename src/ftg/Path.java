package ftg;

import java.awt.Point;
import java.util.ArrayList;

/**
 * WildWest - � 2011 This object is denoted by a start point and end point.
 * Upon creation, it attempts to find a path between the two points specified.
 * It stores the result of whether it has a path within hasPath.
 * 
 * @author Wayne Tsai
 */
public class Path {

	AINode startNode;
	AINode endNode;

	ArrayList<AINode> path = new ArrayList<AINode>();
	ArrayList<AINode> openList = new ArrayList<AINode>();
	ArrayList<AINode> closedList = new ArrayList<AINode>();

	Boolean hasPath;
	Game game;
	Map map;
	Player player = null;
	Double straightCost = 10.0;
	Double diagonalCost = Math.sqrt(2) * 10;

	/**
	 * Constructor for new path takes in (x, y) coordinates and a game. It
	 * attempts to find a path based on the game's map.
	 * 
	 * @param s
	 *            startX
	 * @param t
	 *            startY
	 * @param e
	 *            endX
	 * @param f
	 *            endY
	 * @param ga
	 *            Game
	 */
	public Path(Integer s, Integer t, Integer e, Integer f, Game game) {

		this.game = game;
		map = game.map;

		startNode = new AINode(s, t);
		endNode = new AINode(e, f);

		// If start and end positions are equal, skip search
		if (startNode.xPos == endNode.xPos && startNode.yPos == endNode.yPos) {

			hasPath = false;
			path = null;

		} else {

			hasPath = findPath();

		}

	}

	/**
	 * Constructor for Path that takes in a player for Influence maps
	 * 
	 * @param s
	 * @param t
	 * @param e
	 * @param f
	 * @param ga
	 * @param player
	 */
	public Path(Integer s, Integer t, Integer e, Integer f, Game game,
			Player player) {

		this.game = game;
		this.player = player;

		map = game.map;

		startNode = new AINode(s, t);
		endNode = new AINode(e, f);

		// If start and end positions are equal, skip search
		if (startNode.xPos == endNode.xPos && startNode.yPos == endNode.yPos) {

			hasPath = false;
			path = null;

		} else {

			hasPath = findPath();

		}

	}

	/**
	 * Constructor for Path based on start and end Points
	 * 
	 * @param start
	 * @param end
	 * @param ga
	 * @param pla
	 */
	public Path(Point start, Point end, Game game, Player player) {

		this.game = game;
		this.player = player;

		map = game.map;

		startNode = new AINode(start.x, start.y);
		endNode = new AINode(end.x, end.y);

		// If start and end positions are equal, skip search
		if (startNode.xPos == endNode.xPos && startNode.yPos == endNode.yPos) {

			hasPath = false;
			path = null;

		} else {

			hasPath = findPath();

		}

	}

	/**
	 * This functions finds the path. Note that it is automatically called when
	 * a new path is created and thus shouldn't be run manually.
	 * 
	 * @return Boolean depending on whether there is a path or not.
	 */
	public boolean findPath() {

		AINode searchNode = startNode;

		// Initialise
		searchNode.g = 0.0;

		if (player == null) {
			searchNode.h = heuristicCost(searchNode, endNode);
		} else {
			searchNode.h = heuristicCost(searchNode, endNode, player);
		}

		searchNode.f = searchNode.g + searchNode.h;

		openList.add(searchNode);

		while (!openList.isEmpty()) {

			searchNode = openList.get(getCheapestPoint(openList));

			if (searchNode.xPos == endNode.xPos
					&& searchNode.yPos == endNode.yPos) {

				path = reconstruct_path(searchNode);
				endNode.parentNode = searchNode.parentNode;

				// GameWrapper.d("Path Found!");

				return true;

			}

			openList.remove(searchNode);
			closedList.add(searchNode);

			// Generates the adjacent points
			for (Integer i = 0; i < 8; i++) {

				Integer x = 0;
				Integer y = 0;

				switch (i) {
				case 0:
					if ((game.map.getType(searchNode.xPos, searchNode.yPos + 1) == Type.EMPTY)
							&& (game.map.getType(searchNode.xPos - 1,
									searchNode.yPos) == Type.EMPTY)) {
						x = searchNode.xPos - 1;
						y = searchNode.yPos + 1;
						break;
					} else {
						break;
					}

				case 1:
					x = searchNode.xPos;
					y = searchNode.yPos + 1;
					break;

				case 2:
					if ((game.map.getType(searchNode.xPos, searchNode.yPos + 1) == Type.EMPTY)
							&& (game.map.getType(searchNode.xPos + 1,
									searchNode.yPos) == Type.EMPTY)) {
						x = searchNode.xPos + 1;
						y = searchNode.yPos + 1;
						break;
					} else {
						break;
					}

				case 3:
					x = searchNode.xPos - 1;
					y = searchNode.yPos;
					break;

				case 4:
					x = searchNode.xPos + 1;
					y = searchNode.yPos;
					break;

				case 5:
					if ((game.map.getType(searchNode.xPos, searchNode.yPos - 1) == Type.EMPTY)
							&& (game.map.getType(searchNode.xPos - 1,
									searchNode.yPos) == Type.EMPTY)) {
						x = searchNode.xPos - 1;
						y = searchNode.yPos - 1;
						break;
					} else {
						break;
					}

				case 6:
					x = searchNode.xPos;
					y = searchNode.yPos - 1;
					break;

				case 7:
					if ((game.map.getType(searchNode.xPos, searchNode.yPos - 1) == Type.EMPTY)
							&& (game.map.getType(searchNode.xPos + 1,
									searchNode.yPos) == Type.EMPTY)) {
						x = searchNode.xPos + 1;
						y = searchNode.yPos - 1;
						break;
					} else {
						break;
					}

				default:
					continue;

				}

				// GameWrapper.d("== Iteration "+i+" generated a new node at ("+x+", "+y+")");

				AINode testNode = new AINode(x, y);

				if (isAlreadyOn(testNode, closedList)) {
					continue;
				}

				if (x < 0 || y < 0) {
					continue;
				}

				if (x >= game.mapWidth || y >= game.mapHeight) {
					continue;
				}

				Type mapPointType = map.getType(x, y);

				if (mapPointType != null && !mapPointType.equals(Type.EMPTY)) {
					closedList.add(testNode);
					continue;
				}

				Double tentative_g_score = 0.0;

				// Add cost of parent's g cost
				if (player == null) {
					tentative_g_score += searchNode.g;// +
														// heuristicCost(searchNode,
														// testNode);
				} else {
					tentative_g_score += searchNode.g;// +
														// heuristicCost(searchNode,
														// testNode, player);
				}

				// Cost to get to test node
				if (searchNode.xPos == testNode.xPos
						|| searchNode.yPos == testNode.yPos) {
					tentative_g_score += straightCost;
				} else {
					tentative_g_score += diagonalCost;
				}

				// Not already on openList
				if (isAlreadyOn(testNode, openList)) {

					AINode existingNode = getNode(x, y, openList);

					// Already on openList
					if (tentative_g_score < existingNode.g) {

						openList.remove(existingNode);

						existingNode.parentNode = searchNode;
						existingNode.g = tentative_g_score;

						if (player == null) {
							existingNode.h = heuristicCost(existingNode,
									endNode);
						} else {
							existingNode.h = heuristicCost(existingNode,
									endNode, player);
						}

						existingNode.f = existingNode.g + existingNode.h;

						openList.add(existingNode);

					}

				} else {

					testNode.parentNode = searchNode;
					testNode.g = tentative_g_score;

					if (player == null) {
						testNode.h = heuristicCost(testNode, endNode);
					} else {
						testNode.h = heuristicCost(testNode, endNode, player);
					}

					testNode.f = testNode.g + testNode.h;

					openList.add(testNode);

				}

			}

			// GameWrapper.d("Size of openList: " + openList.size() );

		}

		// GameWrapper.d("No Path Found");

		return false;

	}

	/**
	 * This function recursively assembles the Path based on the endPoint's
	 * parent until it reaches the startPoint which should have no parent.
	 * 
	 * @param searchNode
	 * @return
	 */
	private ArrayList<AINode> reconstruct_path(AINode searchNode) {

		ArrayList<AINode> path = new ArrayList<AINode>();

		path.add(0, searchNode);
		searchNode = searchNode.parentNode;

		while (!(searchNode.xPos == startNode.xPos && searchNode.yPos == startNode.yPos)) {
			path.add(0, searchNode);
			searchNode = searchNode.parentNode;
		}

		return path;

	}

	/**
	 * Calculates the Heuristic cost between two nodes based on a method.
	 * 
	 * @param search
	 *            The start node
	 * @param end
	 *            The end node
	 * @return The integer heuristic cost (h_cost)
	 */
	private Double heuristicCost(AINode search, AINode end) {

		Double h_cost = 0.0;

		/* Euclidian */
		// h_cost += Math.sqrt(Math.pow(end.xPos - search.xPos, 2) +
		// Math.pow(end.yPos - search.yPos, 2));

		/* Manhattan */
		// h_cost += Math.abs(end.xPos - search.xPos) + Math.abs(end.yPos -
		// search.yPos);

		/* Diagonal Shortcut */
		Double xDistance = (double) Math.abs(search.xPos - end.xPos);
		Double yDistance = (double) Math.abs(search.yPos - end.yPos);

		if (xDistance > yDistance) {
			h_cost = diagonalCost * yDistance + straightCost
					* (xDistance - yDistance);
		} else {
			h_cost = diagonalCost * xDistance + straightCost
					* (yDistance - xDistance);
		}

		return h_cost;

	}

	/**
	 * Calculates the Heuristic cost between two nodes based on a method.
	 * Includes an influence map so it stays away from the Player
	 * 
	 * @param search
	 *            The start node
	 * @param end
	 *            The end node
	 * @param player
	 *            The player to avoid
	 * @return The integer heuristic cost (h_cost)
	 */
	private Double heuristicCost(AINode search, AINode end, Body player) {

		Double h_cost = 0.0;

		/* Euclidian */
		// h_cost += Math.sqrt(Math.pow(end.xPos - search.xPos, 2) +
		// Math.pow(end.yPos - search.yPos, 2));

		/* Manhattan */
		// h_cost += Math.abs(end.xPos - search.xPos) + Math.abs(end.yPos -
		// search.yPos);

		/* Diagonal Shortcut */
		Double xDistance = (double) Math.abs(search.xPos - end.xPos);
		Double yDistance = (double) Math.abs(search.yPos - end.yPos);

		if (xDistance > yDistance) {
			h_cost = diagonalCost * yDistance + straightCost
					* (xDistance - yDistance);
		} else {
			h_cost = diagonalCost * xDistance + straightCost
					* (yDistance - xDistance);
		}

		// The closer the path gets to the player the more expensive it is.
		h_cost += Config.getConfig("playerInfluence")
				/ Math.sqrt(Math.pow(player.xPos / game.mapPointSize
						- search.xPos, 2)
						+ Math.pow(player.yPos / game.mapPointSize
								- search.yPos, 2));

		return h_cost;

	}

	/**
	 * Finds the cheapest AINode on an ArrayList of AINodes
	 * 
	 * @param list
	 *            The list to be checked
	 * @return The index number of the AINode on the ArrayList
	 */
	private Integer getCheapestPoint(ArrayList<AINode> list) {

		AINode cheapestPoint = null;

		Integer index = 0;

		for (int i = 0; i < list.size(); i++) {
			AINode point = list.get(i);

			if (cheapestPoint == null) {
				cheapestPoint = point;

				index = i;

			} else if (cheapestPoint.f >= point.f) {

				// GameWrapper.d(point.f+" is cheaper than "+cheapestPoint.f);

				cheapestPoint = point;

				index = i;
			}

		}

		return index;

	}

	/**
	 * Checks if an AINode is already on an ArrayList of AINodes
	 * 
	 * @param checkNode
	 *            AINode to be checked
	 * @param list
	 *            List to check AINode against
	 * @return True or False
	 */
	public boolean isAlreadyOn(AINode checkNode, ArrayList<AINode> list) {

		for (AINode point : list) {

			if (checkNode.xPos == point.xPos && checkNode.yPos == point.yPos) {
				return true;
			}

		}

		return false;

	}

	/**
	 * Gets the AINode on the list based on an x, y position
	 * 
	 * @param x
	 * @param y
	 * @param list
	 *            The list to retrieve the AINode from
	 * @return The AINode on the x, y position
	 */
	public AINode getNode(int x, int y, ArrayList<AINode> list) {

		for (AINode node : list) {
			if (node.xPos == x && node.yPos == y) {
				return node;
			}
		}

		return null;

	}

}
