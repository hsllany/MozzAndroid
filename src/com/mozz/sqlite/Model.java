package com.mozz.sqlite;

public abstract class Model {

	public Model() {

	}

	public final long id() {
		return mId;
	}

	public final boolean hasSetId() {
		if (mId >= 0)
			return true;
		return false;
	}

	final void set(String fieldName, Object value)
			throws IllegalArgumentException {
		if (fieldName.equalsIgnoreCase("id")) {
			if (value instanceof Long || value instanceof Integer) {
				ObjectGenerator.set(this, fieldName,
						((Number) value).longValue());
			} else {
				throw new IllegalArgumentException("id must be long or integer");
			}
		} else {
			ObjectGenerator.set(this, fieldName, value);
		}
	}

	final void setId(long newId) {
		mId = newId;
	}

	private long mId = -1;
}
