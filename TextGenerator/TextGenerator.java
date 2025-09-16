
package comprehensive;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A text generator class that uses a Markov Chain model to generate text based
 * on a given input file. The program takes in a file path, seed word, number of
 * words to generate (k), and a mode as command-line arguments. There are three
 * modes. Probable, deterministic, and random.
 * 
 * @author Nash Taylor and Jayden Whalen
 * @version April 19th, 2025
 */
public class TextGenerator {

	public static void main(String[] args) {

		TextProbability markovChain = new TextProbability();

		String filePath = args[0];
		String seedWord = args[1].toLowerCase();
		int k = Integer.parseInt(args[2]);
		String mode = args[3];

		File file = new File(filePath);

		try {
			markovChain.buildMarkovChain(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String output = switch (mode) {
		case "probable" -> markovChain.generateProbable(seedWord, k);
		case "deterministic" -> markovChain.generateDeterminstic(seedWord, k);
		case "random" -> markovChain.generateRandom(seedWord, k);
		default -> throw new IllegalArgumentException("Invalid mode.");
		};

		System.out.println(output);

	}

}
