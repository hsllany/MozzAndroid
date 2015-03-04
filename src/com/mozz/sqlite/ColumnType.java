package com.mozz.sqlite;

public enum ColumnType {

	TYPE_INTEGER("integer"), TYPE_REAL("real"), TYPE_TEXT("text"), TYPE_BLOB(
			"blob");

	private String mValue;

	ColumnType(String value) {
		mValue = value;
	}

	@Override
	public String toString() {
		return mValue;
	}
}
