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

public class Word {

	public static HashMap<String, Word> cache = new HashMap<String, Word>();

	public static Word getWord(String s) {

		if (cache.containsKey(s.toUpperCase())) {
			Word word = cache.get(s.toUpperCase());
			return word;
		}

		File f = new File(main.getDir() + File.separator + s.toUpperCase() + ".json");

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
		this.save();

		if (cache.size() > 150000) {
			cache.clear();
		}

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
		this.save();
		
		return this.pos;
	}

	public Integer getCount() {
		return this.count;
	}

	public JsonObject getAfter() {
		return this.after;
	}

	public String getPath() {
		return main.getDir() + File.separator + this.word.toUpperCase() + ".json";
	}

	public int addCount(int i) {
		this.count += i;
		this.save();

		return this.count;
	}

	public String checkPOS() {

		if (pos == null || pos == "") {

			try {
				JsonObject obj = Parser.readJsonFromUrl("http://api.pearson.com/v2/dictionaries/entries?headword=" + word);

				if (obj != null && obj.size() > 0) {
					JsonArray a = obj.get("results").getAsJsonArray();

					String currentPos = "noun";

					for (JsonElement el : a) {

						if (el != null && el.getAsJsonObject().has("part_of_speech")) {

							String temppos = el.getAsJsonObject().get("part_of_speech").getAsString();

							if (temppos != null && !temppos.equalsIgnoreCase(currentPos) && !temppos.equalsIgnoreCase("noun")) {
								System.out.print("[!] CHECKING POS IS: " + temppos +  " FOR: " + word + main.newLine);
								currentPos = temppos;
							}

						}
					}

					this.pos = currentPos;
					System.out.print("[!] FOUND  POS " + this.pos + " for " + word + main.newLine);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.print("[!] POS NOT FOUND FOR: " + word + main.newLine);

		return "NoUn";
	}

	public JsonObject addAfterWord(String s) {

		int count = 0;

		if (after.has(s.toUpperCase())) {

			count = Integer.parseInt(this.after.get(s.toUpperCase()).toString());
			after.remove(s.toUpperCase());
		}

		count++;

		after.addProperty(s.toUpperCase(), count);

		this.save();

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

		this.save();
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

	public Word getProbableAfterWord(String sentence) {

		if (after.size() > 0) {

			Sentence sen = new Sentence(sentence, false);
			int senindex = getLocInSentence(sen);
			String nextPOS = "";
			float nextPOSCount = 1;
			float comp = -1;

			Gson gson = new Gson();

			System.out.print(" current sentence [ " + sen.getSum().toString() + " ]");

			for (Entry<String, JsonElement> entry : sentences.entrySet()) {

				JsonArray ja = gson.fromJson(entry.getKey(), JsonArray.class);
				int count = Integer.parseInt(entry.getValue().toString());

				if (ja.size() > senindex && ja.size() < senindex + 7) {
					float nc = (float) sen.compare(ja);

					nc += ((float) count) * 0.2;

					if (nc > comp) {
						System.out.print("	searching matched sentences [ " + nc + " ]  --> " + ja.toString() + main.newLine);
						comp = nc;
						nextPOS = nextPOS.replace("modal ", "");
						nextPOSCount = ((float) 0.05) * count;
						nextPOS = Parser.clearString(ja.get(senindex).toString());
					}
				}
			}

			System.out.print(" [!] found matching next pos [ " + nextPOS + " ]  --> " + comp);

			float prob = 0;
			Word top = null;
			final int total = main.getTotalWordCount();

			for (Entry<String, JsonElement> entry : after.entrySet()) {
				String s = entry.getKey();
				int value = Integer.parseInt(entry.getValue().toString());

				if (s != null && s.length() > 0) {
					Word ent = Word.getWord(s);

					if (ent != null) {
						int count = ent.getCount();

						if (count > 0) {

							float p1 = ((float) value * 2) / getCount();
							float p2 = ((float) count * 4) / total;

							float penalty = 1;

							int itemcount = main.itemCount(sentence, ent);

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

							if (pf > prob && ent.toString() != this.toString()) {
								if (bonus != 0) {
									System.out.print(" Bonus " + bonus + " added being: " + nextPOS + main.newLine);
								}
								System.out.print(ent.toString() + " after " + this.toString() + " --> " + p1 + " * " + p2 + " * " + penalty + " = " + pf + main.newLine);
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
