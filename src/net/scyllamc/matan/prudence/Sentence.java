package net.scyllamc.matan.prudence;

import java.util.ArrayList;
import com.google.gson.JsonArray;

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
				word.addSentence(this.getAsJsonArray());
			}
		}
	}
	

	public ArrayList<Word> getWords() {
		return this.words;
	}

	public JsonArray getAsJsonArray() {

		JsonArray sum = new JsonArray();

		for (Word word : this.words) {

			if (word != null) {
				sum.add(word.getPOS());
			}
		}

		return sum;
	}


}
