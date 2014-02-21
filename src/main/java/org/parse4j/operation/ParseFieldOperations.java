package org.parse4j.operation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseObject;
import org.parse4j.util.ParseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ParseFieldOperations {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseFieldOperations.class);
	
	
	static Map<String, ParseFieldOperationFactory> opDecoderMap = 
			new HashMap<String, ParseFieldOperationFactory>();

	private static void registerDecoder(String opName, ParseFieldOperationFactory factory) {
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registering {} decoder", opName);
		}
		opDecoderMap.put(opName, factory);
	}

	private static abstract interface ParseFieldOperationFactory {
		public abstract ParseFieldOperation decode(JSONObject paramJSONObject) throws JSONException;
	}
	
	public static void registerDefaultDecoders() {
		/*
		registerDecoder("Batch", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				ParseFieldOperation op = null;
				JSONArray ops = object.getJSONArray("ops");
				for (int i = 0; i < ops.length(); i++) {
					ParseFieldOperation nextOp = ParseFieldOperations.decode(
							ops.getJSONObject(i));
					op = nextOp.mergeWithPrevious(op);
				}
				return op;
			}
		});
		*/
		
		registerDecoder("Delete", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				return new DeleteFieldOperation();
			}
		});
		registerDecoder("Increment", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				return new IncrementFieldOperation((Number) object
						.opt("amount"));
			}
		});
		registerDecoder("Add", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				return new AddOperation(object.opt("objects"));
			}
		});
		registerDecoder("AddUnique", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				return new AddUniqueOperation(object.opt("objects"));
			}
		});
		registerDecoder("Remove", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				return new RemoveFieldOperation(object.opt("objects"));
			}
		});
		registerDecoder("AddRelation", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				JSONArray objectsArray = object.optJSONArray("objects");
				List objectsList = (List) ParseDecoder.decode(objectsArray);
				return new RelationOperation(new HashSet(objectsList),
						null);
			}
		});
		registerDecoder("RemoveRelation", new ParseFieldOperationFactory() {
			public ParseFieldOperation decode(JSONObject object) throws JSONException {
				JSONArray objectsArray = object.optJSONArray("objects");
				List objectsList = (List) ParseDecoder.decode(objectsArray);
				return new RelationOperation(null,
						new HashSet(objectsList));
			}
		});
	}
	
	public static ParseFieldOperation decode(JSONObject encoded) throws JSONException {
		String op = encoded.optString("__op");
		ParseFieldOperationFactory factory = (ParseFieldOperationFactory) opDecoderMap.get(op);
		if (factory == null) {
			throw new RuntimeException("Unable to decode operation of type " + op);
		}
		return factory.decode(encoded);
	}

}
