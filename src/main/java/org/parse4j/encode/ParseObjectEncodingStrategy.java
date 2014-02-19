package org.parse4j.encode;

import org.json.JSONObject;
import org.parse4j.ParseObject;

public interface ParseObjectEncodingStrategy {
	
	public abstract JSONObject encodeRelatedObject(ParseObject parseObject);

}
