package net.scyllamc.matan.prudence.prediction;

import java.util.UUID;
import java.util.Map.Entry;

import com.google.gson.JsonElement;

import net.scyllamc.matan.prudence.Main;
import net.scyllamc.matan.prudence.Word;

public class WordProbability implements Runnable {

	private Word word;
	private Word after;
	private int afterCount;
	private UUID taskID;

	WordProbability(Word word, Entry<String, JsonElement> entry, UUID taskID) {
		this.word = word;
		this.after = Word.getWord(entry.getKey());
		this.afterCount = entry.getValue().getAsInt();
		this.taskID = taskID;
		run();
	}

	@Override
	public void run() {

		double P1 = 
				
				
				float p1 = ((float) value * 2) / getCount();
		float p2 = ((float) count) / total;

		float penalty = 1;

		int itemcount = Main.itemCount(sentence, ent);

		for (int r = 0; r < itemcount; r++) {

			if (penalty >= 0.30) {
				penalty -= 0.30;
			}
		}
	}
}
