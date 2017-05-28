package net.scyllamc.matan.prudence.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class Utils {

	public static String newLine = System.getProperty("line.separator");

	public static String clearString(String in) {
		String s = in;
		s = s.replaceAll("[^a-zA-Z]", "");

		return s;
	}

	public static String timeToString(long time) {
		return String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}


	public static Map<String, Float> sortJsonObject(JsonObject input){
   
		Gson gson = new Gson();
		Type stringStringMap = new TypeToken<LinkedHashMap<String, Float>>(){}.getType();
		
        Map<String, Float> sorted = new LinkedHashMap<>();

		Map<String, Float> map = gson.fromJson(input, stringStringMap);
		map.entrySet().stream().sorted(Map.Entry.<String, Float>comparingByValue().reversed()).limit(20).forEachOrdered(x -> sorted.put(x.getKey(), x.getValue()));
		
		return sorted;
    }
	
	
	public static String arrayToString(ArrayList<String> a) {
		String fin = "";

		for (String s : a) {
			fin += s;
			fin += Utils.newLine;
		}

		return fin;
	}
	
	public static String arrayToString(String[] a) {
		String fin = "";

		for (String s : a) {
			fin += s;
			fin += Utils.newLine;
		}

		return fin;
	}
}
