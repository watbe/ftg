package ftg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * WildWest - (c) 2011
 * 
 * This is the main class that runs the game and splash screens.
 * 
 * @author Wayne Tsai
 * 
 */
public class WildWest implements ActionListener {

	final int delay = 10;
	static boolean keyQuit;
	static boolean exit;

	private Timer timer;
	Game game;

	public WildWest() {

		timer = new Timer(delay, this);
		timer.start();
		game = new Game();

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == timer) {
			if (keyQuit) {
				timer.stop();
				Launcher.quitGame();
			}

		}

	}

	public void close() {
		game.window.g.dispose();
		game.window.frame.dispose();
		game.window.frame.removeAll();
		timer.stop();
		game.timer.cancel();
		game = null;
	}

}
