package net.scyllamc.matan.prudence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Parser {

	public static void Parse(final String in) {


		final long starttime = System.currentTimeMillis();

		new Thread(new Runnable() {
			public void run() {

				
				int len = in.split("\\s+").length;
				int index = 0;
				
				BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

				iterator.setText(in);
				int start = iterator.first();
			
				for(int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()){

					String s = in.substring(start,end);
					
					Sentence sentence = new Sentence(s, true);
					index += sentence.getSize();
					
					long per = (long) ((float) (index + 1) / len * 100);
					main.updateBar(per);

					long elapsed = System.currentTimeMillis() - starttime;
					String time = String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(elapsed), TimeUnit.MILLISECONDS.toSeconds(elapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed)));
					main.updateTime(time);

					System.out.print(per + "% -->" +  main.newLine);
				}

			}
		}).start();

	}
	

	public static String clearString(String in) {

		String s = in;
		s = s.replaceAll("[^a-zA-Z]", "");

		return s;
	}
	
	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }
	 
	 public static JsonObject readJsonFromUrl(String url) throws IOException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      JsonObject json = new Gson().fromJson(jsonText, JsonObject.class);
		      return json;
		    } finally {
		      is.close();
		    }
		  }
}
