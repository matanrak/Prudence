package net.scyllamc.matan.prudence;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import net.scyllamc.matan.prudence.learning.Website;
import net.scyllamc.matan.prudence.learning.WebsiteFetcher;

public class Server {

	String host = "10.0.0.1";
	static int port = 9092;

	public static void run() {
		try {
			boolean a = true;

			System.out.print("RUNNING SERVER!" +  System.lineSeparator());

			new WebsiteFetcher(Website.NYCTIMES);
		
			
			while (a) {

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("Enter String" + System.lineSeparator());
				
				String s = br.readLine();
				
					System.out.println("TEST: " + s +  System.lineSeparator());
				

				/**
				ServerSocket listener = new ServerSocket(port);

				Socket socket = listener.accept();

				LogHandler.print(0, "RECIVED REQUEST FROM: " + socket.getInetAddress());

				InputStream input = socket.getInputStream();
				byte[] buff = new byte[512];
				int read;

				while ((read = input.read(buff)) != -1) {

					String data = new String(buff, 0, read);

					LogHandler.print(1, "REQUEST FOR WORD AFTER: " + data);

					String[] words = data.split("\\s+");
					String s = words[words.length - 1].replaceAll("[^a-zA-Z]", "");

					Word word = Word.getWord(s);

					LogHandler.print(1, "LAST WORD: " + word.toString() + " " + word.getCount());
					Word prob = word.getProbableAfterWord(data);

					LogHandler.print(1, "RETURN: " + prob);

					DataOutputStream output = new DataOutputStream(socket.getOutputStream());
					output.writeUTF(prob.toString());

				}

				input.close();
				socket.close();
				listener.close();
				**/

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
