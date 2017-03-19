package net.scyllamc.matan.prudence;

import java.util.ArrayList;

import com.google.gson.JsonArray;

public class Sentence {

	private ArrayList<Word> w = new ArrayList<Word>();

	public Sentence(String in, boolean parse) {

		String[] words = in.split("\\s+");

		for (int i = 0; i < words.length; i++) {

			if (words[i] != null) {

				String s = Parser.clearString(words[i]);

				if (s.length() > 0) {
					Word word = Word.getWord(s);

					if (parse && word != null) {
						if (i != words.length - 1) {
							String after = Parser.clearString(words[i + 1]);

							if (after.length() > 0) {
								word.addAfterWord(after);
							}
						}

						word.addCount(1);
						main.addWordCount(1);
						registerSentence();
					}

					w.add(word);

					System.out.print("   (" + i + ") " + s.toUpperCase() + main.newLine);
				}
			}

		}

	}

	public int getSize() {
		return w.size();
	}

	public void registerSentence() {
		for (Word word : w) {
			if (word != null) {
				word.addSentence(this.getSum());
			}
		}
	}

	public ArrayList<Word> getWords() {
		return this.w;
	}

	public JsonArray getSum() {

		JsonArray sum = new JsonArray();

		for (Word word : w) {

			if (word != null) {
				sum.add(word.getPOS());
			}
		}

		return sum;
	}

	public int compare(JsonArray arr) {

		int score = 0;

		for (int r = 0; r < this.w.size(); r++) {
			Word w1 = w.get(r);

			if (arr.size() > r) {
				String pos = arr.get(r).toString();
				pos = pos.replace("modal ", "");

				if (w1.getPOS().toString().equalsIgnoreCase(pos)) {
					score++;
				}

			}

		}

		return score;
	}

}
