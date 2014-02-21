package org.parse4j.util;

import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.parse4j.ParseClassName;
import org.parse4j.ParseObject;
import org.parse4j.ParseRole;
import org.parse4j.ParseUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseRegistry {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseRegistry.class);

	private static final Map<Class<? extends ParseObject>, String> classNames = 
			new ConcurrentHashMap<Class<? extends ParseObject>, String>();

	private static final Map<String, Class<? extends ParseObject>> objectTypes = 
			new ConcurrentHashMap<String, Class<? extends ParseObject>>();

	public static void registerDefaultSubClasses() {
		registerSubclass(ParseUser.class);
		registerSubclass(ParseRole.class);
	}
	
	public static void unregisterSubclass(String className) {
		objectTypes.remove(className);
	}

	public static void registerSubclass(Class<? extends ParseObject> subclass) {

		String className = getClassName(subclass);
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registering sub class {}", className);
		}
		if (className == null) {
			throw new IllegalArgumentException(
					"No ParseClassName annoation provided on " + subclass);
		}
	    
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
		
		Class<? extends ParseObject> oldValue = (Class<? extends ParseObject>) objectTypes.get(className);
	    if ((oldValue != null) && (subclass.isAssignableFrom(oldValue))) {
	      return;
	    }
	    
	    objectTypes.put(className, subclass);

	}

	private static boolean isAccessible(Member m) {
		return (Modifier.isPublic(m.getModifiers()))
				|| ((m.getDeclaringClass().getPackage().getName()
						.equals("com.parse"))
						&& (!Modifier.isPrivate(m.getModifiers())) && (!Modifier
							.isProtected(m.getModifiers())));
	}

	public static String getClassName(Class<? extends ParseObject> clazz) {
		String name = (String) classNames.get(clazz);
		if (name == null) {
			ParseClassName info = (ParseClassName) clazz.getAnnotation(ParseClassName.class);
			if (info == null) {
				return null;
			}
			name = info.value();
			classNames.put(clazz, name);
		}
		return name;
	}
	
	public static Class<? extends ParseObject> getParseClass(String className) {
		Class<? extends ParseObject> value = (Class<? extends ParseObject>) objectTypes.get(className);
		return value;
	}
	
}
