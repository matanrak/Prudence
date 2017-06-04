package net.scyllamc.matan.prudence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import net.scyllamc.matan.prudence.learning.Website;
import net.scyllamc.matan.prudence.learning.WebsiteFetcher;
import net.scyllamc.matan.prudence.prediction.PerusalTask;
import net.scyllamc.matan.prudence.utils.Utils;

public class Server {

	private String host;
	private int port;

	public Server(String host, int port) {
		try {

			if (host != null) {
				this.host = host;
			} else {
				this.host = "10.0.0.1";
			}

			this.port = port;
			
			
			write("Prudence version " + Main.version + " server mode started." + System.lineSeparator());

			Timer timer = new Timer();
			timer.schedule(new listen(this), 1);

			boolean a = true;

			while (a) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String[] args = br.readLine().split(" ");

				write("RECIVED: " + Utils.arrayToString(args) + " A[0] " + args[0]);

				if (args[0].equalsIgnoreCase("learn")) {

					if (args.length > 1 && args[1] != null && args[1].equalsIgnoreCase("all")) {
						write("Fetching articles from all known websites (" + Website.values().length + ")");

						for (Website site : Website.values()) {
							new WebsiteFetcher(site);
						}

					} else if (args.length > 1 && args[1] != null && Website.fromString(args[1]) != null) {
						write("Fetching articles from " + Website.fromString(args[1]).toString());
						new WebsiteFetcher(Website.fromString(args[1]));

					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int getPort(){
		return this.port;
	}
	
	public String getAddress(){
		return this.host;
	}

	public static void write(String str) {
		System.out.print(str + System.lineSeparator());
	}

	static class listen extends TimerTask {

		Server server;
		public listen(Server server){
			this.server = server;
		}
		
		public void run() {
			try {
				ServerSocket listener = new ServerSocket(this.server.getPort());

				Socket socket = listener.accept();
				write("RECIVED REQUEST FROM: " + socket.getInetAddress());

				InputStream input = socket.getInputStream();
				byte[] buff = new byte[512];
				int read;

				while ((read = input.read(buff)) != -1) {

					String data = new String(buff, 0, read);
					User user = new User(socket);

					String[] words = data.split("\\s+");
					String s = words[words.length - 1].replaceAll("[^a-zA-Z]", "");

					new PerusalTask(Word.getWord(s), words, user);
				}

				input.close();
				socket.close();
				listener.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
