package org.parse4j.callback;

import org.parse4j.ParseException;

public abstract class CountCallback extends ParseCallback<Integer> {

	abstract void done(int paramInt, ParseException parseException);

	@Override
	void internalDone(Integer paramInt, ParseException parseException) {
		if (parseException == null)
			done(paramInt.intValue(), null);
		else
			done(-1, parseException);
	}

}
