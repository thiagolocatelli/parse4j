package org.parse4j.command;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.parse4j.Parse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsePostCommand extends ParseCommand {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParsePostCommand.class);

	private String endPoint;
	private String objectId;

	public ParsePostCommand(String endPoint, String objectId) {
		this.endPoint = endPoint;
		this.objectId = objectId;
	}

	public ParsePostCommand(String endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public HttpRequestBase getRequest() throws IOException {
		
		HttpPost httppost = new HttpPost(getUrl());
		setupHeaders(httppost, addJson);

		if (data.has("data")) {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Sending data: {}", data.getJSONObject("data"));
			}
			httppost.setEntity(new StringEntity(data.getJSONObject("data").toString(), "UTF8"));
		}
		return httppost;
	}
	
	protected String getUrl() {
		String url = Parse.getParseAPIUrl(endPoint) + (objectId != null ? "/" + objectId : "");
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Request URL: {}", url);
		}
		
		return url;
	}

}
