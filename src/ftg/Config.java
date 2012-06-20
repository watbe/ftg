package ftg;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * WildWest - � 2011 Sets the configuration of the game
 * 
 * @author Wayne Tsai
 * 
 */

public class Config {

	static String configFileName = "config.txt";
	static HashMap<String, Integer> config = new HashMap<String, Integer>();

	public static void setDefaults() {
		setConfig("mapPointSize", 80);
		setConfig("simplicity", 4);
		setConfig("mapWidth", 30);
		setConfig("mapHeight", 30);
		setConfig("numberBaddies", 3);
		setConfig("fullscreen", 0);
		setConfig("debug", 1);
		setConfig("windowedResX", 600);
		setConfig("windowedResY", 500);
		setConfig("framerate", 30);
		setConfig("gameSpeed", 70);
		setConfig("playerInfluence", 40);
		setConfig("baddieRevealDistance", 3);
		setConfig("AssMaxSpeed", 5);
		setConfig("BanditMaxSpeed", 5);
		setConfig("baddieRevealDistance", 3);
		setConfig("maxPickups", 10);
		setConfig("playerMaxSpeed", 10);
		setConfig("playerRadarRange", 250);
		setConfig("maxPickups", 10);
		setConfig("pickupChance", 200);
		setConfig("timeLimit", 180);

		Toolkit tk = Toolkit.getDefaultToolkit();
		setConfig("resX", (int) tk.getScreenSize().getWidth());
		setConfig("resY", (int) tk.getScreenSize().getHeight());
		setConfig("nativeX", (int) tk.getScreenSize().getWidth());
		setConfig("nativeY", (int) tk.getScreenSize().getHeight());
		setConfig("mapType", 3);

		Game.d("Default config set!");

	}

	/**
	 * Initialises Config by reading it into the HashMap
	 */
	public static void initConfig() {

		readConfig();
		Game.d("Configuration file loaded with: " + config.toString());

	}

	/**
	 * Sets a new variable to the hashmap of configs
	 * 
	 * @param variable
	 *            The name of the variable
	 * @param value
	 *            Integer value denoting the value of the variable
	 * @throws IOException
	 */
	public static void setConfig(String variable, Integer value) {

		config.put(variable, value);
	}

	/**
	 * Returns the configuration variable
	 * 
	 * @param variable
	 * @return Integer value of the variable
	 */
	public static Integer getConfig(String variable) {
		return config.get(variable);
	}

	/**
	 * Creates a new configuration file and sets the first value to Variable -1
	 */
	public static void createNewConfig() {

		setConfig("Variable", -1);
		writeConfig();

	}

	/**
	 * Reads the config file and puts it into the Hashmap (config)
	 */
	public static void readConfig() {

		File file = new File(configFileName);

		if (!file.exists()) {
			Game.d("Config file doesn't exist, creating new config file");
			createNewConfig();
		} else {
			Game.d("Config file exists already");
		}

		BufferedReader existingConfig = null;

		try {
			existingConfig = new BufferedReader(new InputStreamReader(
					new FileInputStream(configFileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(existingConfig);

		while (scanner.hasNext()) {

			String variable = scanner.next();
			Integer value = scanner.nextInt();

			Game.d("Read config into HashMap: " + variable + ": " + value);

			config.put(variable, value);

		}

	}

	/**
	 * Writes the config to the file
	 */
	public static void writeConfig() {

		BufferedWriter output = null;

		try {
			output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(configFileName)));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File Access Error!");
			System.out.println("File Access Error");
			e.printStackTrace();
		}

		Iterator<Entry<String, Integer>> it = config.entrySet().iterator();
		while (it.hasNext()) {

			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println("Writing: " + pairs.getKey() + " "
					+ pairs.getValue());

			try {
				output.write(pairs.getKey() + " " + pairs.getValue() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Resets the configurations to default
	 */
	public static void resetConfig() {

		if (JOptionPane
				.showConfirmDialog(
						null,
						"Are you sure you want to delete your user preferences and set the default settings?\nNote scores will not be reset.",
						"Reset Configuration?", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE) == 0) {
			deleteConfig();
			config.clear();
			setDefaults();
			writeConfig();

			String defaultConfig = null;

			Iterator<Entry<String, Integer>> it = config.entrySet().iterator();
			while (it.hasNext()) {

				@SuppressWarnings("rawtypes")
				Map.Entry pairs = (Map.Entry) it.next();

				defaultConfig += (pairs.getKey() + ": " + pairs.getValue() + "\n");

			}

			JOptionPane.showMessageDialog(null,
					"The default configuration has been set:\n\n"
							+ defaultConfig + "\n\n", "Configuration Reset!",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Deletes the configuration
	 */
	public static void deleteConfig() {

		File file = new File(configFileName);

		if (file.exists()) {
			file.delete();
			Game.d("Config file deleted!");
		} else {
			Game.d("No config file to delete!");
		}

	}

	/**
	 * Reads the config file and puts it into a text string.
	 */
	public static String dumpConfig() {

		String output = "";

		Iterator<Entry<String, Integer>> it = config.entrySet().iterator();
		while (it.hasNext()) {

			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			System.out.println("Writing: " + pairs.getKey() + " "
					+ pairs.getValue());

			output += (pairs.getKey() + " " + pairs.getValue() + "\n");

		}

		return output;

	}

	/**
	 * Reads the config file and puts it into the Hashmap (config)
	 */
	public static void readDump(String string) {

		String existingConfig = string;

		Scanner scanner = new Scanner(existingConfig);

		while (scanner.hasNext()) {

			String variable = scanner.next();
			Integer value = scanner.nextInt();

			Game.d("Read config into HashMap: " + variable + ": " + value);

			config.put(variable, value);

		}

	}

}
