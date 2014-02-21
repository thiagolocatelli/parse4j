package org.parse4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.parse4j.callback.CountCallback;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.GetCallback;

public class ParseQueryTestCase extends Parse4JTestCase {

	
	@Test
	public void query1() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
		query.addAscendingOrder("loosingScore")
			.addDescendingOrder("score2")
			.whereGreaterThan("score1", 6)
			.whereLessThanOrEqualTo("score2", 2)
			.whereContainedIn("playerName", Arrays.asList(names));;
		query.limit(10);
		query.skip(10);
		System.out.println(query.toREST());
	}
	
	@Test
	public void test1() {
		System.out.println("test1(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			ParseObject po = query.get("GLVPuc2X8H");
			assertNotNull("ObjectId should not be null", po.getObjectId());
		}
		catch(ParseException e) {
			assertNull("test1(): should not have thrown ParseException", e);
		}
		
	}
	
	@Test
	public void test12() {
		System.out.println("test12(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.getInBackground("GLVPuc2X8H", new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject t, ParseException parseException) {
				assertNotNull("ObjectId should not be null", t);
				assertNull("test12(): should not have thrown ParseException", parseException);
			}
		});
		sleep(1000);
	}
	
	@Test
	public void test13() {
		System.out.println("test13(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.getInBackground("NOT_FOUND", new GetCallback<ParseObject>() {
			
			@Override
			public void done(ParseObject t, ParseException parseException) {
				assertNull("ObjectId should be null", t);
				assertNull("test13(): should not have been trown", parseException);
			}
		});
		sleep(1000);
	}	

	@Test
	public void test2() {
		System.out.println("test2(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			query.whereGreaterThan("losingScore", 140);
			List<ParseObject> po = query.find();
			assertFalse("There should be 15 items on the list", po.size() != 15);
		}
		catch(ParseException e) {
			assertNull("test2(): should not have thrown ParseException", e);
		}
		
	}
	
	@Test
	public void test21() {
		System.out.println("test21(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException parseException) {
				assertFalse("test21(): There should be 15 items on the list", list.size() != 15);
				assertNull("test21(): should not have thrown ParseException", parseException);
				
			}
		});
		
		sleep(1000);	
	}
	
	@Test
	public void test22() {
		System.out.println("test21(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140);
		query.skip(4);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException parseException) {
				assertFalse("test21(): There should be 15 items on the list", list.size() != 11);
				assertNull("test21(): should not have thrown ParseException", parseException);
				
			}
		});
		
		sleep(1000);	
	}
	
	@Test
	public void test23() {
		System.out.println("test21(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140);
		query.limit(7);
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException parseException) {
				assertFalse("test21(): There should be 15 items on the list", list.size() != 7);
				assertNull("test21(): should not have thrown ParseException", parseException);
				
			}
		});
		
		sleep(1000);	
	}	
	
	@Test
	public void test3() {
		System.out.println("test3(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			query.whereGreaterThan("losingScore", 140)
			.whereLessThan("winningScore", 150)
			.whereStartsWith("losingTeam", "Denver");
			List<ParseObject> po = query.find();
			assertFalse("There should be 3 items on the list", po.size() != 3);
		}
		catch(ParseException e) {
			assertNull("test3(): should not have thrown ParseException", e);
		}
		
	}
	
	@Test
	public void test31() {
		System.out.println("test31(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140)
			.whereLessThan("winningScore", 150)
			.whereStartsWith("losingTeam", "Denver");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException parseException) {
				assertFalse("test31(): There shoult be 3 items on the list", list.size() != 3);
				assertNull("test31(): should not have thrown ParseException", parseException);
				
			}
		});
		
		sleep(1000);	
	}
	
	@Test
	public void test4() {
		System.out.println("test4(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			query.whereGreaterThan("losingScore", 140);
			int total = query.count();
			assertFalse("There should be 15 items on the list", total != 15);
		}
		catch(ParseException e) {
			assertNull("test4(): should not have thrown ParseException", e);
		}
		
	}
	
	@Test
	public void test41() {
		System.out.println("test41(): initializing...");
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140);
		query.countInBackground(new CountCallback() {
			
			@Override
			public void done(Integer count, ParseException parseException) {
				assertFalse("test41(): There should be 15 items on the list", count != 15);
				assertNull("test41(): should not have thrown ParseException", parseException);
				
			}
		});
		
		sleep(1000);	
	}
	
	@Test
	public void test5() {
		//selectKeys
		System.out.println("test5(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			query.selectKeys(Arrays.asList("losingTeam", "losingScore"));
			query.setTrace(true);
			ParseObject po = query.get("GLVPuc2X8H");
			assertNotNull("test5(): ObjectId should not be null", po.getObjectId());
		}
		catch(ParseException e) {
			assertNull("test5(): should not have thrown ParseException", e);
		}
	}
	
	@Test
	public void test6() {
		System.out.println("test6(): initializing...");
		
		try {
			ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
			ParseObject po = query.get("GLVPuc2X8H");
			//po.increment("losingScore", -3);
			//po.remove("data");
			//po.save();
			assertNotNull("test6(): ObjectId should not be null", po.getObjectId());
		}
		catch(ParseException e) {
			assertNull("test6(): should not have thrown ParseException", e);
		}
		
	}
	
}
