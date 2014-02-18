package org.parse4j.callback;

import org.parse4j.ParseException;
import org.parse4j.ParseGeoPoint;

public abstract class LocationCallback extends ParseCallback<ParseGeoPoint> {

	abstract void done(ParseGeoPoint parseGeoPoint, ParseException parseException);

	@Override
	void internalDone(ParseGeoPoint parseGeoPoint, ParseException parseException) {
		done(parseGeoPoint, parseException);
	}
	
}
