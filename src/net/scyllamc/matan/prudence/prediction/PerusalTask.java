package net.scyllamc.matan.prudence.prediction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.LogHandler;
import net.scyllamc.matan.prudence.Main;
import net.scyllamc.matan.prudence.PTask;
import net.scyllamc.matan.prudence.TaskManager;
import net.scyllamc.matan.prudence.Word;
import net.scyllamc.matan.prudence.utils.Utils;


public class PerusalTask implements PTask {

	public static HashMap<UUID, PerusalTask> taskList = new HashMap<UUID, PerusalTask>();
	
	private UUID ID;
	private Word word;
	private String[] sub;
	private File file;
	private long starttime;
	private boolean finished;
	private boolean started;
	@SuppressWarnings("unused")
	private int totalPOS;
	
	public JsonObject posPool = new JsonObject();
	public JsonObject wordProbability = new JsonObject();

	
	public PerusalTask(Word word, String[] subStrings){
		this.ID = UUID.randomUUID();
		this.word = word;
		
		String[] sub = new String[subStrings.length];
		
		int r = 0; 
		for(String str : subStrings){
			Word temp = Word.getWord(str);
			sub[r] = temp.getPOS();
			r++;
		}
		
		this.sub = sub;

		taskList.put(this.ID, this);
		LogHandler.print(0, "Adding perusal task, ID: " + this.ID.toString());
	
		
		File directory = new File( Main.mainDirectory + File.separator + "cache");

		if (!directory.exists()) {
			directory.mkdir();
		}
		
		this.file = new File(directory, this.ID.toString() + ".json");

		if (!file.exists()) {
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(new JsonObject().toString());
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
		run();
	}
	
	
	public void run(){
		this.started = true;
		this.starttime = System.currentTimeMillis();
		LogHandler.print(1, "Started finding probable word");
		 
		
		new Thread(new Runnable() {
			public void run() {

				Gson gson = new Gson();
				
				ExecutorService sentenceExecutor = Executors.newFixedThreadPool(15);

				for (Entry<String, JsonElement> entry : word.getSentences().entrySet()) {
					JsonArray ja = gson.fromJson(entry.getKey(), JsonArray.class);
					Runnable sentenceParser = new SentenceComparator(jsonToArray(ja), sub , ID);
					sentenceExecutor.execute(sentenceParser);
				}
				
				sentenceExecutor.shutdown();
				while (!sentenceExecutor.isTerminated()) {}
				
				
				ExecutorService wordExecutor = Executors.newFixedThreadPool(15);			
				for (Entry<String, JsonElement> entry : word.getAfter().entrySet()) {					
					Runnable wordProbabilityAlgorithm = new WordProbability(word, entry , ID);
					wordExecutor.execute(wordProbabilityAlgorithm);
				}
				
				wordExecutor.shutdown();
				while (!wordExecutor.isTerminated()) {}
				
				
				JsonObject fin = new JsonObject();
				fin.addProperty("Word", word.toString());
				fin.addProperty("Word_Pos", word.getPOS());
				fin.addProperty("Word_Count", word.getCount());
				fin.addProperty("Total_Count", Main.wordCount);
				fin.addProperty("Probability", gson.toJson(Utils.sortJsonObject(wordProbability)));
			//	fin.add("Sentences", posPool);
			//	fin.addProperty("total_pos", totalPOS);
				
				try {
					FileWriter writer = new FileWriter(file);
					writer.write(new Gson().toJson(fin));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		
				Main.setGlobalWordCount(Main.wordCount);
				
				finished = true; 
				LogHandler.print(2, "posPool size: " + posPool.size());
				LogHandler.print(1, "Finished Perusal task ID: " + ID);
				
			}
		}).start();
		 
		
	}

	public UUID getID(){
		return this.ID;
	}

	
	public void addPOSToPool(String s){
		
		this.totalPOS++;
		
		if (this.posPool.has(s)) {
			this.posPool.addProperty(s, this.posPool.get(s).getAsInt() + 1);
		} else {
			this.posPool.addProperty(s, 1);
		}
		
	}

	public String[] jsonToArray(JsonArray jsonArray) {
	    int arraySize = jsonArray.size();
	    String[] stringArray = new String[arraySize];

	    for(int i=0; i<arraySize; i++) {
	        stringArray[i] = (String) jsonArray.get(i).toString();
	    }

	    return stringArray;
	}


	@Override
	public long getElasped() {
		return System.currentTimeMillis() - this.starttime;
	}


	@Override
	public boolean hasStarted() {
		return this.started;
	}


	@Override
	public boolean didFinish() {
		return this.finished;
	}


	@Override
	public double getPercentage() {
		return -1;
	}
	
	

	@Override
	public String getStatus() {
		String[] inf = new String[]{
		"Persual task for: " + this.word.toString(),	
		"TASK ID: " + this.ID,	
		Utils.timeToString(getElasped()) + " passed since started",
		"",
		"Tasks in pool: " + TaskManager.tasks.size()
		};
		
		return String.join(System.lineSeparator(), inf);
	}
	
	
	
}
