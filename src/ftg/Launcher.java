package ftg;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Font;
import javax.swing.JComboBox;
import java.awt.Component;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Cursor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * WildWest - (c) 2011 Creates the launcher
 * 
 * @author Wayne Tsai
 * 
 */

public class Launcher {
	static String name;
	static Levels levels;
	Integer numberOfLevels;
	private static JFrame frmRescueRiver;
	private JCheckBox DebuggingMode;
	static WildWest wildwest;
	private JComboBox<String> levelSelect;
	private JTextPane levelBrief;
	private ImageButton btnLaunchGame;
	private JTextPane highScores;
	private ImageButton help;

	static Integer currentLevel;
	static Point mouseDownScreenCoords;
	static Point mouseDownCompCoords;
	public static Scores scores;
	private JTextField captainName;
	
	static Sound themeMusic;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		// Install the look and feel
		UIManager.setLookAndFeel(nativeLF);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					@SuppressWarnings("unused")
					Launcher window = new Launcher();
					Launcher.frmRescueRiver.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Launcher() {
		Config.setDefaults();
		Config.initConfig();

		Toolkit tk = Toolkit.getDefaultToolkit();
		Config.setConfig("resX", (int) tk.getScreenSize().getWidth());
		Config.setConfig("resY", (int) tk.getScreenSize().getHeight());
		levels = new Levels();
		scores = new Scores();
		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
				
		frmRescueRiver = new JFrame();
		frmRescueRiver.setIconImage(Toolkit.getDefaultToolkit().getImage(
				Launcher.class.getResource("player.png")));
		frmRescueRiver.setTitle("Rescue River");
		frmRescueRiver.setResizable(false);
		frmRescueRiver.setBounds(Config.getConfig("resX") / 2 - 200,
				Config.getConfig("resY") / 2 - 300, 400, 600);
		frmRescueRiver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRescueRiver.getContentPane().setLayout(null);
		frmRescueRiver.setUndecorated(true);
		mouseDownScreenCoords = null;
		mouseDownCompCoords = null;

		frmRescueRiver.addMouseListener(new MouseListener() {

			public void mouseReleased(MouseEvent e) {
				mouseDownScreenCoords = null;
				mouseDownCompCoords = null;
			}
			public void mousePressed(MouseEvent e) {
				mouseDownScreenCoords = e.getLocationOnScreen();
				mouseDownCompCoords = e.getPoint();
			}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}

		});

		frmRescueRiver.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {}
			public void mouseDragged(MouseEvent e) {
				Point currCoords = e.getLocationOnScreen();
				frmRescueRiver.setLocation(mouseDownScreenCoords.x
						+ (currCoords.x - mouseDownScreenCoords.x)
						- mouseDownCompCoords.x, mouseDownScreenCoords.y
						+ (currCoords.y - mouseDownScreenCoords.y)
						- mouseDownCompCoords.y);
			}
		});

		ImagePanel panel_2 = new ImagePanel("launcher");
		panel_2.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		panel_2.setBounds(0, 0, 400, 600);
		frmRescueRiver.getContentPane().add(panel_2);
		panel_2.setLayout(null);
		panel_2.setOpaque(false);

		btnLaunchGame = new ImageButton("launchButton");
		btnLaunchGame.setOpaque(false);
		btnLaunchGame.setBounds(150, 467, 99, 99);
		panel_2.add(btnLaunchGame);
		btnLaunchGame.setToolTipText("Start the game already!");
		btnLaunchGame.setBackground(Color.GRAY);
		btnLaunchGame.setFont(new Font("Rockwell", Font.BOLD, 23));
		btnLaunchGame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					runGame();
				}
			}
		});

		// Add an empty border around us to compensate for
		// the rounded corners.
		btnLaunchGame.setBorder(null);
		btnLaunchGame.setBackground(null);
		btnLaunchGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runGame();
			}
		});

		btnLaunchGame.setFocusCycleRoot(true);

		JLabel label = new JLabel("");
		label.setBounds(154, 111, 0, 0);
		panel_2.add(label);

		JLabel label_1 = new JLabel("");
		label_1.setBounds(159, 111, 0, 0);
		panel_2.add(label_1);

		JButton btnResetSettings = new JButton("Reset Settings");
		btnResetSettings.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnResetSettings.setOpaque(false);
		btnResetSettings.setBounds(20, 570, 140, 23);
		panel_2.add(btnResetSettings);
		
		JButton howTo = new JButton("How to play");
		howTo.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		howTo.setOpaque(false);
		howTo.setBounds(240, 570, 140, 23);
		
		panel_2.add(howTo);
		
		help = new ImageButton("help");
		help.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		help.setOpaque(false);
		help.setVisible(false);
		help.setBounds(50, 50, 300, 400);
		
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				help.setVisible(false);
			}
		});
		
		
		howTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				help.setVisible(true);
			}
		});
		
		panel_2.add(help);
		

		ImageButton btnExit = new ImageButton("close");
		btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnExit.setBounds(377, 11, 13, 14);
		panel_2.add(btnExit);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setIcon(null);
		btnExit.setBackground(new Color(255, 255, 255, 0));
		btnExit.setOpaque(false);

		highScores = new JTextPane();
		highScores.setOpaque(false);
		highScores.setMargin(new Insets(4, 4, 3, 3));
		highScores.setBackground(Color.GREEN);
		highScores.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		highScores.setFont(new Font("Monospaced", Font.PLAIN, 12));
		highScores.setFocusable(false);
		highScores.setEditable(false);
		highScores.setBounds(208, 322, 164, 171);
		loadHighscores();
		highScores.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				loadHighscores();
			}
		});
		highScores.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				loadHighscores();
			}
		});
		panel_2.add(highScores);

		captainName = new JTextField();
		captainName.setMargin(new Insets(0, 3, 0, 0));
		captainName.setBackground(Color.GREEN);
		captainName.setFont(new Font("Monospaced", Font.PLAIN, 12));
		captainName.setForeground(Color.BLACK);
		if(captainName.getText().equals("")) {
			captainName.setText("Captain ");
		}
		captainName.setBounds(226, 216, 146, 20);
		panel_2.add(captainName);
		captainName.setColumns(10);

		levelBrief = new JTextPane();
		levelBrief.setFocusable(false);
		levelBrief.setOpaque(false);
		levelBrief.setMargin(new Insets(4, 4, 3, 3));
		levelBrief.setBackground(Color.GREEN);
		levelBrief.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		levelBrief.setFont(new Font("Monospaced", Font.PLAIN, 12));
		levelBrief.setEditable(false);
		levelBrief.setBounds(26, 322, 161, 171);
		panel_2.add(levelBrief);

		levelSelect = new JComboBox<String>();
		levelSelect.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		levelSelect.setFont(new Font("Georgia", Font.PLAIN, 13));
		levelSelect.setOpaque(false);
		levelSelect.setBackground(Color.GREEN);
		levelSelect.setBounds(145, 254, 227, 23);
		
		for (int i = 0; i < levels.numberOfLevels; i++) {
			levelSelect.addItem(Levels.getLevelName(i));
		}
		
		levelSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loadLevelBrief();
			}
		});
		
		

		panel_2.add(levelSelect);
		
		ImageButton resetButton = new ImageButton("reset");
		
		resetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				scores.resetScores();
				if(JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete all Highscores?", "Confirm Highscore Reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
					scores.resetScores();
					JOptionPane.showMessageDialog(null, "All scores have been deleted!", "Highscores reset!", JOptionPane.INFORMATION_MESSAGE);
				}
				loadHighscores();
			}
		});
		
		resetButton.setOpaque(false);
		resetButton.setBackground(new Color(255, 255, 255, 0));
		resetButton.setBounds(340, 297, 32, 14);
		panel_2.add(resetButton);
