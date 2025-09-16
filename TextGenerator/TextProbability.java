package comprehensive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

/**
 * This class uses a Markov Chain to determine the probabilities of subsequent
 * words of each word in a text file. It can generate a string of words,
 * separated by a space, that can come after a seed word based on probable,
 * deterministic, and random occurrences in the file.
 * 
 * @author Nash Taylor and Jayden Whalen
 * @version April 19, 2025
 */
public class TextProbability {

	private Map<String, Map<String, Integer>> markovChain;
	private ArrayList<String> validWords;

	/**
	 * Constructor that initializes the Markov Chain and list of valid words.
	 */
	public TextProbability() {
		markovChain = new HashMap<>();
		validWords = new ArrayList<>();
	}

	/**
	 * Builds the Markov Chain using a Scanner to read the file and StringBuilder to
	 * convert all letters to lowercase and its split function to reject all
	 * punctuation except underscores and apostrophes.
	 * 
	 * @param file - text file used to build the Markov Chain
	 * @throws FileNotFoundException if the file path is incorrect (should never
	 *                               happen with correct command-line arguments)
	 */
	@SuppressWarnings("unused")
	public void buildMarkovChain(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		StringBuilder content = new StringBuilder();

		while (scanner.hasNext()) {
			content.append(scanner.next().toLowerCase()).append(" ");
		}
		scanner.close();

		String[] words = content.toString().split("[^a-z0-9'_]+"); // Gets rid of all punctuation except underscores and
																	// apostrophes
		for (String word : words) {
			if (!word.isEmpty()) {
				validWords.add(word);
			}
		}

		for (int i = 0; i < validWords.size() - 1; i++) {
			makeMap(validWords.get(i), validWords.get(i + 1));
		}

	}

	/**
	 * Updates the Markov Chain by adding or incrementing the occurrence of a word
	 * being followed by another word. If the current word is not in the Markov
	 * Chain yet, a new entry is created. Then, it checks if the next word has
	 * already been added. If it hasn't, it gets added as a subsequent word. If it
	 * has, the frequency count (value) gets incremented.
	 * 
	 * @param current - the current word being looked at in the input text file
	 * @param next    - the word that follows the current word in the input text
	 *                file
	 */
	private void makeMap(String current, String next) {
		if (!markovChain.containsKey(current)) {
			markovChain.put(current, new HashMap<>());
		}

		Map<String, Integer> nextWords = markovChain.get(current);

		if (nextWords.containsKey(next)) {
			nextWords.put(next, nextWords.get(next) + 1);
		} else {
			nextWords.put(next, 1);
		}
	}

	/**
	 * Generates the k most probable words that could come after the seed word. The
	 * words are outputted in descending order from most probable to least, with
	 * ties being broken using their lexicographical ordering.
	 * 
	 * @param seed    - the starting word used to look up probable words that come
	 *                after it
	 * @param kAmount - the number of probable words to return
	 * @return a string of most probable words in descending order seperated by a
	 *         space.
	 */
	public String generateProbable(String seed, int kAmount) {
		if (!markovChain.containsKey(seed) || markovChain.get(seed).isEmpty()) {
			return "";
		}

		Map<String, Integer> innerChain = markovChain.get(seed);
		PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(new WordFrequencyComparator());

		pq.addAll(innerChain.entrySet());

		StringBuilder result = new StringBuilder();
		int count = Math.min(kAmount, pq.size());

		for (int i = 0; i < count; i++) {
			if (i > 0) {
				result.append(" ");
			}
			result.append(pq.poll().getKey());
		}

		return result.toString();
	}

	/**
	 * Will always select only the next word with greatest probability. Ties are
	 * broken using the lexicographical ordering of the words. The first word in the
	 * output is the seed word and if more words need to be generated, then the seed
	 * word and most probable next word alternate.
	 * 
	 * @param seed    - the starting word used to look up the most probable word
	 *                that comes after it
	 * @param kAmount - number of words to be generated
	 * @return a string of a deterministic sequence of words seperated by a space.
	 */
	public String generateDeterminstic(String seed, int kAmount) {
		StringBuilder result = new StringBuilder();
		String currentWord = seed;

		if (kAmount > 0) {
			result.append(seed);
			kAmount--;
		}

		while (kAmount > 0) {
			if (markovChain.containsKey(currentWord)) {
				Map<String, Integer> nextWords = markovChain.get(currentWord);

				String bestWord = null;
				int bestFreq = -1;

				for (Map.Entry<String, Integer> entry : nextWords.entrySet()) {
					String word = entry.getKey();
					int freq = entry.getValue();

					if (freq > bestFreq || (freq == bestFreq && (bestWord == null || word.compareTo(bestWord) < 0))) {
						bestWord = word;
						bestFreq = freq;
					}
				}

				if (bestWord == null) {
					bestWord = seed;
				}

				result.append(" ").append(bestWord);
				currentWord = bestWord;
				kAmount--;
			} else {

				result.append(" ").append(seed);
				currentWord = seed;
				kAmount--;
			}
		}

		return result.toString();
	}

	/**
	 * Generates a k amount of random words that could come after the seed word
	 * based on their probabilities, calculated by their frequency of occurrence in
	 * the input text file.
	 * 
	 * @param seed    - the starting word used to look up the the words that come
	 *                after it
	 * @param kAmount - the number of random words to return
	 * @return a string of random words, seperated by a space, that come after the
	 *         seed word, taking probability into account.
	 */
	public String generateRandom(String seed, int kAmount) {
		StringBuilder result = new StringBuilder();
		String currentWord = seed;
		Random random = new Random();

		if (kAmount > 0) {
			result.append(seed);
			kAmount--;
		}

		while (kAmount > 0) {
			if (markovChain.containsKey(currentWord) && !markovChain.get(currentWord).isEmpty()) {
				Map<String, Integer> nextWords = markovChain.get(currentWord);
				int totalOccurrences = 0;
				for (Integer count : nextWords.values()) {
					totalOccurrences += count;
				}

				int randomIndex = random.nextInt(totalOccurrences);
				int cumulativeCount = 0;
				for (Map.Entry<String, Integer> entry : nextWords.entrySet()) {
					cumulativeCount += entry.getValue();
					if (cumulativeCount > randomIndex) {
						result.append(" ").append(entry.getKey());
						currentWord = entry.getKey();
						break;
					}
				}
			} else {
				result.append(" ").append(seed);
				currentWord = seed;
			}
			kAmount--;
		}

		return result.toString();
	}

	/**
	 * A comparator for comparing entries in a word-frequency map in descending
	 * order (higher frequencies come first). Ties are broken by using the
	 * lexicographical ordering of the words (ascending).
	 */
	private static class WordFrequencyComparator implements Comparator<Map.Entry<String, Integer>> {

		/**
		 * Compares two entries based on their frequency values. Higher frequency
		 * entries are considered "less than" lower ones for priority queue ordering
		 * (i.e., they come first). If frequencies are equal, entries are compared
		 * lexicographically by key.
		 * 
		 * @param e1 - the first word-frequency map entry
		 * @param e2 - the second word-frequency map entry
		 * @return a negative one if e1 has higher priority, one if e2 does, or zero if
		 *         both entries are considered equal.
		 */
		@Override
		public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
			int freqCompare = Integer.compare(e2.getValue(), e1.getValue()); // Descending by value
			if (freqCompare != 0)
				return freqCompare;
			return e1.getKey().compareTo(e2.getKey()); // Ascending by word
		}
	}

}