package org.parse4j;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class Parse {

	private static String mApplicationId;
	private static String mRestAPIKey;
	private static final DateFormat dateFormat;

	private static final Map<String, Class<? extends ParseObject>> objectTypes = 
			new ConcurrentHashMap<String, Class<? extends ParseObject>>();

	static {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		format.setTimeZone(new SimpleTimeZone(0, "GMT"));
		dateFormat = format;

		registerSubclass(ParseUser.class);
		registerSubclass(ParseRole.class);
		// registerSubclass(ParseInstallation.class);
	}

	static public void initialize(String applicationId, String restAPIKey) {
		mApplicationId = applicationId;
		mRestAPIKey = restAPIKey;
	}

	static public String getApplicationId() {
		return mApplicationId;
	}

	static public String getRestAPIKey() {
		return mRestAPIKey;
	}

	static public String getParseAPIUrl(String context) {
		return ParseConstants.API_ENDPOINT + "/" + ParseConstants.API_VERSION
				+ "/" + context;
	}

	public static synchronized String encodeDate(Date date) {
		return dateFormat.format(date);
	}

	public static synchronized Date parseDate(String dateString) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean isInvalidKey(String key) {
		return "objectId".equals(key) || "createdAt".equals(key)
				|| "updatedAt".equals(key);
	}

	public static boolean isValidType(Object value) {
		return ((value instanceof JSONObject))
				|| ((value instanceof JSONArray))
				|| ((value instanceof String))
				|| ((value instanceof Number))
				|| ((value instanceof Boolean))
				|| (value == JSONObject.NULL)
				|| ((value instanceof ParseObject))
				// || ((value instanceof ParseACL))
				|| ((value instanceof ParseFile))
				// || ((value instanceof ParseRelation)
				|| ((value instanceof ParseGeoPoint))
				|| ((value instanceof Date)) || ((value instanceof byte[]))
				|| ((value instanceof List)) || ((value instanceof Map));
	}

	public static void registerSubclass(Class<? extends ParseObject> subclass) {
		
		
		if (subclass.getDeclaredConstructors().length > 0) {
			try {
				if (!isAccessible(subclass.getDeclaredConstructor(new Class[0])))
					throw new IllegalArgumentException(
							"Default constructor for " + subclass
									+ " is not accessible.");
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(
						"No default constructor provided for " + subclass);
			}
		}

		ParseObject po = (ParseObject) subclass;
		objectTypes.put(((ParseObject) subclass).getClassName(), subclass);
	}

	private static boolean isAccessible(Member m) {
		return (Modifier.isPublic(m.getModifiers()))
				|| ((m.getDeclaringClass().getPackage().getName()
						.equals("com.parse"))
						&& (!Modifier.isPrivate(m.getModifiers())) && (!Modifier
							.isProtected(m.getModifiers())));
	}

}