package org.parse4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParseExecutor {
	
	private static ExecutorService executor;
	
	static {
		executor = Executors.newFixedThreadPool(10);
	}
	
	public static void runInBackground(Runnable runnable) {
		executor.execute(runnable);
	}
	
	public static ExecutorService getExecutor() {
		return executor;
	}

}
