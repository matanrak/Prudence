package net.scyllamc.matan.prudence.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.scyllamc.matan.prudence.LogHandler;
import net.scyllamc.matan.prudence.Word;
import net.scyllamc.matan.prudence.main;
import net.scyllamc.matan.prudence.utils.Utils;


public class ParseTask {

	public static HashMap<UUID, ParseTask> parseTasks = new HashMap<UUID, ParseTask>();
	
	private UUID ID;
	private String text;
	private boolean finished;
	private boolean started;
	private int wordsParsed;
	
	private ArrayList<Word> cachedWords = new ArrayList<Word>();
	
	private long starttime;
	private int length;
	
	public ParseTask(String text){
		this.ID = UUID.randomUUID();
		this.text = text;
		this.finished = false;
		this.started = false;
		parseTasks.put(this.ID, this);
		
		LogHandler.print(0, "Adding task, ID: " + this.ID.toString());
		ParseTaskHandler.tasks.add(this);
	}
	
	
	public void run(){

		this.started = true;
		final long starttime = System.currentTimeMillis();
		LogHandler.print(1, "Started Parsing text at: " + starttime + " , the length of the input is: " + text.length());
		
		new Thread(new Runnable() {
			public void run() {
				length = text.split("\\s+").length;

				ExecutorService executor = Executors.newFixedThreadPool(30);
				BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

				iterator.setText(text);
				int start = iterator.first();

				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {

					Runnable sentenceParser = new SentenceParser(text.substring(start, end), ID);
					executor.execute(sentenceParser);
				}
				
				executor.shutdown();

				while (!executor.isTerminated()) {}
				
				for(Word w : cachedWords){
					w.saveToFile();
				}
				
				main.setGlobalWordCount(main.wordCount);
				
				finished = true;
				LogHandler.print(1, "Finished Parsing text after: " + wordsParsed +  "/" + length + " " + Utils.timeToString(getElasped()));
				
			}
		}).start();
		
		
	}

	public long getElasped(){
		return System.currentTimeMillis() - starttime;
	}
	
	public double getPercentage(){
		return  (long) ((float) (wordsParsed + 1) / length * 100);
	}
	
	public boolean didFinish(){
		return this.finished;
	}
	
	public UUID getID(){
		return this.ID;
	}
	
	public boolean everStarted(){
		return this.started;
	}
	
	public void addCachedWord(Word w){
		if(!this.cachedWords.contains(w)){
			this.cachedWords.add(w);
		}
	}

	public void addWordsParsed(int i) {
		this.wordsParsed += i;
	}
	
	public int getWordsParsed(){
		return this.wordsParsed;
	}
	
	public int getTextLength(){
		return this.length;
	}
	
	
	
	
	
	
	
	
	
}
