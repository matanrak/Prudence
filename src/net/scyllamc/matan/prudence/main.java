package net.scyllamc.matan.prudence;

import com.google.gson.JsonObject;

import java.util.Timer;

public class Main {

	public static enum Mode {
		SERVER, GUI;
	}

	public static String version = "0.5";
	public static String mainDirectory = "/Users/matanrak/Prudence";

	public static int wordCount = -1;

	public static Mode mode;
	private static FileHandler fileHandler;
	private static Timer parserHandler;

	public static void main(String[] args) {

		String dir = "";
		
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			dir = "D:\\Matan Rak\\Java Projects\\Prudence";
		} else {
			dir = "/Users/matanrak/Prudence";
		}

		mainDirectory = dir;

		if (FileHandler.Files.GLOBAL_DATA.getJson().has("wordCount")) {
			wordCount = Integer.parseInt(FileHandler.Files.GLOBAL_DATA.getJson().get("wordCount").toString());
		} else {

			addWordCount(0);
			wordCount = 0;
		}

		parserHandler = new Timer();
		parserHandler.schedule(new TaskManager(), 0, 100);

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

		if (mode == Mode.GUI) {
			GUI f = new GUI();
			f.setVisible(true);
		} else {
			new Server("10.0.0.1", 9092);
		}
	}

	public static String getDir() {
		return mainDirectory;
	}

	public static Timer getParseTaskHandler() {
		return parserHandler;
	}

	public static FileHandler getFileHandler() {
		return fileHandler;
	}

	public static void addWordCount(Integer i) {
		wordCount += i;
	}

	public static void setGlobalWordCount(int i) {
		wordCount = i;

		JsonObject obj = FileHandler.Files.GLOBAL_DATA.getJson();

		if (obj.has("wordCount")) {
			obj.remove("wordCount");
		}

		obj.addProperty("wordCount", wordCount);
		
		FileHandler.Files.GLOBAL_DATA.setJson(obj);
	}

}
