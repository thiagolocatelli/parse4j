package org.parse4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.parse4j.command.ParseBatchCommand;
import org.parse4j.command.ParseCommand;
import org.parse4j.command.ParseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Main class used for sending batch requests
 * @author Nikolay Rusev
 *
 */
public class ParseBatch {
	private static final String path = "/" + ParseConstants.API_VERSION + "/"+ "classes" + "/";
	private JSONArray data = new JSONArray();

	private static Logger LOGGER = LoggerFactory.getLogger(ParseBatch.class);

	public void deleteObject(ParseObject obj) {
		if (obj.getObjectId() == null)
			throw new IllegalArgumentException("for delete operation your object should provide objectId");
		JSONObject inner = new JSONObject();
		inner.put("path", path + obj.getClassName() + "/" + obj.getObjectId());
		inner.put("method", "DELETE");
		inner.put("body", obj.getParseData());
		data.put(inner);
	}

	public void createObject(ParseObject obj) {
		JSONObject inner = new JSONObject();
		inner.put("path", path + obj.getClassName());
		inner.put("method", "POST");
		inner.put("body", obj.getParseData());
		data.put(inner);
	}
	
	public void updateObject(ParseObject obj) {
		if (obj.getObjectId() == null)
			throw new IllegalArgumentException("for update operation your object should provide objectId");
		JSONObject inner = new JSONObject();
		inner.put("path", path + obj.getClassName() + "/" + obj.getObjectId());
		inner.put("method", "PUT");
		inner.put("body", obj.getParseData());
		data.put(inner);
	}
	/**
	 * from Parse.com->
	 * The response from batch will be a list with the same number of elements as the input list.
	 * Each item in the list with be a dictionary with either the success or error field set. 
	 * @return array of json objects corresponding to every passed object if it is successfully created
	 * @throws ParseException
	 */
	public JSONArray batch() throws ParseException {
		ParseCommand command = new ParseBatchCommand();
		command.put("requests", data);
		ParseResponse response = command.perform();
		if (!response.isFailed()) {
			Object json = new JSONTokener(response.getRawResponseBody()).nextValue();
			if (json instanceof JSONArray) {
				JSONArray jsonResponse = (JSONArray) json;
				LOGGER.info("response is " + jsonResponse);
				return jsonResponse;
			}
			//if the response is not array there is an error
			LOGGER.error("Error response");
			throw response.getException();
		} else {
			LOGGER.error("Error while sending batch request");
			throw response.getException();
		}
	}

}
