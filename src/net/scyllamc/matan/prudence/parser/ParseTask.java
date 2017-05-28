package net.scyllamc.matan.prudence.parser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.scyllamc.matan.prudence.LogHandler;
import net.scyllamc.matan.prudence.Main;
import net.scyllamc.matan.prudence.PTask;
import net.scyllamc.matan.prudence.TaskManager;
import net.scyllamc.matan.prudence.Word;
import net.scyllamc.matan.prudence.utils.Utils;


public class ParseTask implements PTask{

	public static HashMap<UUID, ParseTask> parseTasks = new HashMap<UUID, ParseTask>();
	
	
	private List<String> cachedWords =  Collections.synchronizedList(new ArrayList<String>());

	private UUID ID;
	private String text;
	private String source;
	private boolean finished;
	private boolean started;
	private int wordsParsed;
	private long starttime;
	private int length;
	
	
	public ParseTask(String source, String text){
		this.ID = UUID.randomUUID();
		this.text = text;
		this.source = source;
		this.finished = false;
		this.started = false;
		parseTasks.put(this.ID, this);
		
		LogHandler.print(0, "Adding parse task, ID: " + this.ID.toString());
		TaskManager.tasks.add(this);
	}
	
	
	@Override
	public void run(){

		this.started = true;
		starttime = System.currentTimeMillis();
		LogHandler.print(1, "Started Parsing text at: " + starttime + " , the length of the input is: " + text.length());
		
		new Thread(new Runnable() {
			public void run() {
				length = text.split("\\s+").length;

				ExecutorService executor = Executors.newFixedThreadPool(15);
				
				BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
				iterator.setText(text);
				int start = iterator.first();

				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {					
					Runnable sentenceParser = new SentenceParser(text.substring(start, end), ID);
					executor.execute(sentenceParser);
				}
				
				executor.shutdown();

				while (!executor.isTerminated()) {}
				
				for(String ws : cachedWords){
					Word.getWord(ws).save();
				}
				
				cachedWords.clear();
				
				Main.setGlobalWordCount(Main.wordCount);
				
				finished = true;
				LogHandler.print(1, "Finished Parsing text after: " + wordsParsed +  "/" + length + " " + Utils.timeToString(getElasped()));
				
			}
		}).start();
		
		
	}

	@Override
	public long getElasped(){
		return System.currentTimeMillis() - starttime;
	}
	
	@Override
	public double getPercentage(){
		return  (long) ((float) (wordsParsed + 1) / length * 100);
	}
	
	@Override
	public boolean didFinish(){
		return this.finished;
	}
	
	@Override
	public UUID getID(){
		return this.ID;
	}
	
	@Override
	public boolean hasStarted(){
		return this.started;
	}
	
	@Override
	public String getStatus() {
		String[] inf = new String[]{
		"Parse task for: " + this.length + " words",	
		"Source: " + source,
		"TASK ID: " + this.ID,	
		getPercentage() + "% Finished (" + this.wordsParsed + "/" + this.length + ")",	
		Utils.timeToString(getElasped()) + " passed since started",
		"Cahched: " + cachedWords.size(),
		"",
		"Tasks in pool: " + TaskManager.tasks.size()
		};
		
		return String.join(System.lineSeparator(), inf);
	}


	public boolean wordCahced(Word w){
		return cachedWords.contains(w);
	}
	
	public void addCachedWord(Word w){
		if(!cachedWords.contains(w.toString().toUpperCase())){
			cachedWords.add(w.toString().toUpperCase());
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
