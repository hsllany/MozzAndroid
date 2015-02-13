package com.mozzandroidutils.sqlite;

import java.util.HashMap;
import java.util.Map;

import com.mozzandroidutils.file.ObjectByte;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 代表数据库中的表
 * 
 * @author Yang
 * 
 * @param <T>, 必须是Model类继承
 */
public abstract class Eloquent<T extends Model> {

	private String DEBUG_TAG = this.getClass().getSimpleName();

	private final static String ID_COLUMN = "_id";

	/**
	 * 顺序
	 * 
	 * @author PC
	 * 
	 */
	public static enum ORDER {
		DESC, ASC
	};

	/**
	 * 创建表
	 * 
	 * @param tableName
	 *            , 表名
	 * @param columnNames
	 *            , 列名
	 * @param types
	 *            , 列的类型
	 * @param context
	 * @return
	 */
	public static boolean create(String tableName, String[] columnNames,
			ColumnType[] types, Context context) {

		if (columnNames.length != types.length || columnNames.length == 0
				|| types.length == 0)
			return false;

		SQLiteDatabase database = MozzDB.writebleDatabase(context);
		StringBuilder sb = new StringBuilder();

		sb.append("CREATE TABLE IF NOT EXISTS " + tableName.toLowerCase());
		sb.append(" (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, ");

		for (int i = 0; i < columnNames.length; i++) {
			String type = types[i].toString();

			if (type != null) {
				sb.append(columnNames[i] + " " + type);

				if (i < columnNames.length - 1)
					sb.append(",");
			}
		}

		String createSQL = sb.toString() + ")";

		Log.d("Eloquent", createSQL);
		synchronized (database) {
			database.execSQL(createSQL);
		}

		return true;
	}

