package org.parse4j;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;
import org.parse4j.operation.ParseFieldOperation;

public class ParseDecoderTestCase {

	@Test
	public void decode() throws ParseException {
		JSONObject jsonObject = new JSONObject(
				"{\"createdAt\":\"2015-12-28T06:18:45.493Z\",\"__type\":\"Object\",\"name\":\"test object\",\"nested_object\":{  \"createdAt\":\"2015-12-28T04:24:57.680Z\",\"__type\":\"Object\",\"name\":\"nested object\",\"className\":\"NestedTestClass\",\"objectId\":\"DdDjWiUT8y\",\"updatedAt\":\"2015-12-28T04:42:38.980Z\"},\"className\":\"TestClass\",\"objectId\":\"Ts7L6Bhv00\",\"updatedAt\":\"2015-12-30T11:31:51.512Z\"}");
		ParseObject decodedObject = (ParseObject) ParseDecoder.decode(jsonObject);
		assertFalse(decodedObject.isDirty);
		assertNotNull(decodedObject.getCreatedAt());
		assertNotNull(decodedObject.getUpdatedAt());
		assertEquals("TestClass", decodedObject.getClassName());
		assertEquals("test object", decodedObject.getString("name"));
		assertEquals("Object", decodedObject.getString("__type"));
		assertEquals("Ts7L6Bhv00", decodedObject.getObjectId());
		List<?> dirtyKeys = (List<?>) getPrivateField(decodedObject, "dirtyKeys");
		@SuppressWarnings("unchecked")
		Map<String, ParseFieldOperation> operations = (Map<String, ParseFieldOperation>) getPrivateField(decodedObject, "operations");
		assertTrue(dirtyKeys.size() == 0);
		assertTrue(operations.size() == 0);
		ParseObject nestedObject = decodedObject.getParseObject("nested_object");
		assertFalse(nestedObject.isDirty);
		assertNotNull(nestedObject.getCreatedAt());
		assertNotNull(nestedObject.getUpdatedAt());
		assertEquals("NestedTestClass", nestedObject.getClassName());
		assertEquals("nested object", nestedObject.getString("name"));
		assertEquals("Object", nestedObject.getString("__type"));
		assertEquals("DdDjWiUT8y", nestedObject.getObjectId());
		List<?> dirtyKeys2 = (List<?>) getPrivateField(nestedObject, "dirtyKeys");
		@SuppressWarnings("unchecked")
		Map<String, ParseFieldOperation> operations2 = (Map<String, ParseFieldOperation>) getPrivateField(nestedObject, "operations");
		assertTrue(dirtyKeys2.size() == 0);
		assertTrue(operations2.size() == 0);
	}

	private Object getPrivateField(Object obj, String fieldName) {
		try {
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			Object value = field.get(obj);
			field.setAccessible(false);
			return value;
		} catch (Exception e) {
			return null;
		} 
	}

}
