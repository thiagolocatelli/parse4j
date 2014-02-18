package org.parse4j.operation;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.Parse;
import org.parse4j.ParseFile;
import org.parse4j.ParseGeoPoint;
import org.parse4j.ParseObject;

public class SetFieldOperation implements ParseFieldOperation {

	private Object value;
	
	public SetFieldOperation(Object value) {
		this.value = value;
	}
	
	@Override
	public Object apply(Object oldValue, ParseObject parseObject, String key) {
		return value;
	}

	@Override
	public Object encode() throws JSONException {
		
		if(value instanceof Date) {
			Date dt = (Date) value;
			//return Parse.encodeDate(dt);
			JSONObject output = new JSONObject();
			output.put("__type", "Date");
			output.put("iso", Parse.encodeDate(dt));
			return output;			
		}
		
		if(value instanceof ParseFile) {
			ParseFile file = (ParseFile) value;
			JSONObject output = new JSONObject();
			output.put("__type", "File");
			output.put("name", file.getName());
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
		
		return value;
	}

}
