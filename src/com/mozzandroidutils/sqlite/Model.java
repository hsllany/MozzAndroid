package com.mozzandroidutils.sqlite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Model {

	public Model() {
		mField = new HashMap<String, Object>();
	}

	public long id() {
		return mId;
	}

	public boolean hasSetId() {
		if (mId >= 0)
			return true;
		return false;
	}

	public void set(String fieldName, Object obj)
			throws IllegalArgumentException {
		if (fieldName.equalsIgnoreCase("id")) {
			throw new IllegalArgumentException(
					"Id can be only get by Eloquenet's query methods. See Eloquenet's where(), all() etc..");
		} else {
			mField.put(fieldName, obj);
		}
	}

	public Object get(String fieldName) {
		if (fieldName.equalsIgnoreCase("id"))
			return id();
		return mField.get(fieldName);
	}

	Set<String> allOtherFields() {
		return mField.keySet();
	}

	Set<Entry<String, Object>> fieldsAndValues() {
		return mField.entrySet();
	}

	void setId(long newId) {
		mId = newId;
	}

	void setTableName(String tableName) {
		mTable = tableName;
	}

	public String toJson() {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"id\": " + this.mId + "");
		Set<Entry<String, Object>> entrySet = fieldsAndValues();

		Iterator<Entry<String, Object>> it = entrySet.iterator();

		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();

			String fieldName = entry.getKey();

			Object value = entry.getValue();
			if (value instanceof Number) {
				jsonBuilder.append(",\"" + fieldName + "\":" + value.toString()
						+ "");
			} else {
				jsonBuilder.append(",\"" + fieldName + "\":\""
						+ value.toString() + "\"");
			}
		}

		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

	@Override
	public String toString() {
		return toJson();
	}

	private HashMap<String, Object> mField;
	private String mTable = null;
	private long mId = -1;
}
