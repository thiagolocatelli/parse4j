package org.parse4j.operation;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseObject;

public class DeleteFieldOperation implements ParseFieldOperation {

	@Override
	public JSONObject apply(Object oldValue, ParseObject paramParseObject, String key) {
		JSONObject output = new JSONObject();
	    output.put("__op", "Delete");
	    return output;
	}

	@Override
	public JSONObject encode() throws JSONException {
		JSONObject output = new JSONObject();
	    output.put("__op", "Delete");
	    return output;
	}

}
