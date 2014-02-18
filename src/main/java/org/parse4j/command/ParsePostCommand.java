package org.parse4j.command;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.parse4j.Parse;

public class ParsePostCommand extends ParseCommand {

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
		String url = Parse.getParseAPIUrl(endPoint)
				+ (objectId != null ? "/" + objectId : "");
		HttpPost httppost = new HttpPost(url);
		setupHeaders(httppost, true);

		if (data != null) {
			httppost.setEntity(new StringEntity(data.toString()));
		}
		return httppost;
	}

}
