package org.parse4j;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ParseAnalyticsTestCase extends Parse4JTestCase {

	
	@Test
	public void trackAppOpened() {
		System.out.println("trackAppOpened(): initializing...");
		ParseAnalytics.trackAppOpened();
		sleep(1000);
	}

	@Test
	public void trackJunitTest() {
		System.out.println("trackJunitTest(): initializing...");
		ParseAnalytics.trackEvent("JUnitTestStarted");
		sleep(1000);
	}	
	
	@Test
	public void trackJunitTest2() {
		System.out.println("trackJunitTest2(): initializing...");
		Map<String, String> dimensions = new HashMap<String, String>();
		dimensions.put("attr1", "10");
		dimensions.put("attr2", "127.0.0.1");
		ParseAnalytics.trackEvent("JUnitTestStarted");
		sleep(1000);
	}	

}
