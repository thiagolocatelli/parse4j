package org.parse4j.callback;

import org.parse4j.ParseException;

public abstract class GetDataCallback extends ParseCallback<byte[]> {

	public abstract void done(byte[] data, ParseException e);

	final void internalDone(byte[] returnValue, ParseException e) {
		done(returnValue, e);
	}

}