	public Cursor all() {
		if (mTableExist) {
			debug("SELECT * FROM " + mTableName);
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
				debug("SELECT * FROM " + mTableName + " ORDER BY " + ID_COLUMN
						+ " DESC");
				Cursor cursor = mDatabase
						.rawQuery("SELECT * FROM " + mTableName + " ORDER BY "
								+ ID_COLUMN + " DESC", null);
				return cursor;
			} else {
				debug("SELECT * FROM " + mTableName + "ORDER BY " + ID_COLUMN);
				Cursor cursor = mDatabase.rawQuery("SELECT * FROM "
						+ mTableName + "ORDER BY " + ID_COLUMN, null);
				return cursor;
			}
		} else {

			return null;
		}
	}

	public Cursor where(String whereSQL) {

		if (!mTableExist)
			return null;

		String selectSQL = "SELECT * FROM " + mTableName + " " + whereSQL;
		debug(selectSQL);

		return mDatabase.rawQuery(whereSQL, null);
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

		String selectSQL = "SELECT * FROM " + mTableName + " WHERE "
				+ sb.toString();
		debug(selectSQL);
		return mDatabase.rawQuery(selectSQL, null);
	}

	public T Find(int id, T t) {
		if (mTableExist) {
			debug("SELECT * FROM " + mTableName + " WHERE " + ID_COLUMN + " = "
					+ id);
			Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
					+ " WHERE " + ID_COLUMN + " = " + id, null);

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
						Object obj = ObjectByte.toObject(cursor.getBlob(cursor
								.getColumnIndex(columnName)));
						setField(t, columnName, obj);
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
		mDatabase = MozzDB.writebleDatabase(context);

		if (mTableName == null) {
			String className = this.getClass().getSimpleName();
			mTableName = className.substring(0, className.indexOf("Eloquent"))
					.toLowerCase();
		}

		checkTableExistAndColumn();
	}

	Eloquent(Context context, boolean readOnly) {
		if (readOnly) {
			mDatabase = MozzDB.readOnlyDatabase(context);
			mReadOnly = true;
		} else {
			mDatabase = MozzDB.writebleDatabase(context);
		}

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

	Eloquent(Context context, String tableName, boolean readOnly) {
		this(context, readOnly);

		mTableName = tableName.toLowerCase();
	}

	public boolean save(T t) {
		if (mReadOnly)
			return false;

		if (mTableExist) {
			boolean insertMode = true;
			if (t.hasSetId()) {
				Cursor cursor = null;
				debug("SELECT * FROM " + mTableName + " WHERE " + ID_COLUMN
						+ " = " + t.id());
				synchronized (mDatabase) {
					cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
							+ " WHERE " + ID_COLUMN + " = " + t.id(), null);
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
						ColumnType type = mColumn.get(fieldName);
						if (type == ColumnType.TYPE_BLOB) {
							byte[] valueByte = ObjectByte.toByteArray(value);

							for (int j = 0; j < valueByte.length; j++) {
								valueSb.append(valueByte[j]);
							}
						} else {
							valueSb.append("'" + value.toString() + "'");

						}

						if (i < fieldLength - 1) {
							sb.append(",");
							valueSb.append(",");
						}
					}
				}

				String sqlInsert = sb.toString() + valueSb.toString() + ")";
				debug(sqlInsert);
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
						ColumnType type = mColumn.get(fieldName);

						if (type == ColumnType.TYPE_BLOB) {
							sb.append(fieldName + "=");
							byte[] valueByte = ObjectByte.toByteArray(value);

							for (int j = 0; j < valueByte.length; j++) {
								sb.append(valueByte[j]);
							}

						} else {
							sb.append(fieldName + " = '" + value.toString()
									+ "'");
						}

						if (i < fieldLength - 1) {
							sb.append(",");
						}
					}
				}

				String upgrateSQL = "UPDATE " + mTableName + " SET "
						+ sb.toString() + " WHERE " + ID_COLUMN + " = " + t._id;
				debug(upgrateSQL);
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
		if (value instanceof Number || value instanceof String
				|| value instanceof byte[])
			t.setField(fieldName, value);

	}

	private void checkTableExistAndColumn() {
		Cursor cursor = null;
		synchronized (mDatabase) {
			cursor = mDatabase.rawQuery(
					"SELECT sql FROM sqlite_master WHERE type = 'table' AND name = '"
							+ mTableName + "';", null);
		}
		if (cursor.moveToFirst()) {
			mTableExist = true;
			mColumn.clear();
			String createSQL = cursor.getString(0);
			mColumn = createSQLParser(createSQL);

		}

		if (cursor != null)
			cursor.close();

	}

	public boolean delete(T t) {
		if (t.hasSetId() && mTableExist && !mReadOnly) {
			String deleteSQL = "DELETE FROM table " + mTableName + " WHERE "
					+ ID_COLUMN + " = " + t._id;
			debug(deleteSQL);

			synchronized (mDatabase) {
				mDatabase.execSQL(deleteSQL);
			}

			return true;
		}
		return false;
	}

	public void drop() {
		if (mTableExist && !mReadOnly) {
			String dropSQL = "DROP TABLE " + mTableName;
			debug(dropSQL);
			synchronized (mDatabase) {
				mDatabase.execSQL(dropSQL);
			}

			mTableExist = false;
		}
	}

	/**
	 * should be positioned in the onDestory() of Activity, Service, or
	 * Appliction
	 */
	public void close() {
		if (mReadOnly) {
			if (mDatabase != null && !mDatabase.isOpen())
				mDatabase.close();

			mDatabase = null;
		} else {
			if (!MozzDB.isDBClosed())
				MozzDB.close();
		}
	}

	public void setDebug(boolean open) {
		if (open)
			mOpenDebug = true;
		else
			mOpenDebug = false;
	}

	public static Map<String, ColumnType> createSQLParser(String createSQL) {
		createSQL = createSQL.substring(createSQL.indexOf('(') + 1,
				createSQL.length() - 1);

		String[] parts = createSQL.split(",");
		HashMap<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
		for (int i = 0; i < parts.length; i++) {
			String[] singleColumn = parts[i].split(" ");
			String keys = singleColumn[0];

			int otherChar = singleColumn[1].indexOf('(');
			String typeString = singleColumn[1].toLowerCase();

			ColumnType type = null;

			if (otherChar > 0)
				typeString = singleColumn[1].substring(0, otherChar)
						.toLowerCase();

			if (typeString.equals("float") || typeString.equals("double")
					|| typeString.equals("real")) {
				type = ColumnType.TYPE_REAL;
			} else if (typeString.equals("integer")
					|| typeString.equals("short")) {
				type = ColumnType.TYPE_INTEGER;
			} else if (typeString.equals("blob")) {
				type = ColumnType.TYPE_BLOB;
			} else if (typeString.equals("text")) {
				type = ColumnType.TYPE_TEXT;
			}

			columnTypes.put(keys, type);
		}

		return columnTypes;
	}

	private void debug(String debugInfo) {
		if (mOpenDebug)
			Log.d(DEBUG_TAG, debugInfo);
	}

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;

	private Map<String, ColumnType> mColumn = new HashMap<String, ColumnType>();
	private boolean mTableExist = false;
	private boolean mReadOnly = false;
	private boolean mOpenDebug = true;

}
