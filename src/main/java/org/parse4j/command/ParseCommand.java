package org.parse4j.command;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parse4j.Parse;
import org.parse4j.ParseConstants;
import org.parse4j.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ParseCommand {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseCommand.class);

	private static RequestConfig config;
	protected JSONObject data = new JSONObject();
	protected boolean addJson = true;

	static {
		config = RequestConfig.custom().build();
	}	
	
	abstract HttpRequestBase getRequest() throws IOException;

	public ParseResponse perform() throws ParseException {

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Data to be sent: {}", data);
		}		
		
		try {
			long commandStart = System.currentTimeMillis();
			HttpClient httpclient = createSingleClient();
			//ResponseHandler<String> responseHandler=new BasicResponseHandler();
			HttpResponse httpResponse = httpclient.execute(getRequest());
			//String resp = httpclient.execute(getRequest(), responseHandler);
			ParseResponse response = new ParseResponse(httpResponse);
			
			long commandReceived = System.currentTimeMillis();
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("ParseCommand took " + (commandReceived - commandStart) + " milliseconds\n");
			}
			return response;
		}
		catch (ClientProtocolException e) {
			throw ParseResponse.getConnectionFailedException(e.getMessage());
		} 
		catch (IOException e) {
			throw ParseResponse.getConnectionFailedException(e.getMessage());
		}
		
	}
	
	protected HttpClient createSingleClient() {
		HttpClientBuilder client = HttpClients.custom().setDefaultRequestConfig(config);
		
		return client.build();
	}
	
	protected void setupHeaders(HttpRequestBase requestBase, boolean addJson) {
		requestBase.addHeader(ParseConstants.HEADER_APPLICATION_ID, Parse.getApplicationId());
		requestBase.addHeader(ParseConstants.HEADER_REST_API_KEY, Parse.getRestAPIKey());
		if(addJson) {
			requestBase.addHeader(ParseConstants.HEADER_CONTENT_TYPE, ParseConstants.CONTENT_TYPE_JSON);
		}
		
		if(data.has("sessionToken")) {
			requestBase.addHeader(ParseConstants.HEADER_SESSION_TOKEN, data.getString("session_token"));
		}
		
	}

	public void setData(JSONObject data) {
		this.data.put("data", data);
	}
	
	public void put(String key, String value) {
		this.data.put(key, value);
	}
	
	public void put(String key, int value) {
		this.data.put(key, value);
	}
	
	public void put(String key, long value) {
		this.data.put(key, value);
	}
	
	public void put(String key, JSONObject value) {
		this.data.put(key, value);
	}
	
	public void put(String key, JSONArray value) {
		this.data.put(key, value);
	}	
	
}
