package ftg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class loads levels from level config files. It provides methods to load
 * the next level and to parse the level.conf files. Note that level.conf files
 * are essentially config files, with the exception that the first line is
 * reserved for the level name.
 * 
 * @author Wayne Tsai
 */
public class Levels {

	Integer numberOfLevels;
	String levelFileNamePrefix = "level";
	String levelFileNameSuffix = ".conf";
	static ArrayList<String> levels = new ArrayList<String>();
	static ArrayList<String> levelDescriptions = new ArrayList<String>();
	Integer currentLevel = 0;

	/**
	 * Constructor for Levels. At the moment it just runs initialiseLevels()
	 */
	public Levels() {
		initialiseLevels();
	}

	/**
	 * Initialises the levels by searching for level files and adding their
	 * names into the levels list. Should only be run once.
	 */
	public void initialiseLevels() {

		int i = 1;

		do {

			Game.d("looking for " + levelFileNamePrefix + i
					+ levelFileNameSuffix);

			BufferedReader existingConfig = null;

			/* Add to levels */
			String levelFileName = levelFileNamePrefix + i
					+ levelFileNameSuffix;

            String path = "" + levelFileName;

            Game.d(path);

			URL url = WildWest.class.getResource(path);

			if(url == null) {
				Game.d(levelFileNamePrefix + i + levelFileNameSuffix
						+ " does not exist");
				break;
			}
			
			try {
				existingConfig = new BufferedReader(new InputStreamReader(url.openStream()));
			} catch (IOException e) {

			}

			Scanner scanner = new Scanner(existingConfig);

			String levelName = scanner.nextLine();

			levels.add(i + " " + levelName);

			String levelDescription = scanner.nextLine();

			levelDescriptions.add(levelDescription);

			Game.d("Loaded level: " + levels.get(levels.size() - 1));

			i++;

		} while (true);

		numberOfLevels = i - 1;

		Game.d(numberOfLevels + " levels detected");

	}

	/**
	 * Loads the next level based on the current level. If there are no more
	 * levels, it reloads the current level.
	 */
	public void loadNextLevel() {

		if (currentLevel < numberOfLevels) {
			currentLevel++;
			loadLevel(currentLevel);
		} else {
			loadLevel(currentLevel);
		}

	}

	/**
	 * Loads a specified level by reading the config file of the level into
	 * Config
	 * 
	 * @param i
	 *            The number of the level to load
	 * @return Boolean True or False depending on whether there is a level to
	 *         load
	 */
	public Boolean loadLevel(int i) {

		if (i > numberOfLevels || i <= 0) {
			Game.d("No such level: " + i);
			return false;
		}

		String levelFileName = levelFileNamePrefix + i + levelFileNameSuffix;

		BufferedReader existingConfig = null;

		try {
			existingConfig = new BufferedReader(new InputStreamReader(
					WildWest.class.getResource("/ftg/"+levelFileName).openStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(existingConfig);

		scanner.nextLine();
		scanner.nextLine(); // Skip first and second line (level name)

		while (scanner.hasNext()) {

			String variable = scanner.next();
			Integer value = scanner.nextInt();

			Game.d("Read level config: " + variable + ": " + value);

			Config.setConfig(variable, value);

		}

		return true;

	}

	/**
	 * Gets the name of the level
	 * 
	 * @param i
	 * @return
	 */
	public static String getLevelName(int i) {
		return levels.get(i);
	}

	public static String getLevelDescription(int i) {
		return levelDescriptions.get(i);
	}
}
