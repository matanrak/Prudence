package net.scyllamc.matan.prudence.parser;

import java.util.ArrayList;
import java.util.UUID;

import net.scyllamc.matan.prudence.Sentence;
import net.scyllamc.matan.prudence.Word;
import net.scyllamc.matan.prudence.main;
import net.scyllamc.matan.prudence.utils.Utils;

public class SentenceParser implements Runnable {

	private String text;
	private UUID taskID;
	
	SentenceParser(String text, UUID taskID) {
		this.text = text;
		this.taskID = taskID;
	}

	@Override
	public void run() {
		
		ArrayList<Word> array = new ArrayList<Word>();
		String[] words = text.split("\\s+");

		for (int i = 0; i < words.length; i++) {

			if (words[i] != null) {
				String s = words[i].replaceAll("[^a-zA-Z]", "");

				if (s.length() > 0) {
					
					Word word = Word.getWord(s);

					if (word != null) {
						if (i != words.length - 1) {
							String after = Utils.clearString(words[i + 1]);

							if (after.length() > 0) {
								word.addAfterWord(after);
							}
						}
						
						word.addCount(1);
						ParseTask.parseTasks.get(taskID).addCachedWord(word);
						
						main.addWordCount(1);
					}
					array.add(word);

				}
			}

		}
		
		ParseTask.parseTasks.get(taskID).addWordsParsed(words.length);
		new Sentence(array).registerSentence();
		
	}
}
