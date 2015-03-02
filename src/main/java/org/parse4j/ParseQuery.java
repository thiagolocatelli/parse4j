package org.parse4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.callback.CountCallback;
import org.parse4j.callback.FindCallback;
import org.parse4j.callback.GetCallback;
import org.parse4j.command.ParseGetCommand;
import org.parse4j.command.ParseResponse;
import org.parse4j.encode.ParseObjectEncodingStrategy;
import org.parse4j.encode.PointerEncodingStrategy;
import org.parse4j.util.ParseEncoder;
import org.parse4j.util.ParseRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseQuery<T extends ParseObject> {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseQuery.class);

	private String className;
	private QueryConstraints where;
	private ArrayList<String> include;
	private ArrayList<String> selectedKeys;
	private HashMap<String, Object> extraOptions = null;
	private int limit;
	private int skip;
	private String order;
	
	private boolean trace;
	private boolean caseSensitive = true;
	private String strTrace;

	public ParseQuery(Class<T> subclass) {
		this(ParseRegistry.getClassName(subclass));
	}

	public ParseQuery(String theClassName) {
		this.className = theClassName;
		this.limit = -1;
		this.skip = 0;
		this.where = new QueryConstraints();
		this.include = new ArrayList<String>();
		this.trace = false;
		this.extraOptions = new HashMap<String, Object>();
	}
	
	public static <T extends ParseObject> ParseQuery<T> getQuery(
			Class<T> subclass) {
		return new ParseQuery<T>(subclass);
	}

	public static <T extends ParseObject> ParseQuery<T> getQuery(
			String className) {
		return new ParseQuery<T>(className);
	}
	
	private void addCondition(String key, String condition, Object value) {
		KeyConstraints whereValue = null;

		if (this.where.containsKey(key)) {
			Object existingValue = this.where.get(key);
			if ((existingValue instanceof KeyConstraints)) {
				whereValue = (KeyConstraints) existingValue;
			}
		}
		if (whereValue == null) {
			whereValue = new KeyConstraints();
		}

		whereValue.put(condition, value);
		this.where.put(key, whereValue);
	}
	
	
	public ParseQuery<T> whereEqualTo(String key, Object value) {
		this.where.put(key, value);
		return this;
	}
	
	public ParseQuery<T> whereLessThan(String key, Object value) {
		addCondition(key, "$lt", value);
		return this;
	}

	public ParseQuery<T> whereNotEqualTo(String key, Object value) {
		addCondition(key, "$ne", value);
		return this;
	}

	public ParseQuery<T> whereGreaterThan(String key, Object value) {
		addCondition(key, "$gt", value);
		return this;
	}

	public ParseQuery<T> whereLessThanOrEqualTo(String key, Object value) {
		addCondition(key, "$lte", value);
		return this;
	}

	public ParseQuery<T> whereGreaterThanOrEqualTo(String key, Object value) {
		addCondition(key, "$gte", value);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParseQuery<T> whereContainedIn(String key, Collection<? extends Object> values) {
		addCondition(key, "$in", new ArrayList(values));
		return this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParseQuery<T> whereContainsAll(String key, Collection<?> values) {
		addCondition(key, "$all", new ArrayList(values));
		return this;
	}

	public ParseQuery<T> whereMatchesQuery(String key, ParseQuery<?> query) {
		addCondition(key, "$inQuery", query);
		return this;
	}

	public ParseQuery<T> whereDoesNotMatchQuery(String key, ParseQuery<?> query) {
		addCondition(key, "$notInQuery", query);
		return this;
	}

	public ParseQuery<T> whereMatchesKeyInQuery(String key, String keyInQuery, ParseQuery<?> query) {
		JSONObject condition = new JSONObject();
		try {
			condition.put("key", keyInQuery);
			condition.put("query", query);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		addCondition(key, "$select", condition);
		return this;
	}

	public ParseQuery<T> whereDoesNotMatchKeyInQuery(String key, String keyInQuery, ParseQuery<?> query) {
		JSONObject condition = new JSONObject();
		try {
			condition.put("key", keyInQuery);
			condition.put("query", query);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		addCondition(key, "$dontSelect", condition);
		return this;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ParseQuery<T> whereNotContainedIn(String key, Collection<? extends Object> values) {
		addCondition(key, "$nin", new ArrayList(values));
		return this;
	}

	public ParseQuery<T> whereNear(String key, ParseGeoPoint point) { 
		addCondition(key, "$nearSphere", point);
		return this;
	}

	public ParseQuery<T> whereWithinMiles(String key, ParseGeoPoint point,double maxDistance) {
		whereWithinRadians(key, point, maxDistance / ParseGeoPoint.EARTH_MEAN_RADIUS_MILE);
		return this;
	}

	public ParseQuery<T> whereWithinKilometers(String key, ParseGeoPoint point, double maxDistance) {
		whereWithinRadians(key, point, maxDistance / ParseGeoPoint.EARTH_MEAN_RADIUS_KM);
		return this;
	}

	public ParseQuery<T> whereWithinRadians(String key, ParseGeoPoint point, double maxDistance) {
		addCondition(key, "$nearSphere", point);
		addCondition(key, "$maxDistance", Double.valueOf(maxDistance));
		return this;
	}

	public ParseQuery<T> whereWithinGeoBox(String key, ParseGeoPoint southwest, ParseGeoPoint northeast) {
		ArrayList<ParseGeoPoint> array = new ArrayList<ParseGeoPoint>();
		array.add(southwest);
		array.add(northeast);
		HashMap<String, Object> dictionary = new HashMap<String, Object>();
		dictionary.put("$box", array);
		addCondition(key, "$within", dictionary);
		return this;
	}

	public ParseQuery<T> whereMatches(String key, String regex) {
		addCondition(key, "$regex", regex);
		return this;
	}

	public ParseQuery<T> whereMatches(String key, String regex, String modifiers) {
		addCondition(key, "$regex", regex);
		if (modifiers.length() != 0) {
			addCondition(key, "$options", modifiers);
		}
		return this;
	}
	
	public ParseQuery<T> whereContains(String key, String substring) {
		whereMatches(key, new StringBuilder(caseSensitive?"":"(?i)").
				append(Pattern.quote(substring)).toString());
		return this;
	}

	public ParseQuery<T> whereStartsWith(String key, String prefix) {
		whereMatches(key, new StringBuilder("^").
				append(caseSensitive?"":"(?i)").
				append(Pattern.quote(prefix)).toString());
		return this;
	}

	public ParseQuery<T> whereEndsWith(String key, String suffix) {
		whereMatches(key, new StringBuilder(caseSensitive?"":"(?i)").
				append(Pattern.quote(suffix)).append("$").toString());
		return this;
	}
	
	public ParseQuery<T> whereExists(String key) {
		addCondition(key, "$exists", Boolean.valueOf(true));
		return this;
	}

	public ParseQuery<T> whereDoesNotExist(String key) {
		addCondition(key, "$exists", Boolean.valueOf(false));
		return this;
	}

	public ParseQuery<T> whereRelatedTo(ParseObject parent, String key) {
		this.where.put("$relatedTo", new RelationConstraint(key, parent));
		return this;
	}

	public ParseQuery<T> redirectClassNameForKey(String key) {
		this.extraOptions.put("redirectClassNameForKey", key);
		return this;
	}
	
	public void include(String key) {
		this.include.add(key);
	}	

	public ParseQuery<T> orderByAscending(String key) {
		this.order = key;
		return this;
	}

	public ParseQuery<T> addAscendingOrder(String key) {
		if (this.order == null)
			this.order = key;
		else {
			this.order = (this.order + ", " + key);
		}
		return this;
	}

	public ParseQuery<T> orderByDescending(String key) {
		this.order = ("-" + key);
		return this;
	}

	public ParseQuery<T> addDescendingOrder(String key) {
		if (this.order == null)
			this.order = ("-" + key);
		else {
			this.order = (this.order + ", -" + key);
		}
		return this;
	}
	
	public void limit(int newLimit) {
		this.limit = newLimit;
	}

	public void setTrace(boolean shouldTrace) {
		this.trace = shouldTrace;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public int getLimit() {
		return this.limit;
	}

	public void skip(int newSkip) {
		this.skip = newSkip;
	}

	public int getSkip() {
		return this.skip;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	String[] sortKeys() {
		if (this.order == null) {
			return new String[0];
		}
		return this.order.split(",");
	}

	List<String> getIncludes() {
		return Collections.unmodifiableList(this.include);
	}

	public void selectKeys(Collection<String> keys) {
		if (this.selectedKeys == null) {
			this.selectedKeys = new ArrayList<String>();
		}
		this.selectedKeys.addAll(keys);
	}
	
	
	public JSONObject toREST() {
		JSONObject params = new JSONObject();
		try {
			params.put("className", this.className);
			
			if(this.where.size() > 0) {
				params.put("where", ParseEncoder.encode(this.where, PointerEncodingStrategy.get()));
			}

			if (this.limit >= 0) {
				params.put("limit", this.limit);
			}
			
			if (this.skip > 0) {
				params.put("skip", this.skip);
			}
			
			if (this.order != null) {
				params.put("order", this.order);
			}
			
			if (!this.include.isEmpty()) {
				params.put("include", Parse.join(this.include, ","));
			}
			
			if (this.selectedKeys != null) {
				params.put("keys", Parse.join(this.selectedKeys, ","));
			}
			
			if (this.trace) {
				params.put("trace", "1");
			}

			for (String key : this.extraOptions.keySet())
				params.put(key, ParseEncoder.encode(this.extraOptions.get(key), PointerEncodingStrategy.get()));
		
		} catch (JSONException e) {
			LOGGER.error("Error parsing json", e);
			throw new RuntimeException(e);
		}

		return params;
	}
	
	
	
	public T get(String objectId) throws ParseException {
		
		whereEqualTo("objectId", objectId);
		
		List<T> results = find();
		if(results != null && results.size() > 0 ) {
			return results.get(0);
		}
		
		return null;
		
		/*
		String endPoint;
		if(!"users".equals(getClassName()) && !"roles".equals(getClassName())) {
			endPoint = "classes/" + getClassName();
		}
		else {
			endPoint = getClassName();
		}
		

		ParseGetCommand command = new ParseGetCommand(endPoint);
		JSONObject query = whereEqualTo("objectId", objectId).toREST();
		query.remove("className");
		command.setData(query);
		ParseResponse response = command.perform();
		if(!response.isFailed()) {
			if(response.getJsonObject() == null) {
				LOGGER.debug("Empty response.");
				throw response.getException();
			}
			try {
				JSONObject json = response.getJsonObject();
				JSONArray objs = json.getJSONArray("results");
				if(objs.length() == 0) {
					return null;
				}
				
				ParseObject po = new ParseObject(getClassName());
				JSONObject obj = (JSONObject) objs.get(0);
				po.setData(obj);
				return (T) po;
			}
			catch (JSONException e) {
				LOGGER.error(
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
				throw new ParseException(
						ParseException.INVALID_JSON,
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
			}
		}
		else {
			LOGGER.debug("Request failed.");
			throw response.getException();
		}
		*/

	}

	public void getInBackground(String objectId, GetCallback<T> callback) {
		GetInBackgroundThread task = new GetInBackgroundThread(objectId, callback);
		ParseExecutor.runInBackground(task);
	}
	
	class GetInBackgroundThread extends Thread {
		GetCallback<T> callback;
		String objectId;

		public GetInBackgroundThread(String objectId, GetCallback<T> callback) {
			this.callback = callback;
			this.objectId = objectId;
		}

		public void run() {
			ParseException exception = null;
			T object = null;
			try {
				object = get(objectId);
			} catch (ParseException e) {
				exception = e;
			}
			if (callback != null) {
				callback.done(object, exception);
			}
		}
	}

	/**
	 * 
	 * @return
	 * @throws ParseException
	 */
	public List<T> find() throws ParseException {
		
		return find(toREST());
		
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public List<T> find(String json) throws ParseException {
		
		return find(new JSONObject(json));
		
	}
		
	@SuppressWarnings("unchecked")
	public List<T> find(JSONObject query) throws ParseException {
		
		String endPoint;
		if(!"users".equals(getClassName()) && !"roles".equals(getClassName())) {
			endPoint = "classes/" + getClassName();
		}
		else {
			endPoint = getClassName();
		}

		ParseGetCommand command = new ParseGetCommand(endPoint);
		query.remove("className");
		command.setData(query);
		ParseResponse response = command.perform();
		List<T> results = null;
		if(!response.isFailed()) {
			if(response.getJsonObject() == null) {
				LOGGER.debug("Empty response.");
				throw response.getException();
			}
			try {
				JSONObject json = response.getJsonObject();
				JSONArray objs = json.getJSONArray("results");
				if(objs.length() == 0) {
					return null;
				}
				
				if(trace) {
					strTrace = json.getString("trace");
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug(strTrace);
					}
				}
				
				results = new ArrayList<T>();
				for(int i = 0; i < objs.length(); i++) {
					Class<?> clazz = ParseRegistry.getParseClass(getClassName());
					if(clazz != null) {
						T po = (T) clazz.newInstance();
						JSONObject obj = (JSONObject) objs.get(i);
						 
						/*
						We disable some checks while setting data in objects during fetch because
						those checks are useful only when setting data from client
						code. The "true" argument disables such checks.
						*/
						po.setData(obj, true);
						results.add((T) po);
					}
					else {
						ParseObject po = new ParseObject(getClassName());
						JSONObject obj = (JSONObject) objs.get(i);
						// see above for the "true" argument
						po.setData(obj, true);
						results.add((T) po);
					}
				}

				return results;
			}
			catch (JSONException e) {
				LOGGER.error(
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
				throw new ParseException(
						ParseException.INVALID_JSON,
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
			} catch (InstantiationException e) {
				LOGGER.error("Error while instantiating class. Did you register your subclass?", e);
				throw new ParseException(
					ParseException.INVALID_JSON,
					"Although Parse reports object successfully saved, the response was invalid.",
					e);
			} catch (IllegalAccessException e) {
				LOGGER.error("Error while instantiating class. Did you register your subclass?",e);
				throw new ParseException(
					ParseException.INVALID_JSON,
					"Although Parse reports object successfully saved, the response was invalid.",
					e);
			}
		}
		else {
			LOGGER.debug("Request failed.");
			throw response.getException();
		}
	}

	public void findInBackground(FindCallback<T> callback) {
		FindInBackgroundThread task = new FindInBackgroundThread(callback);
		ParseExecutor.runInBackground(task);
	}
	
	class FindInBackgroundThread extends Thread {
		FindCallback<T> callback;

		public FindInBackgroundThread(FindCallback<T> callback) {
			this.callback = callback;
		}

		public void run() {
			ParseException exception = null;
			List<T> object = null;
			try {
				object = find();
			} catch (ParseException e) {
				exception = e;
			}
			if (callback != null) {
				callback.done(object, exception);
			}
		}
	}

	public int count() throws ParseException {
		
		String endPoint;
		if(!"users".equals(getClassName()) && !"roles".equals(getClassName())) {
			endPoint = "classes/" + getClassName();
		}
		else {
			endPoint = getClassName();
		}

		ParseGetCommand command = new ParseGetCommand(endPoint);
		JSONObject query = toREST();
		query.put("count", 1);
		query.put("limit", 0);
		query.remove("className");
		command.setData(query);
		ParseResponse response = command.perform();
		if(!response.isFailed()) {
			if(response.getJsonObject() == null) {
				LOGGER.debug("Empty response.");
				throw response.getException();
			}
			try {
				JSONObject json = response.getJsonObject();
				int count = json.getInt("count");
				return count;
			}
			catch (JSONException e) {
				LOGGER.error(
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
				throw new ParseException(
						ParseException.INVALID_JSON,
						"Although Parse reports object successfully saved, the response was invalid.",
						e);
			}
		}
		else {
			LOGGER.debug("Request failed.");
			throw response.getException();
		}
		
	}

	public void countInBackground(CountCallback countCallback) {
		CountInBackgroundThread task = new CountInBackgroundThread(countCallback);
		ParseExecutor.runInBackground(task);
	}
	
	class CountInBackgroundThread extends Thread {
		CountCallback callback;

		public CountInBackgroundThread(CountCallback callback) {
			this.callback = callback;
		}

		public void run() {
			ParseException exception = null;
			int count = -1;
			try {
				count = count();
			} catch (ParseException e) {
				exception = e;
			}
			if (callback != null) {
				callback.done(count, exception);
			}
		}
	}

	@SuppressWarnings("serial")
	static class KeyConstraints extends HashMap<String, Object> { }

	@SuppressWarnings("serial")
	static class QueryConstraints extends HashMap<String, Object> { }
	

	public class RelationConstraint {
		private String key;
		private ParseObject object;

		public RelationConstraint(String key, ParseObject object) {
			if ((key == null) || (object == null)) {
				throw new IllegalArgumentException("Arguments must not be null.");
			}
			this.key = key;
			this.object = object;
		}

		public String getKey() {
			return this.key;
		}

		public ParseObject getObject() {
			return this.object;
		}

		public ParseRelation<ParseObject> getRelation() {
			return this.object.getRelation(this.key);
		}

		public JSONObject encode(ParseObjectEncodingStrategy objectEncoder) {
			JSONObject json = new JSONObject();
			try {
				json.put("key", this.key);
				json.put("object",
						objectEncoder.encodeRelatedObject(this.object));
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
			return json;
		}
	}

}
