package org.parse4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseBatchTestCase extends Parse4JTestCase {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseBatchTestCase.class);

	@Test
	public void testReadData() throws ParseException{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
		query.limit(20);
		List<ParseObject> find = query.find();
		
		for (int i = 0; i < find.size(); i++) {
			ParseObject current = find.get(i);
			System.out.println(current.get("title"));
			System.out.println(current.getUpdatedAt());
		}
	}
	@Test
	public void testBatchInsert() throws ParseException{
		ParseBatch batcher = new ParseBatch();
		for (int i = 0; i < 20; i++) {
			ParseObject obj = new ParseObject("Post");
			obj.put("title", "input message " + i);
			batcher.createObject(obj);
			
		}
		JSONArray batch = batcher.batch();
		assertNotNull(batch);
		assertTrue(batch.length() == 20);
		
	}
	@Test
	public void testBatchUpdate() throws ParseException{
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
		query.limit(20);
		query.addDescendingOrder("updateAt");
		List<ParseObject> find = query.find();
		LOGGER.info("size is " + find.size());
		
		ParseBatch batcher = new ParseBatch();
		for (int i = 0; i < find.size(); i++) {
			ParseObject current = find.get(i);
			current.put("title", "input updated message " + i);
			batcher.updateObject(current);
		}

		//send batch update
		JSONArray batch = batcher.batch();
		assertNotNull(batch);
		assertTrue(batch.length() == 20);
		
		//refresh results
		find = query.find();
		for (int i = 0; i < find.size(); i++) {
			assertEquals("not equal", "input updated message " + i , find.get(i).getString("title"));
		}
	}
	@Test
	public void testBatchDelete() throws ParseException{
		final int size = 5;
		//first insert 5 objects that should be deleted !
		for(int i = 0;i<size;i++){
			ParseObject obj = new ParseObject("Post");
			obj.put("title", "input message " + i);
			obj.save();
		}

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
		query.limit(size);
		query.addDescendingOrder("updatedAt");
		List<ParseObject> find = query.find();
		ParseBatch batcher = new ParseBatch();
		for (int i = 0; i < find.size(); i++) {
			ParseObject current = find.get(i);
			LOGGER.info("object found " + current.get("title"));
			LOGGER.info("updated at " + current.getUpdatedAt());
			batcher.deleteObject(current);
		}
		JSONArray batch = batcher.batch();
		assertNotNull(batch);
		assertEquals(batch.length(), find.size());
	}
	
	@Test(expected = IllegalArgumentException.class) 
	public void testFailureOnDelete() throws ParseException{
		ParseBatch batch = new ParseBatch();
		ParseObject obj = new ParseObject();
		batch.deleteObject(obj);
		batch.batch();
	}
	@Test(expected = IllegalArgumentException.class) 
	public void testFailureOnUpdate() throws ParseException{
		ParseBatch batch = new ParseBatch();
		ParseObject obj = new ParseObject();
		batch.updateObject(obj);
		batch.batch();
	}
}
