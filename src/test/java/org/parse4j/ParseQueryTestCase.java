package org.parse4j;

import java.util.Arrays;

import org.junit.Test;

public class ParseQueryTestCase extends Parse4JTestCase {

	
	@Test
	public void query1() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
		String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
		query.addAscendingOrder("score1")
			.addDescendingOrder("score2")
			.whereGreaterThan("score1", 6)
			.whereLessThanOrEqualTo("score2", 2)
			.whereContainedIn("playerName", Arrays.asList(names));;
		query.setLimit(10);
		query.setSkip(10);
		System.out.println(query.toREST());
	}



}
