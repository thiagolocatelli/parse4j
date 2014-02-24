package org.parse4j.bolts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.parse4j.Parse4JTestCase;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;

public class TestBolts extends Parse4JTestCase {
	
	@Test
	public void testBackgroundCallWaiting() throws Exception {
		
		Task<List<ParseObject>> task = Task.callInBackground(new Callable<List<ParseObject>>() {
			
			@Override
			public List<ParseObject> call() throws Exception {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
				query.whereGreaterThan("losingScore", 140);
				query.skip(4);
				System.out.println("before running");
				return query.find();
			}
			
		});
		
		//task.waitForCompletion();
		assertTrue(task.isCompleted());
		assertEquals(11, task.getResult().size());
		System.out.println("finish");
	}
	
	@Test
	public void testForResult() throws Exception {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
		query.whereGreaterThan("losingScore", 140);
		query.skip(4);
		Task<List<ParseObject>> task = Task.forResult(query.find());
		
		//task.waitForCompletion();
		assertTrue(task.isCompleted());
		assertEquals(11, task.getResult().size());
	}	
	
	
	@Test
	public void testContinuationWith() throws InterruptedException {
				
		
		Task<List<ParseObject>> task = Task.callInBackground(new Callable<List<ParseObject>>() {
			
			@Override
			public List<ParseObject> call() throws Exception {
				ParseQuery<ParseObject> query = ParseQuery.getQuery("games");
				query.whereGreaterThan("losingScore", 140);
				query.skip(4);
				return query.find();
			}
			
		}).continueWith(new Continuation<List<ParseObject>, List<ParseObject>>() {

			@Override
			public List<ParseObject> then(Task<List<ParseObject>> task) throws Exception {
				System.out.println("completed");
				return null;
			}
			
		}).onSuccess(new Continuation<List<ParseObject>, List<ParseObject>>() {

			@Override
			public List<ParseObject> then(Task<List<ParseObject>> task) throws Exception {
				System.out.println("onSucess 1");
				return null;
			}
			
		}).onSuccess(new Continuation<List<ParseObject>, List<ParseObject>>() {

			@Override
			public List<ParseObject> then(Task<List<ParseObject>> task) throws Exception {
				System.out.println("onSucess 2");
				return null;
			}
			
		}).onSuccess(new Continuation<List<ParseObject>, List<ParseObject>>() {

			@Override
			public List<ParseObject> then(Task<List<ParseObject>> task) throws Exception {
				System.out.println("onSucess 3");
				return null;
			}
			
		});
		
		task.waitForCompletion();
	}

}
