package net.scyllamc.matan.prudence.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.Main;


public class FileHandler {


	public static enum Files {

		LOG("log", Main.getDir());

		private File file;
		private File directory;

		Files(String name, String dir) {

			File directory = new File(dir);

			if (!directory.exists()) {
				directory.mkdir();
			}

			this.directory = directory;

			File file = new File(directory, name.toUpperCase() + ".json");

			if (!file.exists()) {
				try {
					FileWriter writer = new FileWriter(file);
					writer.write(new JsonObject().toString());
					writer.close();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			this.file = file;

		}

		public File getFile(){
			return this.file;
		}
		
		public File getDirectory(){
			return this.directory;
		}
		
		public JsonObject getJson() {
			JsonObject obj = new JsonObject();

			try {
				BufferedReader reader = new BufferedReader(new FileReader(this.file));
				obj = new Gson().fromJson(reader, JsonObject.class);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return obj;
		}
		
		public void setJson(JsonObject obj){
			try {
				FileWriter writer = new FileWriter(file);
				writer.write(obj.toString());
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
