package com.mozzandroidutils.sqlite;

public abstract class Model {

	public long id() {
		return mId;
	}

	public boolean hasSetId() {
		if (mId >= 0)
			return true;
		return false;
	}

	void set(String fieldName, Object value) throws IllegalArgumentException {
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

	void setId(long newId) {
		mId = newId;
	}

	private long mId = -1;
}
