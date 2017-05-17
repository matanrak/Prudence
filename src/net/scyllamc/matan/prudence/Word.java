package net.scyllamc.matan.prudence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.utils.JsonUrl;
import net.scyllamc.matan.prudence.utils.Utils;

public class Word {

	public static HashMap<String, Word> cache = new HashMap<String, Word>();

	public static Word getWord(String s) {

		if (cache.containsKey(s.toUpperCase())) {
			Word word = cache.get(s.toUpperCase());
			return word;
		}

		File f = new File(Main.getDir() + File.separator + s.toUpperCase() + ".json");

		if (f.exists()) {

			BufferedReader reader;
			Word word = null;

			try {
				reader = new BufferedReader(new FileReader(f));
				word = new Gson().fromJson(reader, Word.class);

				if (word != null) {
					word.checkPOS();
				}

				return word;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		return new Word(s);
	}

	private String word;
	private String pos;
	private int count;
	private JsonObject after;
	private JsonObject sentences;

	private Word(String s) {

		this.word = s;
		this.after = new JsonObject();
		
		cache.put(this.word.toUpperCase(), this);

		new Thread(new Runnable() {
			public void run() {
				checkPOS();
			}
		}).start();
	}

	public String toString() {
		return this.word;
	}

	public String getPOS() {

		if (this.pos != null) {
			return this.pos;
		}

		this.pos = checkPOS();

		return this.pos;
	}

	public Integer getCount() {
		return this.count;
	}

	public JsonObject getAfter() {
		return this.after;
	}

	public String getPath() {
		return Main.getDir() + File.separator + this.word.toUpperCase() + ".json";
	}

	public int addCount(int i) {
		this.count += i;

		return this.count;
	}

	public String checkPOS() {

		if (pos == null || pos == "") {

			try {
				
				JsonObject obj = JsonUrl.readJsonFromUrl("http://api.pearson.com/v2/dictionaries/entries?headword=" + word);

				if (obj != null && obj.size() > 0) {
					JsonArray a = obj.get("results").getAsJsonArray();

					String currentPos = "noun";

					for (JsonElement el : a) {

						if (el != null && el.getAsJsonObject().has("part_of_speech")) {

							String temppos = el.getAsJsonObject().get("part_of_speech").getAsString();

							if (temppos != null && !temppos.equalsIgnoreCase(currentPos) && !temppos.equalsIgnoreCase("noun")) {
								currentPos = temppos;
							}

						}
					}

					this.pos = currentPos;
					return this.pos;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			LogHandler.print(2, "[!] POS NOT FOUND FOR: " + word + Utils.newLine);

		}

		return this.pos;
	}

	public JsonObject addAfterWord(String s) {

		int count = 0;

		if (after.has(s.toUpperCase())) {

			count = Integer.parseInt(this.after.get(s.toUpperCase()).toString());
			after.remove(s.toUpperCase());
		}

		count++;

		after.addProperty(s.toUpperCase(), count);

		return this.after;
	}

	public JsonObject addSentence(JsonArray a) {

		int count = 0;

		if (sentences == null) {
			this.sentences = new JsonObject();
		}

		if (sentences.has(a.toString())) {

			count = Integer.parseInt(sentences.get(a.toString()).toString());
			sentences.remove(a.toString());
		}

		count++;

		sentences.addProperty(a.toString(), count);

		return this.sentences;
	}

	public void saveToFile() {

		try {
			FileWriter writer = new FileWriter(this.getPath());
			writer.write(new Gson().toJson(this));
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	
	
	
	public Word getProbableAfterWord(String sentence) {

		System.out.println("Trying to get a probable word");

		if (after.size() > 0) {

			Sentence sen = new Sentence(sentence);
			int senindex = getLocInSentence(sen);
			String nextPOS = "";
			float nextPOSCount = 1;
			float comp = -1;

			Gson gson = new Gson();

			
			System.out.print("Current Word: " + this.toString() + Utils.newLine);
			System.out.print("Sentence: " + sen.getSum().toString() +  Utils.newLine);

			System.out.print(" Searching for matches: " +  Utils.newLine);
			for (Entry<String, JsonElement> entry : sentences.entrySet()) {


				JsonArray ja = gson.fromJson(entry.getKey(), JsonArray.class);
				int count = Integer.parseInt(entry.getValue().toString());

				if (ja.size() >= senindex && ja.size() != 1 && ja.size() < senindex + 7) {
					float nc = (float) sen.compare(ja);

					nc += ((float) count) * 0.2;

					if (nc > comp) {
						System.out.print("  (" + nc + ") " + ja.toString() + Utils.newLine);
						comp = nc;
						nextPOS = nextPOS.replace("modal ", "");
						nextPOSCount = ((float) 0.05) * count;

						if(ja.size() > senindex + 1){
							nextPOS = Utils.clearString(ja.get(senindex + 1).toString());
						}
					}
				}
			}
			
			if(nextPOS != ""){
				System.out.print(" Found probable next POS: (" + comp + ") " + nextPOS +  Utils.newLine);
			}else{
				System.out.print(" No match found." +  Utils.newLine);
			}

			float prob = 0;
			Word top = null;
			final int total = Main.wordCount;
			
			System.out.print("---------" + Utils.newLine );
			System.out.print("Calculating probable word: (" + after.size() + ")" +  Utils.newLine);
			
			for (Entry<String, JsonElement> entry : after.entrySet()) {
				String s = entry.getKey();
				int value = Integer.parseInt(entry.getValue().toString());

				if (s != null && s.length() > 0) {
					Word ent = Word.getWord(s);
					
					if (ent != null) {
						int count = ent.getCount();

						if (count > 0) {

							float p1 = ((float) value * 2) / getCount();
							float p2 = ((float) count) / total;

							float penalty = 1;

							int itemcount = Main.itemCount(sentence, ent);

							for (int r = 0; r < itemcount; r++) {

								if (penalty >= 0.30) {
									penalty -= 0.30;
								}
							}

							float bonus = 0;

							if (ent.getPOS().equalsIgnoreCase(nextPOS)) {
								bonus = nextPOSCount;
							}

							float pf = ((float) p1 - p2) * penalty;
							pf += bonus;

							if (pf > prob && !ent.toString().equalsIgnoreCase(this.toString()) && !ent.toString().equalsIgnoreCase("the")) {
								
								System.out.print(" Word: " + ent.toString() +  Utils.newLine);

								if (bonus != 0) {
									System.out.print("  Bonus: " + bonus + Utils.newLine);
								}
								
								if(penalty != 0){
									System.out.print("  Penalty: " + penalty + Utils.newLine);
								}
								
								System.out.print("  Fin: " + ent.toString() + " --> "  + pf + Utils.newLine);
								prob = pf;
								top = ent;
							}
						}
					}
				}
			}
			return top;
		}

		return null;
	}

	
	
	
	
	public JsonObject getSentences(){
		return this.sentences;
	}
	
	public int getLocInSentence(Sentence sen) {
		int c = 0;

		for (Word word : sen.getWords()) {

			if (word != null) {
				if (word.getPOS().equalsIgnoreCase(this.getPOS())) {
					return c;
				}
			}
			c++;
		}

		return c;
	}

}
