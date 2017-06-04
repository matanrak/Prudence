package net.scyllamc.matan.prudence;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class User {

	private Socket socket;
	private UUID ID;
	private File file;
	private boolean state;

	public User(Socket socket) {
		this.socket = socket;
		this.ID = UUID.randomUUID();
		this.state = false;
		
		Timer timer = new Timer();
		timer.schedule(new wait(this), 1);

	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Socket getSocket() {
		return this.socket;
	}

	public InetAddress getAddress() {
		return this.socket.getInetAddress();
	}

	public UUID getID() {
		return this.ID;
	}

	public File getFile() {
		return this.file;
	}

	public boolean getState() {
		return this.state;
	}

	static class wait extends TimerTask {

		User user;

		public wait(User user) {
			this.user = user;
		}

		@SuppressWarnings("resource")
		public void run() {
			try {

				if (user.state) {
					
					Socket socket = new ServerSocket(user.getSocket().getPort()).accept();
					
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					FileInputStream input = new FileInputStream(this.user.file);
					byte[] buffer = new byte[4096];
					
					while (input.read(buffer) > 0) {
						out.write(buffer);
					}
					
					input.close();
					out.close();	
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
