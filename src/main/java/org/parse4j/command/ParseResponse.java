package org.parse4j.command;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseException;

public class ParseResponse {

	private HttpResponse httpResponse;
	static final String RESPONSE_CODE_JSON_KEY = "code";
	static final String RESPONSE_ERROR_JSON_KEY = "error";

	public ParseResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public boolean isFailed() {
		return hasConnectionFailed() || hasErrorCode();
	}

	public boolean hasConnectionFailed() {
		return httpResponse.getEntity() == null;
	}

	public boolean hasErrorCode() {
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		return (statusCode < 200 || statusCode >= 300);
	}

	static ParseException getConnectionFailedException(String message) {
		return new ParseException(ParseException.CONNECTION_FAILED,
				"Connection failed with Parse servers.  Log: " + message);
	}

	static ParseException getConnectionFailedException(Throwable e) {
		return getConnectionFailedException(e.getMessage());
	}
	
	public ParseException getException() {

		if (hasConnectionFailed()) {
			return new ParseException(ParseException.CONNECTION_FAILED,
					"Connection to Parse servers failed.");
		}

		if (!hasErrorCode()) {
			return new ParseException(ParseException.OPERATION_FORBIDDEN,
					"getException called with successful response");
		}

		JSONObject response = getJsonObject();

		if (response == null) {
			return new ParseException(ParseException.INVALID_JSON,
					"Invalid response from Parse servers.");
		}
		
		return getParseError(response);
	}
	
	public JSONObject getJsonObject() {
		try {
			return new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		} catch (org.apache.http.ParseException e) {
			return null;
		} catch (JSONException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}	
	
	private ParseException getParseError(JSONObject response) {
		
		int code;
		String error;
		
		try {
			code = response.getInt(RESPONSE_CODE_JSON_KEY);
		}
		catch(JSONException e) {
			code = ParseException.NOT_INITIALIZED;
		}
		
		try {
			error = response.getString(RESPONSE_ERROR_JSON_KEY);
		}
		catch(JSONException e) {
			error = "Error undefinted by Parse server.";
		}
		
		return new ParseException(code, error);
	}

}
