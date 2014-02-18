package org.parse4j.callback;

import org.parse4j.ParseException;

public abstract class ParseCallback<T> {
	
	abstract void internalDone(T paramT, ParseException parseException);
	
}
