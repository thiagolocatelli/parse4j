package org.parse4j;

import java.util.Map;

import org.json.JSONObject;
import org.parse4j.callback.FunctionCallback;
import org.parse4j.command.ParsePostCommand;
import org.parse4j.command.ParseResponse;

public class ParseCloud {

	@SuppressWarnings("unchecked")
	public static <T> T callFunction(String name, Map<String, ?> params)
			throws ParseException {

		T result = null;
		ParsePostCommand command = new ParsePostCommand("functions", name);
		command.setData(new JSONObject(params));
		ParseResponse response = command.perform();
		
		if(!response.isFailed()) {
			JSONObject jsonResponse = response.getJsonObject();
			result = (T) jsonResponse.get("result");
			return result;
		}
		else {
			throw response.getException();
		}
		
	}

	public static <T> void callFunctionInBackground(String name,
			Map<String, ?> params, FunctionCallback<T> callback) {

		CallFunctionInBackgroundThread<T> task = new CallFunctionInBackgroundThread<T>(name, params, callback);
		ParseExecutor.runInBackground(task);
	}
	
	private static class CallFunctionInBackgroundThread<T> extends Thread {
		Map<String, ?> params;
		FunctionCallback<T> functionCallback;
		String name;
		
		public CallFunctionInBackgroundThread(String name, Map<String, ?> params, FunctionCallback<T> functionCallback) {
			this.functionCallback = functionCallback;
			this.params = params;
			this.name = name;
		}

		public void run() {
			ParseException exception = null;
			T result = null;
			try {
				result = callFunction(name, params);
			} catch (ParseException e) {
				exception = e;
			}
			if (functionCallback != null) {
				functionCallback.done(result, exception);
			}
		}
	}

}
