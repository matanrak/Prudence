package net.scyllamc.matan.prudence;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;

import net.scyllamc.matan.prudence.utils.Utils;

public class TaskManager extends TimerTask {

	public static Queue<PTask> tasks = new LinkedList<PTask>();

	@Override
	public void run() {

		if (tasks.size() > 0) {

			LogHandler.print(0, tasks.peek().getStatus());

			if(Main.mode == Main.Mode.GUI){
				GUI.updateBar(tasks.peek().getPercentage());
				GUI.updateTime(Utils.timeToString(tasks.peek().getElasped()));
			}
			
			if (tasks.peek().didFinish()) {
				
				LogHandler.print(0, "Ended task. ID: " + tasks.peek().getID());
				tasks.poll();

				if (tasks.size() > 0) {
					tasks.peek().run();
					LogHandler.print(0, "Continuing, new task ID: " + tasks.peek().getID());
				}
				
			} else if (!tasks.peek().hasStarted()) {
				tasks.peek().run();
				LogHandler.print(0, "Starting task ID: " + tasks.peek().getID());
			}

			
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.print("\b\b\b\b\b");

	}

	public static boolean parseRunning() {
		if (tasks.size() > 0 && !tasks.peek().didFinish() && !tasks.peek().hasStarted()) {
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