//		frmRescueRiver.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{btnLaunchGame, panel_2, DebuggingMode, label, label_1, btnResetSettings, btnExit}));
//		frmRescueRiver.setFocusTraversalPolicy(new FocusTraversalOnArray(
//				new Component[]{btnLaunchGame, frmRescueRiver.getContentPane(),
//						panel_2, DebuggingMode, label, label_1,
//						btnResetSettings, btnExit}));

		btnResetSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.resetConfig();
			}
		});
		
		frmRescueRiver.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				loadLevelBrief();
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
			}
		});

		frmRescueRiver.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					runGame();
				}
			}
		});

		loadLevelBrief();
		
		Sound.playAudio(Sound.themeMusic);
		
	}

	public void loadLevelBrief() {
		currentLevel = levelSelect.getSelectedIndex() + 1;
		levels.loadLevel(currentLevel);
		String string = "";
		string += "Time Limit: " + Config.getConfig("timeLimit") + " secs\n";
		string += "Map Height: " + Config.getConfig("mapHeight") + "\n";
		string += "Map Width: " + Config.getConfig("mapWidth") + "\n";
		string += "Map Difficulty: " + Config.getConfig("simplicity") + "\n";
		string += "Enemy Reavers: " + Config.getConfig("numberBaddies") + "\n";
		string += "Max Speed: " + Config.getConfig("playerMaxSpeed") + "\n";
		string += "Max Pickups: " + Config.getConfig("maxPickups") + "\n";
		levelBrief.setText(string);
	}

	public void loadHighscores() {
		String rawText = "";
		for (String string : scores.getScoresString()) {
			rawText += string + "\n";

		}
		highScores.setText(rawText);
	}

	public void runGame() {
		name = captainName.getText();

		do {
			while (name.endsWith(" ")) {
				name = name.substring(0, name.length() - 1);
			}
	
			while (name.startsWith(" ")) {
				name = name.substring(1, name.length());
			}
	
			if (name == null || name.equals("")) {
				name = "Captain Nully";
			}
			
			if(name.equals("Captain")) {
				name = JOptionPane.showInputDialog(null, "Are you sure you wish to use Captain as your name? \nEnter a new name if you wish to change it.", "Confirm generic name", JOptionPane.QUESTION_MESSAGE);
				while (name.endsWith(" ")) {
					name = name.substring(0, name.length() - 1);
				}
		
				while (name.startsWith(" ")) {
					name = name.substring(1, name.length());
				}
		
				if (name == null || name.equals("")) {
					name = "Captain Nully";
				}
				break;
			} 
			
		} while (name.equals("Captain"));

		Toolkit tk = Toolkit.getDefaultToolkit();
		
		saveSettings();

        Config.setConfig("resX", 800);
        Config.setConfig("resY", 600);
        Config.setConfig("fullscreen", 0);

//		Config.setConfig("resX", (int) tk.getScreenSize().getWidth());
//		Config.setConfig("resY", (int) tk.getScreenSize().getHeight());
		
		wildwest = new WildWest();
		frmRescueRiver.setVisible(false);
	}

	public static void quitGame() {
		wildwest.game.window.g.dispose();
		wildwest.close();
		wildwest = null;
		frmRescueRiver.setVisible(true);
		frmRescueRiver.setAlwaysOnTop(true);
		frmRescueRiver.setAlwaysOnTop(false);
	}

	public void saveSettings() {

		Config.writeConfig();

		currentLevel = levelSelect.getSelectedIndex() + 1;
		levels.loadLevel(currentLevel);

		Game.d("loaded level: " + (levelSelect.getSelectedIndex() + 1));

	}
}
