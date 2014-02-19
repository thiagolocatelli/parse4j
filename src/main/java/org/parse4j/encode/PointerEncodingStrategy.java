package org.parse4j.encode;

import org.json.JSONObject;
import org.parse4j.ParseObject;

public class PointerEncodingStrategy extends PointerOrLocalIdEncodingStrategy {

	private static final PointerEncodingStrategy instance = new PointerEncodingStrategy();

	public static PointerEncodingStrategy get() {
		return instance;
	}

	public JSONObject encodeRelatedObject(ParseObject object) {
		if (object.getObjectId() == null) {
			throw new IllegalStateException(
					"unable to encode an association with an unsaved ParseObject");
		}
		return super.encodeRelatedObject(object);
	}

}
