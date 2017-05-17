package net.scyllamc.matan.prudence.prediction;

import java.util.UUID;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.LogHandler;
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

		double P1_1 = ((float) afterCount) / word.getCount();
		double P1_2_1 = ((float) word.getCount()) / Main.wordCount;
		double P1_2_2 = ((float) after.getCount()) / Main.wordCount;

		double P1 = P1_1 * (P1_2_1 + P1_2_2);

		double P2 = 0.01;
		
		JsonObject posPool = PerusalTask.taskList.get(taskID).posPool;
		if (posPool.has(after.getPOS())) {
			LogHandler.print(2, "POS: " + after.getPOS());
			P2 = ((float) posPool.get(after.getPOS()).getAsInt()) / posPool.get(word.getPOS()).getAsInt();
		}
		
		double PF = ((float) P1) * P2;
		
		PerusalTask.taskList.get(taskID).wordProbability.addProperty(after.toString(), PF);

	}
}
