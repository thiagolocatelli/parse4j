package org.parse4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parse4j.operation.ParseRelationOperation;
import org.parse4j.util.ParseDecoder;

public class ParseRelation<T extends ParseObject> {

	private ParseObject parent;
	private String key;
	private String targetClass;
	private Set<ParseObject> knownObjects = new HashSet<ParseObject>();	
	
	
	public ParseRelation(JSONObject jsonObject) {
		this.parent = null;
		this.key = null;
		this.targetClass = jsonObject.optString("className", null);
		JSONArray objectsArray = jsonObject.optJSONArray("objects");
		if (objectsArray != null) {
			for (int i = 0; i < objectsArray.length(); i++) {
				this.knownObjects.add((ParseObject) ParseDecoder.decode(objectsArray
						.optJSONObject(i)));
			}
		}
	}

	public ParseRelation(String targetClass) {
		this.parent = null;
		this.key = null;
		this.targetClass = targetClass;
	}

	public ParseRelation(ParseObject parent, String key) {
		this.parent = parent;
		this.key = key;
		this.targetClass = null;
	}

	public String getTargetClass() {
		return this.targetClass;
	}

	public void setTargetClass(String className) {
		this.targetClass = className;
	}
	
	void ensureParentAndKey(ParseObject someParent, String someKey) {

		if (this.parent == null) {
			this.parent = someParent;
		}
		
		if (this.key == null) {
			this.key = someKey;
		}
		
		if (this.parent != someParent) {
			throw new IllegalStateException(
					"Internal error. One ParseRelation retrieved from two different ParseObjects.");
		}

		if (!this.key.equals(someKey)) {
			throw new IllegalStateException(
					"Internal error. One ParseRelation retrieved from two different keys.");
		}

	}

	public void add(T object) {

		ParseRelationOperation<T> operation = new ParseRelationOperation<T>(
				Collections.singleton(object), null);

		this.targetClass = operation.getTargetClass();
		this.parent.performOperation(this.key, operation);
		this.knownObjects.add(object);

	}

	public void remove(T object) {

		ParseRelationOperation<T> operation = new ParseRelationOperation<T>(null,
				Collections.singleton(object));

		this.targetClass = operation.getTargetClass();
		this.parent.performOperation(this.key, operation);
		this.knownObjects.remove(object);
	}
	
	public ParseQuery<T> getQuery() {

		ParseQuery<T> query;
		if (this.targetClass == null) {
			query = ParseQuery.getQuery(this.parent.getClassName());
			query.redirectClassNameForKey(this.key);
		} else {
			query = ParseQuery.getQuery(this.targetClass);
		}
		//query.whereRelatedTo(this.parent, this.key);
		return query;

	}	
	
}
