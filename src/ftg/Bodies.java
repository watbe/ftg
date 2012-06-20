package ftg;

import java.util.ArrayList;

/**
 * WildWest - 2011 Contains an ArrayList of Characters and the the Collision
 * testing methods that works on it
 * 
 * @author Thomas Lillywhite
 * 
 */

public class Bodies extends ArrayList<Body> {

	private static final long serialVersionUID = -139943806308407880L;
	boolean timerStart = true;
	double timer = 0;

	/**
	 * Checks if player collides with a character. 
	 * Uses the onDeath() function for effects.
	 * 
	 * @param game
	 *            - the game
	 */
	public void characterCollision(Game game) {

		Player player = game.player;

		if (timerStart) {
			timer += 0.1;
			if (timer > 1) {
				player.hit = false;
			}
			if (timer > 5) {
				timerStart = false;
				player.immune = false;
				timer = 0;
			}
		}

		for (int i = 0; i < this.size(); i++) {

			Body character = this.get(i);

			if (Math.abs(player.xPos - character.xPos) < (player.size + character.size) / 2
					&& Math.abs(player.yPos - character.yPos) < (player.size + character.size) / 2) {

				character.onDeath();

			}

		}
	}

}