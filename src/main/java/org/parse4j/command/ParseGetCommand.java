package org.parse4j.command;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.parse4j.Parse;

public class ParseGetCommand extends ParseCommand {

	private String className;
	private String objectId;

	public ParseGetCommand(String className, String objectId) {
		this.className = className;
		this.objectId = objectId;
	}

	public ParseGetCommand(String className) {
		this.className = className;
	}

	@Override
	public HttpRequestBase getRequest() {
		HttpGet httpget = new HttpGet(Parse.getParseAPIUrl(className)
				+ (objectId != null ? "/" + objectId : ""));
		setupHeaders(httpget, true);
		return httpget;
	}

}
