package org.parse4j.encode;

import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseObject;

public class PointerOrLocalIdEncodingStrategy implements
		ParseObjectEncodingStrategy {

	@Override
	public JSONObject encodeRelatedObject(ParseObject parseObject) {
		JSONObject json = new JSONObject();
	    try {
	      if (parseObject.getObjectId() != null) {
	        json.put("__type", "Pointer");
	        json.put("className", parseObject.getClassName());
	        json.put("objectId", parseObject.getObjectId());
	      } else {
	        json.put("__type", "Pointer");
	        json.put("className", parseObject.getClassName());
	        json.put("localId", createTempId());
	      }
	    }
	    catch (JSONException e) {
	      throw new RuntimeException(e);
	    }
	    return json;
	}
	
	private String createTempId() {
		Random random = new Random();
		long localIdNumber = random.nextLong();
		String localId = "local_" + Long.toHexString(localIdNumber);

		if (!isLocalId(localId)) {
			throw new IllegalStateException(
					"Generated an invalid local id: \""
							+ localId
							+ "\". "
							+ "This should never happen. Contact us at https://parse.com/help");
		}

		return localId;
	}

	private boolean isLocalId(String localId) {
		if (!localId.startsWith("local_")) {
			return false;
		}
		for (int i = 6; i < localId.length(); i++) {
			char c = localId.charAt(i);
			if (((c < '0') || (c > '9')) && ((c < 'a') || (c > 'f'))) {
				return false;
			}
		}
		return true;
	}

}
