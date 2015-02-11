package com.mozzandroidutils.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class Eloquent<T extends Model> {

	private final static String ID_COLUMN = "_id";

	public static enum ORDER {
		DESC, ASC
	};

	public static void create(String createSQL, Context context) {
		SQLiteDatabase database = MozzDB.database(context);
		synchronized (database) {
			database.execSQL(createSQL);
		}
	}

	public Cursor all() {
		if (mTableExist) {
			Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName,
					null);
			return cursor;
		} else {
			return null;
		}
	}

	public Cursor all(ORDER order) {
		if (mTableExist) {
			if (order == ORDER.DESC) {
				Cursor cursor = mDatabase.rawQuery("SELECT * FROM "
						+ mTableName + " order by _id desc", null);
				return cursor;
			} else {
				Cursor cursor = mDatabase.rawQuery("SELECT * FROM "
						+ mTableName + "order by _id", null);
				return cursor;
			}
		} else {

			return null;
		}
	}

	public Cursor where(String[] keys, Object[] values) {

		if (keys.length != values.length || !mTableExist)
			return null;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			sb.append(" " + keys[i] + " = '" + values[i] + "' ");
			if (i < keys.length - 1)
				sb.append("&");
		}

		String selectSQL = "select * from " + mTableName + " where "
				+ sb.toString();
		return mDatabase.rawQuery(selectSQL, null);
	}

	public ArrayList<T> allAsList() {
		return null;
	}

	public T Find(int id, T t) {
		if (mTableExist) {
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
						setField(t, columnName, cursor.getInt(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_BLOB:
						setField(t, columnName, cursor.getBlob(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_FLOAT:
						setField(t, columnName, cursor.getFloat(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_STRING:
						setField(t, columnName, cursor.getString(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_NULL:
						setField(t, columnName, null);
						break;
					}
				}
			}

			return t;
		} else {
			return null;
		}
	}

	public Eloquent(Context context) {
		mDatabase = MozzDB.database(context);

		if (mTableName == null) {
			String className = this.getClass().getSimpleName();
			mTableName = className.substring(0, className.indexOf("Eloquent"))
					.toLowerCase();
		}

		checkTableExistAndColumn();
	}

	public Eloquent(Context context, String tableName) {
		this(context);

		mTableName = tableName.toLowerCase();

	}

	public boolean save(T t) {
		if (mTableExist) {
			boolean insertMode = true;
			if (t.hasSetId()) {
				Cursor cursor = null;
				synchronized (mDatabase) {
					cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
							+ " where " + ID_COLUMN + " = " + t.id(), null);
				}

				if (cursor.moveToFirst()) {
					int countId = cursor.getInt(0);

					if (countId > 0)
						insertMode = false;
				}

				if (cursor != null)
					cursor.close();
			}

			if (insertMode) {
				StringBuilder sb = new StringBuilder();
				StringBuilder valueSb = new StringBuilder();
				sb.append("insert into " + mTableName + "(");
				valueSb.append(") values(");

				String[] fields = t.allOtherFields();
				int fieldLength = fields.length;

				for (int i = 0; i < fieldLength; i++) {
					String fieldName = fields[i];
					Object value = t.fieldValue(fieldName);
					if (value != null && mColumn.containsKey(fieldName)) {
						sb.append(fieldName);
						valueSb.append("'" + value.toString() + "'");

						if (i < fieldLength - 1) {
							sb.append(",");
							valueSb.append(",");
						}
					}
				}

				String sqlInsert = sb.toString() + valueSb.toString() + ")";
				synchronized (mDatabase) {
					mDatabase.execSQL(sqlInsert);
				}

				return true;
			} else {
				StringBuilder sb = new StringBuilder();
				String[] fields = t.allOtherFields();
				int fieldLength = fields.length;

				for (int i = 0; i < fieldLength; i++) {
					String fieldName = fields[i];
					Object value = t.fieldValue(fieldName);
					if (value != null && mColumn.containsKey(fieldName)) {
						sb.append(fieldName + " = '" + value.toString() + "'");

						if (i < fieldLength - 1) {
							sb.append(",");
						}
					}
				}

				String upgrateSQL = "update " + mTableName + " set "
						+ sb.toString() + " where _id = " + t._id;
				synchronized (mDatabase) {
					mDatabase.execSQL(upgrateSQL);
				}

				return true;
			}

		} else {
			return false;
		}
	}

	private void setField(T t, String fieldName, Object value) {
		t.setField(fieldName, value);
	}

	private void checkTableExistAndColumn() {
		Cursor cursor = null;
		synchronized (mDatabase) {
			cursor = mDatabase.rawQuery(
					"select count(*) from sqlite_master where type = 'table' and name = '"
							+ mTableName + "';", null);
		}
		if (cursor.moveToFirst()) {
			if (cursor.getInt(0) > 0)
				mTableExist = true;
		}

		if (cursor != null)
			cursor.close();

		synchronized (mColumn) {
			mColumn.clear();
		}

		if (mTableExist) {

			synchronized (mDatabase) {
				cursor = mDatabase.rawQuery("select * from " + mTableName
						+ " limit 1", null);
			}
			if (cursor.moveToFirst()) {
				int columnCount = cursor.getColumnCount();

				for (int i = 0; i < columnCount; i++) {
					String columnName = cursor.getColumnName(i);
					int columnType = cursor.getType(i);
					synchronized (mColumn) {
						mColumn.put(columnName, columnType);
					}
				}
			}

			if (cursor != null)
				if (!cursor.isClosed())
					cursor.close();
		}
	}

	public void drop() {
		if (mTableExist) {
			String dropSQL = "drop table " + mTableName;
			synchronized (mDatabase) {
				mDatabase.execSQL(dropSQL);
			}

			mTableExist = false;
		}
	}

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;

	private Map<String, Integer> mColumn = new HashMap<String, Integer>();
	private boolean mTableExist = false;

}
