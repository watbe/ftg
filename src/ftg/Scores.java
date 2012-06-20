package ftg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Scores {

	public String scoreFileName = "scores.dat";
	public ArrayList<String> names = new ArrayList<String>();
	public ArrayList<Integer> scores = new ArrayList<Integer>();

	Scores() {

		readScores();

	}

	public void readScores() {

		File file = new File(scoreFileName);

		if (!file.exists()) {
			Game.d("Score file doesn't exist, creating new score file");
			writeScores();
		} else {
			Game.d("Score file exists already");
		}

		BufferedReader existingConfig = null;

		try {
			existingConfig = new BufferedReader(new InputStreamReader(
					new FileInputStream(scoreFileName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Scanner scanner = new Scanner(existingConfig);

		scanner.nextLine(); // Skip first line

		while (scanner.hasNext()) {

			String variable = scanner.nextLine();
			Integer value = Integer.parseInt(scanner.nextLine());

			Game.d("Read score into ArrayLists 'Scores' and 'Names': "
					+ variable + ": " + value);

			sortedAdd(variable, value);

		}

	}

	public void sortedAdd(String name, Integer value) {

		Game.d("Adding score: " + name + " " + value);

		if (scores.size() != 0) {
			for (int i = 0; i < scores.size(); i++) {
				if (scores.get(i) < value) {
					scores.add(i, value);
					names.add(i, name);
					break;
				}
				if (i == scores.size() - 1) {
					scores.add(value);
					names.add(name);
					break;
				}
			}
		} else {
			scores.add(value);
			names.add(name);
		}

		writeScores();
	}

	public void writeScores() {

		BufferedWriter output = null;

		File file = new File(scoreFileName);
		file.delete();
		file = new File(scoreFileName);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(scoreFileName)));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File Access Error!");
			System.out.println("File Access Error");
			e.printStackTrace();
		}

		try {
			output.write("Score" + " " + 10000 + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (scores != null) {
			for (int i = 0; i < scores.size(); i++) {

				Integer score = scores.get(i);
				String name = names.get(i);

				System.out.println("Writing to score: " + name + " " + score);

				try {
					output.write(name + "\n" + score + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> getScoresString() {
		ArrayList<String> strings = new ArrayList<String>();
		for (int i = 0; i < scores.size(); i++) {
			strings.add(names.get(i) + " " + scores.get(i));
		}
		return strings;
	}

	public void resetScores() {
		scores.clear();
		names.clear();
		writeScores();
	}
}
