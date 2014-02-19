package org.parse4j;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parse4j.callback.DeleteCallback;
import org.parse4j.callback.SaveCallback;

@RunWith(JUnit4.class)
public class ParseObjectCRUDTestCase extends Parse4JTestCase {
	
	@Test
	public void save() {
		System.out.println("save(): initializing...");
		ParseObject parseObject = getParseObject(CLASS_NAME);
		
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
	public void saveWithFile() {
		System.out.println("saveWithFile(): initializing...");
		ParseObject parseObject = getParseObject(CLASS_NAME);
		byte[] data = null;
		ParseFile file = null;
		
		try {
			
			data = getBytes("/parse.png");
			file = new ParseFile("parse.png", data);
			file.save();
			parseObject.put("pngFile", file);

			data = getBytes("/parse.pdf");
			file = new ParseFile("parse.pdf", data);
			file.save();
			parseObject.put("pdfFile", file);
			
			data = getBytes("/parse.docx");
			file = new ParseFile("parse.docx", data);
			file.save();
			parseObject.put("docxFile", file);
			
			parseObject.save();
			System.out.println("saveWithFile(): objectId: " + parseObject.getObjectId());
			System.out.println("saveWithFile(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("saveWithFile(): updatedAt: " + parseObject.getUpdatedAt());
			assertNotNull("objectId should not be null", parseObject.getObjectId());
			assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
		}
		catch(ParseException pe) {
			assertNull("saveWithFile(): should not have thrown ParseException", pe);
		}
	}
	
	@Test
	public void update() {
		System.out.println("update(): initializing...");
		ParseObject parseObject = getParseObject(CLASS_NAME);
		
		try {
			parseObject.save();
			System.out.println("update(): objectId: " + parseObject.getObjectId());
			System.out.println("update(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("update(): updatedAt: " + parseObject.getUpdatedAt());
			System.out.println("update(): initial name: " + parseObject.getString("name"));
			System.out.println("update(): initial int: " + parseObject.getInt("int"));
			System.out.println("update(): initial double: " + parseObject.getDouble("double"));			
			assertNotNull("objectId should not be null", parseObject.getObjectId());
			assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
		
			parseObject.put("name", "updated");
			parseObject.increment("int");
			parseObject.decrement("double");
			parseObject.save();			
			System.out.println("update(): name: " + parseObject.getString("name"));
			System.out.println("update(): incrementedInt: " + parseObject.getInt("int"));
			System.out.println("update(): incrementedInt: " + parseObject.getDouble("double"));
			System.out.println("update(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("update(): updatedAt: " + parseObject.getUpdatedAt());			
			
		}
		catch(ParseException pe) {
			assertNull("update(): should not have thrown ParseException", pe);
		}
	}	
	
	@Test
	public void delete() {
		System.out.println("delete(): initializing...");
		ParseObject parseObject = getParseObject(CLASS_NAME);
		
		try {
			System.out.println("delete(): saving parseObject first...");
			parseObject.save();
			System.out.println("delete(): objectId: " + parseObject.getObjectId());
			assertNotNull("delete(): objectId should not be null", parseObject.getObjectId());
			assertNotNull("delete(): createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("delete(): updatedAt should not be null", parseObject.getUpdatedAt());
			System.out.println("delete(): deleting parseObject with objectId " + parseObject.getObjectId());
			parseObject.delete();
			assertNull("delete(): parseObject deleted, objectId should be null", parseObject.getObjectId());
			assertNull("delete(): parseObject deleted, createdAt should not be null", parseObject.getCreatedAt());
			assertNull("delete(): parseObject deleted, updatedAt should not be null", parseObject.getUpdatedAt());
		}
		catch(ParseException pe) {
			assertNull("delete() should not have thrown ParseException", pe);
		}		
	}	
	

	public void saveInBackground() {
		System.out.println("saveInBackground(): initializing...");
		final ParseObject parseObject = getParseObject(CLASS_NAME);
		
		parseObject.saveInBackground(new SaveCallback() {			
			@Override
			public void done(ParseException parseException) {
				assertNull("saveInBackground(): parseException should not be null", parseException);
				System.out.println("saveInBackground(): objectId: " + parseObject.getObjectId());
				System.out.println("saveInBackground(): createdAt: " + parseObject.getCreatedAt());
				System.out.println("saveInBackground(): updatedAt: " + parseObject.getUpdatedAt());
				assertNotNull("objectId should not be null", parseObject.getObjectId());
				assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
				assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
			}
		});	
	
	}

	public void updateInBackground() {
		System.out.println("update(): initializing...");
		final ParseObject parseObject = getParseObject(CLASS_NAME);
		
		try {
			parseObject.save();
			System.out.println("updateInBackground(): objectId: " + parseObject.getObjectId());
			System.out.println("updateInBackground(): createdAt: " + parseObject.getCreatedAt());
			System.out.println("updateInBackground(): updatedAt: " + parseObject.getUpdatedAt());
			System.out.println("updateInBackground(): initial name: " + parseObject.getString("name"));
			System.out.println("updateInBackground(): initial int: " + parseObject.getInt("int"));
			System.out.println("updateInBackground(): initial double: " + parseObject.getDouble("double"));			
			assertNotNull("objectId should not be null", parseObject.getObjectId());
			assertNotNull("createdAt should not be null", parseObject.getCreatedAt());
			assertNotNull("updatedAt should not be null", parseObject.getUpdatedAt());
		
			parseObject.put("name", "updated");
			parseObject.increment("int");
			parseObject.decrement("double");
			parseObject.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException parseException) {
					assertNull("updateInBackground(): parseException should not be null", parseException);
					System.out.println("updateInBackground(): name: " + parseObject.getString("name"));
					System.out.println("updateInBackground(): incrementedInt: " + parseObject.getInt("int"));
					System.out.println("updateInBackground(): incrementedInt: " + parseObject.getDouble("double"));
					System.out.println("updateInBackground(): createdAt: " + parseObject.getCreatedAt());
					System.out.println("updateInBackground(): updatedAt: " + parseObject.getUpdatedAt());	
					
				}
			});			
			
		}
		catch(ParseException pe) {
			assertNull("update(): should not have thrown ParseException", pe);
		}
	}		
	
	public void deleteInBackground() {
		System.out.println("deleteInBackground(): initializing...");
		final ParseObject parseObject = getParseObject(CLASS_NAME);
		
		try {
			System.out.println("deleteInBackground(): saving parseObject first...");
			parseObject.save();
			String objectId = parseObject.getObjectId();
			Date createdAt = parseObject.getCreatedAt();
			Date updatedAt = parseObject.getUpdatedAt();
			System.out.println("deleteInBackground(): objectId: " + objectId);
			System.out.println("deleteInBackground(): createdAt: " + createdAt);
			System.out.println("deleteInBackground(): updatedAt: " + updatedAt);
			assertNotNull("objectId should not be null", objectId);
			assertNotNull("createdAt should not be null", createdAt);
			assertNotNull("updatedAt should not be null", updatedAt);
			System.out.println("deleteInBackground(): deleting parseObject with objectId " + objectId);
		}
		catch(ParseException pe) {
			assertNull("deleteInBackground(): should not have thrown ParseException", pe);
		}
		
		parseObject.deleteInBackground(new DeleteCallback() {
			
			@Override
			public void done(ParseException parseException) {
				String objectId = parseObject.getObjectId();
				Date createdAt = parseObject.getCreatedAt();
				Date updatedAt = parseObject.getUpdatedAt();
				assertNull("deleteInBackground(): parseObject deleted, objectId should be null", objectId);
				assertNull("deleteInBackground(): parseObject deleted, createdAt should not be null", createdAt);
				assertNull("deleteInBackground(): parseObject deleted, updatedAt should not be null", updatedAt);
				
			}
		});
		
	}	
	
}
