package org.parse4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Test;
import org.parse4j.callback.FunctionCallback;

public class ParseCloudTestCase extends Parse4JTestCase {

	/*
		Parse.Cloud.define("helloWorld", function(request, response) {
		  response.success("Hello, " + request.params.name + "!!!");
		});
	
		Parse.Cloud.define("Multiply", function(request, response) {
		  response.success(request.params.A * request.params.B);
		});
	
		Parse.Cloud.define("ForcedError", function(request, response) {
		  response.error("forced error");
		});
	*/
	
	@Test
	public void testInvalidFunction() {
		System.out.println("InvalidFunction(): initializing...");
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", "Parse");
			String result = ParseCloud.callFunction("InvalidFunction", params);
			assertEquals("Hello, Parse!!!", result);
		}
		catch(ParseException pe) {
			assertEquals(ParseException.CLOUD_ERROR, pe.getCode());
		}
	}	
	
	@Test
	public void testForcedError() {
		System.out.println("testForcedError(): initializing...");
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", "Parse");
			String result = ParseCloud.callFunction("ForcedError", params);
			assertEquals("Hello, Parse!!!", result);
		}
		catch(ParseException pe) {
			assertEquals(ParseException.CLOUD_ERROR, pe.getCode());
		}
	}	
	
	@Test
	public void testHelloWorld() {
		System.out.println("testHelloWorld(): initializing...");
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("name", "Parse");
			String result = ParseCloud.callFunction("helloWorld", params);
			assertEquals("Hello, Parse!!!", result);
		}
		catch(ParseException pe) {
			assertNull("testHelloWorld(): should not have thrown ParseException", pe);
		}
	}
	
	@Test
	public void testMultiply() {
		System.out.println("testMultiply(): initializing...");	
		try {
			HashMap<String, Integer> params = new HashMap<String, Integer>();
			params.put("A", 12);
			params.put("B", 4);
			Integer result = ParseCloud.callFunction("Multiply", params);
			assertEquals("48", result.toString());
		}
		catch(ParseException pe) {
			assertNull("testMultiply(): should not have thrown ParseException", pe);
		}		
		
	}
	
	@Test
	public void testCallInBackground() {
		System.out.println("testCallInBackground(): initializing...");	
		
		HashMap<String, Integer> params = new HashMap<String, Integer>();
		params.put("A", 12);
		params.put("B", 4);
		ParseCloud.callFunctionInBackground("Multiply", params, new FunctionCallback<Integer>() {

			@Override
			public void done(Integer result, ParseException parseException) {
				assertEquals("48", result.toString());
				
			}
			
		});	
		sleep(2000);
	}
	
}
