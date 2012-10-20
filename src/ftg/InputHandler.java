package ftg;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_KP_LEFT
				|| e.getKeyCode() == KeyEvent.VK_LEFT) {
			Player.left = true;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Player.right = true;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_UP
				|| e.getKeyCode() == KeyEvent.VK_UP) {
			Player.up = true;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_DOWN
				|| e.getKeyCode() == KeyEvent.VK_DOWN) {
			Player.down = true;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Game.spacePressed = true;
			Player.usePickup = true;
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			Game.keyReset = true;
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			if (Game.gameState.equals("playing")) {
				Game.paused = !Game.paused;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_T) {
			Game.victory = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_KP_LEFT
				|| e.getKeyCode() == KeyEvent.VK_LEFT) {
			Player.left = false;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_RIGHT
				|| e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Player.right = false;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_UP
				|| e.getKeyCode() == KeyEvent.VK_UP) {
			Player.up = false;
		} else if (e.getKeyCode() == KeyEvent.VK_KP_DOWN
				|| e.getKeyCode() == KeyEvent.VK_DOWN) {
			Player.down = false;
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Player.usePickup = false;
			Game.spacePressed = false;
		} else if (e.getKeyCode() == KeyEvent.VK_Q) {
			Game.keyReset = false;
		} else if (e.getKeyCode() == KeyEvent.VK_M) {
			Window.showMinimap = !Window.showMinimap;
		}

	}
}
