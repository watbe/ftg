/**
 * 
 */
package ftg;

/**
 * WildWest - (c) 2011 AINodes are a type of class which facilitate AI Path
 * Finding
 * 
 * @author Wayne Tsai
 * 
 */
public class AINode {

	Double g;
	Double h;
	Double f;
	Integer xPos;
	Integer yPos;

	AINode parentNode;

	/**
	 * Constructor for AINode only requires an (x, y) coordinate
	 * 
	 * @param x
	 * @param y
	 */
	public AINode(Integer x, Integer y) {
		xPos = x;
		yPos = y;
	}

}
