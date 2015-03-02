package org.parse4j;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.parse4j.callback.GetCallback;

public class ParseRelationTestCase extends Parse4JTestCase {

	
	@Test
	public void test1() throws ParseException {

		for(int i = 1; i < 21; i++) {
		
			// Create the post
			ParseObject myPost = new ParseObject("Post");
			myPost.put("title", "Post Number " + i);
			myPost.put("content", "Content for Post " + 1);
			myPost.save();
			
			// Create the comment
			ParseObject myComment = new ParseObject("Comment");
			myComment.put("content", "Content for Post" + i);
			myComment.put("parent", myPost);
			myComment.save();
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
			ParseObject fetchedComment = query.get(myComment.getObjectId());
			
			ParseObject postObj = fetchedComment.getParseObject("parent");
			postObj.fetchIfNeeded(new GetCallback<ParseObject>() {
		        public void done(ParseObject post, ParseException e) {
		          String title = post.getString("title");
		          assertNull("title should not be null", title);
		        }
		    });
			
			//myComment.remove("parent");
			//myComment.save();
			//postObj.delete();
			//myComment.delete();
		}
		
	}
	
	@Test
	public void list() throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
		List<ParseObject> list = query.find();
		for(ParseObject po : list) {
			System.out.println(po.getObjectId());
			po.remove("parent");
			po.save();
		}		
	}

	

	@Test
	public void relation4() throws ParseException {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
		query.whereEqualTo("title", "Post Number 20");
		
		ParseQuery<ParseObject> commentsQuery = ParseQuery.getQuery("Comment");
		commentsQuery.whereMatchesQuery("parent", query);
		List<ParseObject> find = commentsQuery.find();
		
		assertNotNull(find);
		assertTrue(find.size()>1);
	}	
	@Test
	public void relation() throws ParseException {	

		ParseObject member = new ParseObject("Member");
		member.put("name", "member name");
		member.put("country", "brazil");
		member.save();
		
		ParseObject project1 = new ParseObject("Project");
		project1.put("name", "project name 1");
		project1.put("start", new Date());
		project1.save();
		
		ParseObject project2 = new ParseObject("Project");
		project2.put("name", "project name 2");
		project2.put("start", new Date());
		project2.save();
		
		ParseRelation<ParseObject> relation = member.getRelation("projects");
		relation.add(project1);
		relation.add(project2);
		member.save();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Member");
		ParseObject fetchedMember = query.get(member.getObjectId());
		
		ParseRelation<ParseObject> fetchedRelations = member.getRelation("projects");
		ParseQuery<ParseObject> fetchQuery = fetchedRelations.getQuery();
		List<ParseObject> list = fetchQuery.find();
		
	}
	
	@Test
	public void relation3() throws ParseException {
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Member");
		ParseObject member = query.get("8EOuUQSV38");
		
		ParseRelation<ParseObject> fetchedRelations = member.getRelation("projects");
		ParseQuery<ParseObject> fetchQuery = fetchedRelations.getQuery();
		List<ParseObject> list = fetchQuery.find();
		for(ParseObject po : list) {
			System.out.println(po.getDate("start"));
		}
	}

	@Test
	public void relation2() throws ParseException {	

		ParseObject member = new ParseObject("GameScore");
		member.put("date", new Date());
		member.put("local", "usa");
		member.save();
		
		ParseObject project1 = new ParseObject("Team");
		project1.put("name", "Team C");
		project1.save();
		
		ParseObject project2 = new ParseObject("Team");
		project2.put("name", "Team D");
		project2.save();
		
		ParseRelation<ParseObject> relation = member.getRelation("opponents");
		relation.add(project1);
		//relation.add(project2);
		member.save();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
		ParseObject fetchedMember = query.get(member.getObjectId());
		
		ParseRelation<ParseObject> fetchedRelations = member.getRelation("opponents");
	}	
	
	
	@Test
	public void arrayParseObject() {
		
		// let's say we have four weapons
		ParseObject scimitar = new ParseObject("weapon");
		scimitar.put("name", "scimitar");
		ParseObject plasmaRifle = new ParseObject("weapon");
		plasmaRifle.put("name", "plasmaRifle");
		ParseObject grenade = new ParseObject("weapon");
		grenade.put("name", "grenade");
		ParseObject bunnyRabbit = new ParseObject("weapon");
		bunnyRabbit.put("name", "bunnyRabbit");
		 
		// stick the objects in an array
		ArrayList<ParseObject> weapons = new ArrayList<ParseObject>();
		weapons.add(scimitar);
		weapons.add(plasmaRifle);
		weapons.add(grenade);
		weapons.add(bunnyRabbit);
		
		ParseObject soldier = new ParseObject("soldier");
		soldier.put("name", "soldier");
		
		
	}
	
	@Test
	public void arrayStrings() throws ParseException {
		 
		// stick the objects in an array
		ArrayList<String> weapons = new ArrayList<String>();
		weapons.add("scimitar");
		weapons.add("plasmaRifle");
		weapons.add("grenade");
		weapons.add("bunnyRabbit");
		
		ParseObject soldier = new ParseObject("soldier");
		soldier.put("name", "soldier");
		soldier.put("weapons", weapons);
		soldier.save();
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery("soldier");
		ParseObject fetchedSoldier = query.get(soldier.getObjectId());
		System.out.println(fetchedSoldier.getList("weapons"));
		
	}	
	
}
