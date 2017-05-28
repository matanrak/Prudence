package net.scyllamc.matan.prudence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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
	
	public JsonObject getSentences(){
		return this.sentences;
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

	public void save() {

		try {
			FileWriter writer = new FileWriter(this.getPath());
			writer.write(new Gson().toJson(this));
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	

}
