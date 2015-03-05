package org.parse4j.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseResponse {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseResponse.class);

	private HttpResponse httpResponse;
	static final String RESPONSE_CODE_JSON_KEY = "code";
	static final String RESPONSE_ERROR_JSON_KEY = "error";
	private String responseBody;
	private String headers;
	private int contentLength;

	public ParseResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
		this.responseBody = getResponseAsString(httpResponse);
		this.contentLength = responseBody.length();
		this.headers = httpResponse.toString();
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Response Headers: " + headers);
			LOGGER.debug("Response Content Length: " + contentLength);
			LOGGER.debug("Response Content: " + responseBody);	
		}
	}

	public boolean isFailed() {
		return hasConnectionFailed() || hasErrorCode();
	}

	public boolean hasConnectionFailed() {
		return responseBody == null;
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
		
		return new JSONObject(responseBody);
		/*
		try {
			
			JSONObject json = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
			
			
			return json;
		} catch (org.apache.http.ParseException e) {
			return null;
		} catch (JSONException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		*/
	}
	public String getRawResponseBody(){
		return responseBody;
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
	
	private String getResponseAsString(HttpResponse httpResponse) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			StringBuilder total = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
			   total.append(line);
			}
			//r.close();
			return total.toString();
		}
		catch(IOException e) {
			LOGGER.error("Error while reading response entity", e);
			throw new IllegalArgumentException("Failed getting Parse Response", e);
		}
	}

}
