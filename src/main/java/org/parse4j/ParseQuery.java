package org.parse4j;

import java.util.List;

import org.parse4j.callback.CountCallback;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.GetCallback;

public class ParseQuery<T extends ParseObject> {

	public T get(String theObjectId) throws ParseException {
		return null;
	}

	public void getInBackground(String objectId, GetCallback<T> callback) {

	}

	public List<T> find() throws ParseException {
		return null;
	}
	
	public void findInBackground(FindCallback<T> callback) {
		
	}
	
	public int count() throws ParseException {
		return 0;
	}
	
	public void countInBackground(CountCallback countCallback) {
		
	}	

}
