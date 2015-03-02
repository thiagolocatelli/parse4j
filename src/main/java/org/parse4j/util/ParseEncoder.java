package org.parse4j.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parse4j.Parse;
import org.parse4j.ParseFile;
import org.parse4j.ParseGeoPoint;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.ParseRelation;
import org.parse4j.encode.ParseObjectEncodingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseEncoder {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseEncoder.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object encode(Object value, ParseObjectEncodingStrategy objectEncoder) {
		
		if (value instanceof ParseObject) {
			return objectEncoder.encodeRelatedObject((ParseObject) value);
		}

		if (value instanceof byte[]) {
			byte[] bytes = (byte[]) value;
			JSONObject output = new JSONObject();
			output.put("__type", "Bytes");
			output.put("base64", Base64.encodeBase64String(bytes));
			return output;
		}
		
		if (value instanceof Date) {
			Date dt = (Date) value;
			JSONObject output = new JSONObject();
			output.put("__type", "Date");
			output.put("iso", Parse.encodeDate(dt));
			return output;
		}

		if (value instanceof List) {
			JSONArray array = new JSONArray();
			List list = (List) value;
			Iterator i = list.iterator();
			while (i.hasNext()) {
				array.put(encode(i.next(), objectEncoder));
			}
			return array;
		}
		
		if (value instanceof JSONObject) {
			JSONObject map = (JSONObject) value;
			JSONObject json = new JSONObject();
			Iterator keys = map.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				json.put(key, encode(map.opt(key), objectEncoder));
			}
			return json;
		}

		if (value instanceof JSONArray) {
			JSONArray array = (JSONArray) value;
			JSONArray json = new JSONArray();
			for (int i = 0; i < array.length(); i++) {
				json.put(encode(array.opt(i), objectEncoder));
			}
			return json;
		}
		
		if (value instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) value;
			JSONObject json = new JSONObject();
			for (String key : map.keySet()) {
				json.put(key, encode(map.get(key), objectEncoder));
			}
			return json;
		}	
		
		if ((value instanceof ParseRelation)) {
			ParseRelation relation = (ParseRelation) value;
			return relation.encodeToJSON(objectEncoder);
		}
		
		if ((value instanceof ParseQuery.RelationConstraint)) {
	        return ((ParseQuery.RelationConstraint) value).encode(objectEncoder);
		}
		
		if(value instanceof ParseFile) {
			ParseFile file = (ParseFile) value;
			JSONObject output = new JSONObject();
			output.put("__type", "File");
			output.put("name", file.getName());
			output.put("url", file.getUrl());
			return output;	
		}
		
		if(value instanceof ParseGeoPoint) {
			ParseGeoPoint gp = (ParseGeoPoint) value;
			JSONObject output = new JSONObject();
			output.put("__type", "GeoPoint");
			output.put("latitude", gp.getLatitude());
			output.put("longitude", gp.getLongitude());
			return output;	
		}
		
		if(value instanceof ParseObject) {
			ParseObject po = (ParseObject) value;
			JSONObject output = new JSONObject();
			output.put("__type", "Pointer");
			output.put("className", po.getClassName());
			output.put("objectId", po.getObjectId());
			return output;	
		}
		
		if(value instanceof ParseQuery){
			return ((ParseQuery) value).toREST();
		}
		
		if(Parse.isValidType(value)) {
			return value;	
		}
		
		LOGGER.error("Object type not decoded: " + value.getClass().getCanonicalName());
		throw new IllegalArgumentException("Invalid type for ParseObject: " + value.getClass().toString());		
		
	}
	
}
