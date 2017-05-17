package net.scyllamc.matan.prudence.utils;

import java.util.concurrent.TimeUnit;

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



	
}
