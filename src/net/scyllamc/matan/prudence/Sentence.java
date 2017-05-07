package net.scyllamc.matan.prudence;

import java.util.ArrayList;

import com.google.gson.JsonArray;

import net.scyllamc.matan.prudence.utils.Utils;

public class Sentence {

	private ArrayList<Word> words = new ArrayList<Word>();

	public Sentence(ArrayList<Word> words) {
		this.words = words;
	}

	public int getSize() {
		return this.words.size();
	}

	public void registerSentence() {
		for (Word word : this.words) {
			if (word != null) {
				word.addSentence(this.getSum());
			}
		}
	}
	
	
	public Sentence(String in){

		String[] wordsa = in.split("\\s+");

		for (int i = 0; i < wordsa.length; i++) {

			if (wordsa[i] != null) {

				String s = Utils.clearString(wordsa[i]);

				if (s.length() > 0) {
					Word word = Word.getWord(s);
					
					words.add(word);

					System.out.print("   (" + i + ") " + s.toUpperCase() + Utils.newLine);
				}
			}

		}

	}


	public ArrayList<Word> getWords() {
		return this.words;
	}

	public JsonArray getSum() {

		JsonArray sum = new JsonArray();

		for (Word word : this.words) {

			if (word != null) {
				sum.add(word.getPOS());
			}
		}

		return sum;
	}

	public int compare(JsonArray arr) {

		int score = 0;

		for (int r = 0; r < this.words.size(); r++) {
			Word w1 = this.words.get(r);

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
