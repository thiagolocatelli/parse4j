package org.parse4j.callback;

import org.parse4j.ParseException;

public abstract class CountCallback extends ParseCallback<Integer> {

	public abstract void done(Integer count, ParseException parseException);

	@Override
	void internalDone(Integer count, ParseException parseException) {
		if (parseException == null)
			done(count, null);
		else
			done(-1, parseException);
	}

}
