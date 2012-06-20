package ftg;

/**
 * WildWest - � 2011 Creates the pickups (power ups)
 * 
 * @author Thomas Lillywhite
 */
public class Pickup extends Body {

	Integer effect;

	public Pickup(Game game, Integer effect) {

		super(game);
		this.charType = Type.PICKUP;
		this.size = 20;
		// Generate a random effect
		this.effect = effect;

	}
	@Override
	public void onDeath() {
		Pickup pickup = (Pickup) this;
		Player player = game.player;
		player.currentPickup = pickup.effect;
		player.hasPickup = true;
		game.pickups.remove(this);
	}

	@Override
	public void update() {
	}

	@Override
	public void reset() {
		game.pickups.remove(this);
	}

}
