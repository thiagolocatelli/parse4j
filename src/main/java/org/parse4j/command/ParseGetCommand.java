package org.parse4j.command;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
		
		String url = Parse.getParseAPIUrl(className) + (objectId != null ? "/" + objectId : "");
		
		if("login".endsWith(className)) {
			try {
				String userPart = "username=" + URLEncoder.encode(data.getString("username"), "UTF-8");
				String passPart = "password=" + URLEncoder.encode(data.getString("password"), "UTF-8");
				url += "?" + userPart + "&" + passPart;
				System.out.println(url);
			}
			catch(UnsupportedEncodingException e) {
				
			}
		}
		
		HttpGet httpget = new HttpGet(url);
		setupHeaders(httpget, addJson);
		return httpget;
	}
	
	public void addJson(boolean addJson) {
		this.addJson = false;
	}

}
