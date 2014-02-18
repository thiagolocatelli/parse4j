package org.parse4j.command;

import java.io.IOException;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.parse4j.Parse;

public class ParsePutCommand extends ParseCommand {
	
	private String endPoint;
	private String objectId;

	public ParsePutCommand(String endPoint, String objectId) {
		this.endPoint = endPoint;
		this.objectId = objectId;
	}
	
	public ParsePutCommand(String endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public HttpRequestBase getRequest() throws IOException {
		String url = Parse.getParseAPIUrl(endPoint)
				+ (objectId != null ? "/" + objectId : "");
		HttpPut httpput = new HttpPut(url);
		setupHeaders(httpput, true);
		
		if (data != null) {
			httpput.setEntity(new StringEntity(data.toString()));
		}		
		return httpput;
	}

}
