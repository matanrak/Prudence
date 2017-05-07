package net.scyllamc.matan.prudence;

import java.util.UUID;

import net.scyllamc.matan.prudence.utils.Utils;

public class LogHandler {

	
	public static void print(int lvl, String msg){
		
		String pad = new String(new char[lvl]).replace("\0", "   ");
		System.out.print(pad + msg + Utils.newLine);

	}
	
	
	
	public void addParseTask(UUID ID, long elapsed){
		
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
