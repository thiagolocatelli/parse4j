package org.parse4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.parse4j.util.MimeType;

public class ParseObjectOperationsTestCase extends Parse4JTestCase {

	@Test(expected = IllegalArgumentException.class )
	public void putNullKey() {
		System.out.println("putNullKey(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put(null, "parse developer");
	}
	
	@Test(expected = IllegalArgumentException.class )
	public void putNullValue() {
		System.out.println("putNullValue(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("name", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void incrementString() {
		System.out.println("incrementString(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("field", "value");
		parseObject.increment("field");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void decrementString() {
		System.out.println("decrementString(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("field", "value");
		parseObject.decrement("field");
	}
	
	@Test
	public void incrementNumbers() {
		System.out.println("incrementNumbers(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("field1", 1);
		parseObject.increment("field1");
		parseObject.put("field2", 2L);
		parseObject.increment("field2");
		parseObject.put("field3", 3.3);
		parseObject.increment("field3");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidPutKey1() {
		System.out.println("invalidPutKey1(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("objectId", "value");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidPutKey2() {
		System.out.println("invalidPutKey2(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("createdAt", "value");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void invalidPutKey3() {
		System.out.println("invalidPutKey3(): initializing...");
		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
		parseObject.put("updatedAt", "value");
	}
	
	@Test
	public void extension() {
		System.out.println("extension(): initializing...");		
		
		for(String extension : MimeType.mimeTypes.keySet()) {
			String fileName = "test." + extension;
			//System.out.println("File name:" + fileName);
			assertEquals("Expected " + MimeType.getFileExtension(fileName), MimeType.getFileExtension(fileName), extension);
		}
		
		String fileName = "test";
		//System.out.println("File name:" + fileName);
		assertEquals("Expected " + MimeType.getFileExtension(fileName), MimeType.getFileExtension(fileName), "");
	}
	
	@Test
	public void extensionNotEqual() {	
		System.out.println("extensionNotEqual(): initializing...");	
		
		for(String extension : MimeType.mimeTypes.keySet()) {
			String fileName = "test." + extension;
			//System.out.println("File name:" + fileName + ", testing against: " + (extension+"x"));
			boolean result = (extension+"x").equals(MimeType.getFileExtension(fileName));
			assertFalse(result);
		}
	}
	
	@Test
	public void mimeType() {
		System.out.println("mimeType(): initializing...");
		
		for(String extension : MimeType.mimeTypes.keySet()) {
			String fileName = "test." + extension;
			//System.out.print("File name:" + fileName);
			String mime = MimeType.getMimeType(MimeType.getFileExtension(fileName));
			//System.out.println(", content-type: " + mime);
			assertEquals("Expected " + MimeType.getMimeType(extension), MimeType.getMimeType(extension), mime);
		}
		
		String fileName = "test";
		//System.out.print("File name:" + fileName);
		String mime = MimeType.getMimeType(MimeType.getFileExtension(fileName));
		String extension = MimeType.getFileExtension(fileName);
		//System.out.println(", content-type: " + mime);
		assertEquals("Expected " + MimeType.getMimeType(extension), MimeType.getMimeType(extension), mime);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testFileNotSave() {
		System.out.println("testFileNotSave(): initializing...");
		try {
			byte[] data = getBytes("/parse.png");
			ParseFile file = new ParseFile("parse.png", data);
			ParseObject po = getParseObject(CLASS_NAME);
			po.put("logo", file);
		}
		catch(ParseException pe) {
			
		}
	}
	
}
