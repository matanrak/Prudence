package net.scyllamc.matan.prudence.parser;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;

import net.scyllamc.matan.prudence.LogHandler;
import net.scyllamc.matan.prudence.UI;
import net.scyllamc.matan.prudence.main;
import net.scyllamc.matan.prudence.utils.Utils;

public class ParseTaskHandler extends TimerTask {

	public static Queue<ParseTask> tasks = new LinkedList<ParseTask>();

	@Override
	public void run() {

		if (tasks.size() > 0) {

			LogHandler.print(0, "CURRENT: " + tasks.peek().getPercentage() + "%");
			LogHandler.print(1,  "STATE: " + tasks.peek().getWordsParsed() +  "/" + tasks.peek().getTextLength());

			if(main.mode == main.Mode.GUI){
				UI.updateBar(tasks.peek().getPercentage());
				UI.updateTime(Utils.timeToString(tasks.peek().getElasped()));
			}
			
			if (tasks.peek().didFinish()) {
				
				LogHandler.print(0, "Ended task. ID: " + tasks.peek().getID());
				tasks.poll();

				if (tasks.size() > 0) {
					tasks.peek().run();
					LogHandler.print(0, "Starting task ID: " + tasks.peek().getID());
				}
				
			} else if (!tasks.peek().everStarted()) {
				tasks.peek().run();
				LogHandler.print(0, "Starting task ID: " + tasks.peek().getID());
			}

			
		}

	}

	public static boolean parseRunning() {
		if (tasks.size() > 0 && !tasks.peek().didFinish() && !tasks.peek().everStarted()) {
			return true;
		}

		return false;
	}

	public static double getPercentage() {

		if (parseRunning() && tasks.size() > 0) {
			return tasks.peek().getPercentage();
		}

		return 0;
	}

	public static long getElapsedTime() {

		if (parseRunning() && tasks.size() > 0) {
			return tasks.peek().getElasped();
		}

		return 0;
	}

}
