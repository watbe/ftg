package ftg;

/**
 * WildWest- � 2011 Extends the character class by adding the field path to
 * the constructor
 * 
 * @author Tom Sautelle & Thomas Lillywhite
 * 
 */
public abstract class Enemy extends Body {

	Path path;
	double timer;
	boolean timerStart = false;
	boolean frozen = false;
	boolean inRange;
	double distance;

	public Enemy(Game game) {

		super(game);
		timer = 0;

	}
	
	/**
	 * Gets the move when it doesnt have a path or line of sight
	 * @param charX
	 * @param charY
	 */
	public abstract void getMove(int charX, int charY);

	/**
	 * Uses the path to get the baddies move.
	 * @param nextMove
	 */
	public abstract void getMove(AINode nextMove);

}
