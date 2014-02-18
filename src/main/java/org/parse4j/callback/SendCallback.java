package org.parse4j.callback;

import org.parse4j.ParseException;

public abstract class SendCallback extends ParseCallback<Void> {
	
	abstract void done(ParseException parseException);
	
	@Override
	void internalDone(Void paramT, ParseException parseException) {
		done(parseException);
	}

}
