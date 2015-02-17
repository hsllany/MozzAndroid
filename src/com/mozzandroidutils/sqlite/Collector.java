package com.mozzandroidutils.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mozzandroidutils.file.ObjectByte;

import android.database.Cursor;

public class Collector {
	Collector(Cursor cursor, Map<String, ColumnType> columnTypes) {
		mCursor = cursor;
		mColumns = columnTypes;
	}

	public List<Model> get() throws IllegalAccessException {
		if (mCursor == null)
			throw new IllegalAccessException(
					"result has been cosumed, please do a query again.");

		List<Model> result = new ArrayList<Model>();
		if (mCursor.moveToFirst()) {
			while (mCursor.moveToNext()) {
				Model model = new Model();
				int length = mCursor.getColumnCount();

				for (int i = 0; i < length; i++) {
					String columnName = mCursor.getColumnName(i);

					ColumnType type = mColumns.get(columnName);
					switch (type) {
					case TYPE_INTEGER:
						model.set(columnName, mCursor.getInt(i));
						break;
					case TYPE_BLOB:
						model.set(columnName,
								ObjectByte.toObject(mCursor.getBlob(i)));
						break;
					case TYPE_TEXT:
						model.set(columnName, mCursor.getString(i));
						break;
					case TYPE_REAL:
						model.set(columnName, mCursor.getDouble(i));
						break;
					}

				}
				result.add(model);
			}
		}

		close();

		return result;
	}

	public Model first() throws IllegalAccessException {
		if (mCursor == null)
			throw new IllegalAccessException(
					"result has been cosumed, please do a query again.");

		if (mCursor.moveToFirst()) {
			Model model = new Model();
			int length = mCursor.getColumnCount();

			for (int i = 0; i < length; i++) {
				String columnName = mCursor.getColumnName(i);

				ColumnType type = mColumns.get(columnName);
				switch (type) {
				case TYPE_INTEGER:
					model.set(columnName, mCursor.getInt(i));
					break;
				case TYPE_BLOB:
					model.set(columnName,
							ObjectByte.toObject(mCursor.getBlob(i)));
					break;
				case TYPE_TEXT:
					model.set(columnName, mCursor.getString(i));
					break;
				case TYPE_REAL:
					model.set(columnName, mCursor.getDouble(i));
					break;
				}
			}
			close();
			return model;
		} else {
			close();
			return null;
		}

	}

	private void close() {
		if (mCursor != null)
			mCursor.close();
		mCursor = null;
	}

	private Cursor mCursor;
	private Map<String, ColumnType> mColumns;
}
