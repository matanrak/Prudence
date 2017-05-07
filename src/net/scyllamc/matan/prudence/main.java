package net.scyllamc.matan.prudence;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.parser.ParseTaskHandler;
import net.scyllamc.matan.prudence.utils.FileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;


public class main extends JFrame {

	public static enum Mode {
		SERVER, GUI;
	}

	public static String version = "0.3";
	public static String mainDirectory = "/Users/matanrak/Prudence";

	private static final long serialVersionUID = 1L;

	public static int wordCount = -1;
	
	public static Mode mode;
	private static UI ui;
	private static FileHandler fileHandler;
	private static Timer parserHandler;

	private static File config;

	public static void main(String[] args) {

		String dir = mainDirectory;

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			dir = "D:\\Matan Rak\\Java Projects\\Prudence";
		} else {
			dir = "/Users/matanrak/Prudence";
		}
		parserHandler = new Timer();
		parserHandler.schedule(new ParseTaskHandler(), 0, 100);

		try {

			for (int r = 0; r < args.length; r++) {

				if (args[r] != null) {

					if (r == 0) {

						if (args[r].equalsIgnoreCase("GUI")) {
							mode = Mode.GUI;
						} else {
							mode = Mode.SERVER;
						}

					} else if (r == 1) {
						dir = args[r];

					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		mainDirectory = dir;
		config = new File(mainDirectory + File.separator + "_CONFIG_.json");

		if (mode == Mode.GUI) {
			UI f = new UI();
			f.setVisible(true);
			ui = f;
		} else {
			Server.run();
		}

		wordCount = getGlobalWordCount();
	}

	public static Timer getParseTaskHandler() {
		return parserHandler;
	}

	public static FileHandler getFileHandler() {
		return fileHandler;
	}

	@SuppressWarnings("static-access")
	public static int getGlobalWordCount() {

		if (main.wordCount == -1) {

			if (config == null) {
				config = new File(mainDirectory + File.separator + "_CONFIG_.json");
			}

			if (config.exists()) {

				BufferedReader reader;
				JsonObject obj = null;

				try {
					reader = new BufferedReader(new FileReader(config));
					obj = new Gson().fromJson(reader, JsonObject.class);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				if (obj.has("globalCount")) {
					main.wordCount = Integer.parseInt(obj.get("globalCount").toString());
				}

			} else {

				addWordCount(0);
				main.wordCount = 0;

			}

			if (mode == Mode.GUI) {

				if (ui.labelCount != null) {
					ui.labelCount.setText("Global word count: " + main.wordCount + " Cache: " + Word.cache.size());
				}
			}

		}
		return main.wordCount;
	}

	public static void addWordCount(Integer i) {

		wordCount += i;
	}
	
	public static void setGlobalWordCount(int i){

		JsonObject obj = new JsonObject();
		obj.addProperty("globalCount", i);

		if (config.exists()) {

			BufferedReader reader;

			try {
				reader = new BufferedReader(new FileReader(config));
				obj = new Gson().fromJson(reader, JsonObject.class);

				if (obj.has("globalCount")) {
					obj.remove("globalCount");
				}

				obj.addProperty("globalCount", i);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		try {
			FileWriter writer = new FileWriter(config);
			writer.write(obj.toString());
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static int itemCount(String s, Word w) {

		String[] words = s.split("\\s+");
		int count = 0;

		for (int i = 0; i < words.length; i++) {

			if (words[i].equalsIgnoreCase(w.toString())) {
				count++;
			}
		}

		return count;
	}

	public static String getDir() {
		return mainDirectory;
	}

}
