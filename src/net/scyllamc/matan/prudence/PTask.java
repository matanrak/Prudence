package net.scyllamc.matan.prudence;

import java.util.UUID;

public interface PTask {

	public void run();
	
	public UUID getID();
	
	public long getElasped();
	
	public boolean hasStarted();

	public boolean didFinish();

	public double getPercentage();
			
	public String getStatus();

}
