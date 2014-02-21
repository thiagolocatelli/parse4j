package org.parse4j.operation;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parse4j.ParseObject;
import org.parse4j.ParseRelation;
import org.parse4j.encode.ParseObjectEncodingStrategy;
import org.parse4j.util.ParseEncoder;

public class RelationOperation<T extends ParseObject> implements ParseFieldOperation {

	private String targetClass;
	private Set<ParseObject> relationsToAdd;
	private Set<ParseObject> relationsToRemove;
	
	public RelationOperation(Set<T> newRelationsToAdd, Set<T> newRelationsToRemove) {
		this.targetClass = null;
		this.relationsToAdd = new HashSet<ParseObject>();
		this.relationsToRemove = new HashSet<ParseObject>();

		if (newRelationsToAdd != null) {
			for (ParseObject object : newRelationsToAdd) {
				addParseObjectToSet(object, this.relationsToAdd);

				if (this.targetClass == null) {
					this.targetClass = object.getClassName();
				} else if (!this.targetClass.equals(object.getClassName())) {
					throw new IllegalArgumentException(
							"All objects in a relation must be of the same class.");
				}

			}

		}

		if (newRelationsToRemove != null) {
			for (ParseObject object : newRelationsToRemove) {
				addParseObjectToSet(object, this.relationsToRemove);

				if (this.targetClass == null) {
					this.targetClass = object.getClassName();
				} else if (!this.targetClass.equals(object.getClassName())) {
					throw new IllegalArgumentException(
							"All objects in a relation must be of the same class.");
				}

			}

		}

		if (this.targetClass == null)
			throw new IllegalArgumentException(
					"Cannot create a ParseRelationOperation with no objects.");
	}	
	
	private void addParseObjectToSet(ParseObject obj, Set<ParseObject> set) {
		
		if (obj.getObjectId() == null) {
			set.add(obj);
			return;
		}

		for (ParseObject existingObject : set) {
			if (obj.getObjectId().equals(existingObject.getObjectId())) {
				set.remove(existingObject);
			}
		}
		set.add(obj);
	}

	public String getTargetClass() {
		return this.targetClass;
	}

	public JSONArray convertSetToArray(Set<ParseObject> set, ParseObjectEncodingStrategy objectEncoder) throws JSONException {
		JSONArray array = new JSONArray();
		for (ParseObject obj : set) {
			array.put(ParseEncoder.encode(obj, objectEncoder));
		}
		return array;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object apply(Object oldValue, ParseObject parseObject, String key) {
		ParseRelation<T>relation = null;

		if (oldValue == null) {
			relation = new ParseRelation<T>(parseObject, key);
			relation.setTargetClass(this.targetClass);
		} else if ((oldValue instanceof ParseRelation)) {
			relation = (ParseRelation<T>) oldValue;
			if ((this.targetClass != null) && (relation.getTargetClass() != null)) {
				if (!relation.getTargetClass().equals(this.targetClass)) {
					throw new IllegalArgumentException(
							"Related object object must be of class "
									+ relation.getTargetClass() + ", but "
									+ this.targetClass + " was passed in.");
				}

				relation.setTargetClass(this.targetClass);
			}

		} else {
			throw new IllegalArgumentException(
					"Operation is invalid after previous operation.");
		}
		return relation;
	}

	@Override
	public JSONObject encode(ParseObjectEncodingStrategy objectEncoder) throws JSONException {
		
		JSONObject adds = null;
		JSONObject removes = null;

		if (this.relationsToAdd.size() > 0) {
			adds = new JSONObject();
			adds.put("__op", "AddRelation");
			adds.put("objects", convertSetToArray(this.relationsToAdd, objectEncoder));
		}

		if (this.relationsToRemove.size() > 0) {
			removes = new JSONObject();
			removes.put("__op", "RemoveRelation");
			removes.put("objects", convertSetToArray(this.relationsToRemove, objectEncoder));
		}

		if ((adds != null) && (removes != null)) {
			JSONObject result = new JSONObject();
			result.put("__op", "Batch");
			JSONArray ops = new JSONArray();
			ops.put(adds);
			ops.put(removes);
			result.put("ops", ops);
			return result;
		}

		if (adds != null) {
			return adds;
		}

		if (removes != null) {
			return removes;
		}

		throw new IllegalArgumentException(
				"A ParseRelationOperation was created without any data.");
	}

}
