package org.parse4j.command;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseGetCommand extends ParseCommand {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseGetCommand.class);

	private String endPoint;
	private String objectId;

	public ParseGetCommand(String className, String objectId) {
		this.endPoint = className;
		this.objectId = objectId;
	}

	public ParseGetCommand(String endPoint) {
		this.endPoint = endPoint;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public HttpRequestBase getRequest() {
		
		String url = getUrl();
		
		if("login".endsWith(endPoint)) {
			try {
				String userPart = "username=" + URLEncoder.encode(data.getString("username"), "UTF-8");
				String passPart = "password=" + URLEncoder.encode(data.getString("password"), "UTF-8");
				url += "?" + userPart + "&" + passPart;
				LOGGER.info(url);
			}
			catch(UnsupportedEncodingException e) {
				LOGGER.error("Error while building request url", e);
			}
		}
		
		try {
			if(data.opt("data") != null) {
				JSONObject query = (JSONObject) data.get("data");
				try {
					
					Iterator it = query.keySet().iterator();
					if(it.hasNext()) {
						url += "?";
					}
					while(it.hasNext()) {
						String key = (String) it.next();
						Object obj = query.get(key);
						url += key + "=" + URLEncoder.encode(obj.toString(), "UTF-8") + "&";
					}
					
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("Final Request URL: {}", url);
					}
				}
				catch(UnsupportedEncodingException e) {
					LOGGER.error("Encoding error while building request url", e);
				}
			}
			
			
		}
		catch(JSONException e) {
			LOGGER.error("Data not found, empty request?", e);
		}
		
		HttpGet httpget = new HttpGet(url);
		setupHeaders(httpget, addJson);
		return httpget;
	}
	
	public void addJson(boolean addJson) {
		this.addJson = false;
	}
	
	protected String getUrl() {
		String url = Parse.getParseAPIUrl(endPoint) + (objectId != null ? "/" + objectId : "");
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request URL: {}", url);
		}
		
		return url;
	}

}
