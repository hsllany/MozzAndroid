package com.mozzandroidutils.sqlite;

import java.lang.reflect.Field;

public abstract class Model {
	private String DEBUG_TAG = this.getClass().getSimpleName();

	int id = -1;

	public int id() {
		return this.id;
	}

	public boolean hasSetId() {
		if (this.id >= 0)
			return true;
		return false;
	}

	boolean setField(String fieldName, Object obj) {
		Class<? extends Model> thisClass = this.getClass();

		try {
			if (fieldName.equals("_id")) {
				Class<?> superClass = thisClass.getSuperclass();
				Field field = superClass.getDeclaredField("_id");
				field.setAccessible(true);
				field.set(this, obj);
			} else {
				Field field = thisClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(this, obj);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	Object fieldValue(String fieldName) {
		Class<? extends Model> thisClass = this.getClass();
		try {
			if (fieldName.equals("_id")) {
				Class<?> superClass = thisClass.getSuperclass();
				Field field = superClass.getDeclaredField("_id");
				field.setAccessible(true);
				return field.get(this);
			} else {
				Field field = thisClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field.get(this);
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		return null;
	}

	String[] allOtherFields() {
		Class<? extends Model> thisClass = this.getClass();
		Field[] fields = thisClass.getDeclaredFields();
		String[] fieldsString = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			fieldsString[i] = fields[i].getName();
		}

		return fieldsString;
	}

	public String toJson() {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"id\": " + this.id + "");
		String[] allFields = allOtherFields();

		for (int i = 0; i < allFields.length; i++) {
			String fieldName = allFields[i];
			Object value = fieldValue(fieldName);
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
}
