package com.mozzandroidutils.sqlite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Model {

	public Model() {
		mField = new HashMap<String, Object>();
	}

	public int id() {
		return this.id;
	}

	public boolean hasSetId() {
		if (this.id >= 0)
			return true;
		return false;
	}

	public void set(String fieldName, Object obj)
			throws IllegalArgumentException {
		if (fieldName.equalsIgnoreCase("id")) {
			if (obj instanceof Integer)
				id = (Integer) obj;
			else
				throw new IllegalArgumentException("id must be integer");
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

	public String toJson() {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"id\": " + this.id + "");
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
	String mTable = null;
	int id = -1;
}
