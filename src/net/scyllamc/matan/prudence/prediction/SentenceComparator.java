package net.scyllamc.matan.prudence.prediction;

import java.util.UUID;

import net.scyllamc.matan.prudence.utils.Utils;

public class SentenceComparator implements Runnable {

	private String[] sentence;
	private String[] sub;
	private UUID taskID;
	
	SentenceComparator(String[] sentence, String[] sub, UUID taskID) {
		this.sentence = sentence;
		this.sub = sub;
		this.taskID = taskID;
		run();

	}

	
	@Override
	public void run() {
		
		for (int i = 0; i < sentence.length - sub.length; i++) {
			boolean found = true;

			for (int j = 0; j < sub.length; j++) {
				
				if (sub[j].equalsIgnoreCase(Utils.clearString(sentence[i + j]))) {
					found = false;
				}
			}

			if (found) {
				PerusalTask.taskList.get(taskID).addPOSToPool(sentence[i +1]);
			}
		}
		
	}
}
