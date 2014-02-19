package org.parse4j.operation;

import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseObject;
import org.parse4j.encode.ParseObjectEncodingStrategy;

public class IncrementFieldOperation implements ParseFieldOperation {

	private Number amount;
	private boolean needIncrement = true;

	public IncrementFieldOperation(Number amount) {
		this.amount = amount;
	}

	@Override
	public Object apply(Object oldValue, ParseObject parseObject, String key) {
		
		if (oldValue == null) {
			needIncrement = false;
			return amount;
		}
		
		if ((oldValue instanceof Number)) {
			return OperationUtil.addNumbers((Number) oldValue, this.amount);
		}
		
		throw new IllegalArgumentException("You cannot increment a non-number. Key type ["+oldValue.getClass().getCanonicalName()+"]");
	}

	@Override
	public Object encode(ParseObjectEncodingStrategy objectEncoder)
			throws JSONException {
		if(needIncrement) {
			JSONObject output = new JSONObject();
			output.put("__op", "Increment");
			output.put("amount", this.amount);
			return output;
		}
		else {
			return amount;
		}
	}

}
