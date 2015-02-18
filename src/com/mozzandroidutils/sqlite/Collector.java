package com.mozzandroidutils.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mozzandroidutils.file.ObjectByte;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Collector {
	Collector(String tableName, SQLiteDatabase sqliteDatabase,
			Map<String, ColumnType> columnTypes) {
		mColumns = columnTypes;
		mTableName = tableName;
		mDatabase = sqliteDatabase;
	}

	void closeBuild() {
		mCanBuildQuery = false;
	}

	void changeTableName(String newTableName) {
		mTableName = newTableName;
	}

	void buildSelect(String select) {
		if (mCanBuildQuery) {
			mSelect = select;
			mIsQueryCosumed = false;
		}
	}

	void buildWhere(String where) {
		if (mCanBuildQuery) {
			mWhereBuilder.append(where);
			mIsQueryCosumed = false;
		}
	}

	private void clear() {
		mSelect = null;
		mWhereBuilder.delete(0, mWhereBuilder.length());
		mIsQueryCosumed = true;
		mCanBuildQuery = true;
	}

	private Cursor build() {
		if (!mIsQueryCosumed) {
			String query = null;
			if (mSelect != null) {
				query += mSelect + " FROM " + mTableName;
			} else {
				query += "SELECT * " + " FROM " + mTableName;
			}

			if (mWhereBuilder.length() > 0) {
				query += " WHERE " + mWhereBuilder.toString();
			}

			if (mDatabase != null && mDatabase.isOpen()) {
				return mDatabase.rawQuery(query, null);
			}
			clear();
		}
		return null;
	}

	public List<Model> get() throws IllegalAccessException {
		Cursor cursor = build();

		if (cursor == null)
			throw new IllegalAccessException(
					"result has been cosumed, please do a query again.");

		List<Model> result = new ArrayList<Model>();
		if (cursor.moveToFirst()) {
			while (cursor.moveToNext()) {
				Model model = new Model();
				int length = cursor.getColumnCount();

				for (int i = 0; i < length; i++) {
					String columnName = cursor.getColumnName(i);

					ColumnType type = mColumns.get(columnName);
					switch (type) {
					case TYPE_INTEGER:
						model.set(columnName, cursor.getInt(i));
						break;
					case TYPE_BLOB:
						model.set(columnName,
								ObjectByte.toObject(cursor.getBlob(i)));
						break;
					case TYPE_TEXT:
						model.set(columnName, cursor.getString(i));
						break;
					case TYPE_REAL:
						model.set(columnName, cursor.getDouble(i));
						break;
					}

				}
				result.add(model);
			}
		}

		close(cursor);

		return result;
	}

	public Model first() throws IllegalAccessException {
		Cursor cursor = build();

		if (cursor == null)
			throw new IllegalAccessException(
					"result has been cosumed, please do a query again.");

		if (cursor.moveToFirst()) {
			Model model = new Model();
			int length = cursor.getColumnCount();

			for (int i = 0; i < length; i++) {
				String columnName = cursor.getColumnName(i);

				ColumnType type = mColumns.get(columnName);
				switch (type) {
				case TYPE_INTEGER:
					model.set(columnName, cursor.getInt(i));
					break;
				case TYPE_BLOB:
					model.set(columnName,
							ObjectByte.toObject(cursor.getBlob(i)));
					break;
				case TYPE_TEXT:
					model.set(columnName, cursor.getString(i));
					break;
				case TYPE_REAL:
					model.set(columnName, cursor.getDouble(i));
					break;
				}
			}
			close(cursor);
			return model;
		} else {
			close(cursor);
			return null;
		}

	}

	private void close(Cursor cursor) {
		if (cursor != null)
			cursor.close();
		cursor = null;
	}

	private Map<String, ColumnType> mColumns;

	private String mSelect;
	private String mTableName;
	private StringBuilder mWhereBuilder;
	private SQLiteDatabase mDatabase;

	private boolean mIsQueryCosumed = true;
	private boolean mCanBuildQuery = true;
}
