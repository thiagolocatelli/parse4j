package org.parse4j;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.parse4j.custom.Person;
import org.parse4j.custom.Person2;
import org.parse4j.util.ParseRegistry;

public class ParseObjectCustomTest extends Parse4JTestCase {

	@Test(expected = IllegalArgumentException.class) 
	public void parseSubclassNoAnnotation() {
		ParseRegistry.registerSubclass(Person2.class);
	}
	
	@Test
	public void parseSubclass() {
		ParseRegistry.registerSubclass(Person.class);
	}
	
	@Test
	public void save() {
		System.out.println("save(): initializing...");
		ParseRegistry.registerSubclass(Person.class);
		Person parseObject = new Person();
		parseObject.setAge(15);
		parseObject.setGender("male");
		parseObject.getString("age");
		
		try {
			parseObject.save();
			System.out.println("save(): objectId: " + parseObject.getObjectId());
			System.out.println("save(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("save(): updatedAt: " + parseObject.getUpdatedAt());
			assertNotNull("objectId should not be null", parseObject.getObjectId());
			assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
		}
		catch(ParseException pe) {
			assertNull("save(): should not have thrown ParseException", pe);
		}
	}
	
	@Test
	public void get() {
		System.out.println("get(): initializing...");
		ParseRegistry.registerSubclass(Person.class);
		Person parseObject = new Person();
		parseObject.setAge(31);
		parseObject.setGender("female");
		
		try {
			parseObject.save();
			
			ParseQuery<Person> query = ParseQuery.getQuery(Person.class);
			Person person = query.get(parseObject.getObjectId());
			System.out.println("get(): objectId - " + person.getObjectId() + "-" + parseObject.getObjectId());
			System.out.println("get(): gender - " + person.getGender() + "-" + parseObject.getGender());
			System.out.println("get(): ages - " + person.getAge() + "-" + parseObject.getAge());
			assertFalse("get(): ObjectIds should be the same", !parseObject.getObjectId().equals(person.getObjectId()));
			assertFalse("get(): Ages should be the same", parseObject.getAge() != person.getAge());
			assertFalse("get(): Genders should be the same", !parseObject.getGender().equals(person.getGender()));
		}
		catch(ParseException pe) {
			assertNull("save(): should not have thrown ParseException", pe);
		}
	}
	
	@Test
	public void saveWithChar() {
		System.out.println("save(): initializing...");
		ParseRegistry.registerSubclass(Person.class);
		Person parseObject = new Person();
		parseObject.setAge(15);
		parseObject.setGender("Suíça");
		parseObject.getString("age");
		
		try {
			parseObject.save();
			System.out.println("save(): objectId: " + parseObject.getObjectId());
			System.out.println("save(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("save(): updatedAt: " + parseObject.getUpdatedAt());
			assertNotNull("objectId should not be null", parseObject.getObjectId());
			assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
		}
		catch(ParseException pe) {
			assertNull("save(): should not have thrown ParseException", pe);
		}
	}
}
