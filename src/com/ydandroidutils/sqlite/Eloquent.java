package com.ydandroidutils.sqlite;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Eloquent<T extends Model> {

	private final static String ID_COLUMN = "_id";

	public Cursor all() {
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName, null);
		return cursor;
	}

	public ArrayList<T> allAsList() {
		return null;
	}

	public T Find(int id, T t) {
		Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
				+ " where " + ID_COLUMN + " = " + id, null);

		if (cursor.moveToFirst()) {
			int columnCount = cursor.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				String columnName = cursor.getColumnName(i);
				int columnType = cursor.getType(cursor
						.getColumnIndex(columnName));

				switch (columnType) {
				case Cursor.FIELD_TYPE_INTEGER:
					setField(t, columnName,
							cursor.getInt(cursor.getColumnIndex(columnName)));
					break;

				case Cursor.FIELD_TYPE_BLOB:
					setField(t, columnName,
							cursor.getBlob(cursor.getColumnIndex(columnName)));
					break;

				case Cursor.FIELD_TYPE_FLOAT:
					setField(t, columnName,
							cursor.getFloat(cursor.getColumnIndex(columnName)));
					break;

				case Cursor.FIELD_TYPE_STRING:
					setField(t, columnName,
							cursor.getString(cursor.getColumnIndex(columnName)));
					break;

				case Cursor.FIELD_TYPE_NULL:
					setField(t, columnName, null);
					break;
				}
			}
		}

		return t;

	}

	public Eloquent(Context context) {
		mDatabase = YDDBHelper.DBInstance(context);
		mContext = context;

		if (mTableName == null) {
			String className = this.getClass().getSimpleName();
			mTableName = className.substring(0, className.indexOf("Eloquent"))
					.toLowerCase();
		}

	}

	public Eloquent(Context context, String tableName) {
		this(context);

		mTableName = tableName.toLowerCase();

	}

	public boolean save(T t) {
		boolean insertMode = true;
		if (t.hasSetId()) {

			Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
					+ " where " + ID_COLUMN + " = " + t.id(), null);

			if (cursor.moveToFirst()) {
				int countId = cursor.getInt(0);

				if (countId > 0)
					insertMode = false;
			}
			cursor.close();
		}

		if (insertMode) {
			StringBuilder sb = new StringBuilder();
			sb.append("insert into " + mTableName + " values(,");

			String[] fields = t.allOtherFields();
			int fieldLength = fields.length;

			for (int i = 0; i < fieldLength; i++) {
				String fieldName = fields[i];
				Object value = t.fieldValue(fieldName);
				if (value != null) {	
				}
			}
		}

		return false;
	}

	private void setField(T t, String fieldName, Object value) {
		t.setField(fieldName, value);
	}

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;
	private Context mContext;

}
