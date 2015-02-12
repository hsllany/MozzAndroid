package com.mozzandroidutils.sqlite;

import java.util.ArrayList;
import java.util.List;

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

		sb.append("create table if not exists " + tableName.toLowerCase());
		sb.append(" (" + ID_COLUMN + " integer primary key autoincrement, ");

		for (int i = 0; i < columnNames.length; i++) {
			String type = typeToString(types[i]);

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

	private static String typeToString(ColumnType type) {
		switch (type) {
		case TYPE_REAL:
			return "REAL";
		case TYPE_INTEGER:
			return "INTEGER";
		case TYPE_TEXT:
			return "TEXT";
		case TYPE_BLOB:
			return "BLOB";
		default:
			break;
		}

		return null;
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
				debug("SELECT * FROM " + mTableName + " order by " + ID_COLUMN
						+ " desc");
				Cursor cursor = mDatabase
						.rawQuery("SELECT * FROM " + mTableName + " order by "
								+ ID_COLUMN + " desc", null);
				return cursor;
			} else {
				debug("SELECT * FROM " + mTableName + "order by " + ID_COLUMN);
				Cursor cursor = mDatabase.rawQuery("SELECT * FROM "
						+ mTableName + "order by " + ID_COLUMN, null);
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
		debug(selectSQL);
		return mDatabase.rawQuery(selectSQL, null);
	}

	public T Find(int id, T t) {
		if (mTableExist) {
			debug("SELECT * FROM " + mTableName + " where " + ID_COLUMN + " = "
					+ id);
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
		if (mTableExist) {
			boolean insertMode = true;
			if (t.hasSetId()) {
				Cursor cursor = null;
				debug("SELECT * FROM " + mTableName + " where " + ID_COLUMN
						+ " = " + t.id());
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
					if (value != null && mColumn.contains(fieldName)) {
						sb.append(fieldName);
						valueSb.append("'" + value.toString() + "'");

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
					if (value != null && mColumn.contains(fieldName)) {
						sb.append(fieldName + " = '" + value.toString() + "'");

						if (i < fieldLength - 1) {
							sb.append(",");
						}
					}
				}

				String upgrateSQL = "update " + mTableName + " set "
						+ sb.toString() + " where " + ID_COLUMN + " = " + t._id;
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
			int columnCount = cursor.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				String columnName = cursor.getColumnName(i);
				synchronized (mColumn) {
					mColumn.add(columnName);
				}
			}

			if (cursor != null)
				if (!cursor.isClosed())
					cursor.close();
		}
	}

	public boolean delete(T t) {
		if (t.hasSetId() && mTableExist) {
			String deleteSQL = "delete from table " + mTableName + " where "
					+ ID_COLUMN + " = " + t._id;

			synchronized (mDatabase) {
				mDatabase.execSQL(deleteSQL);
			}

			return true;
		}
		return false;
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

	/**
	 * should be positioned in the onDestory() of Activity, Service, or
	 * Appliction
	 */
	public void onDestory() {
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

	private void debug(String debugInfo) {
		if (mOpenDebug)
			Log.d(DEBUG_TAG, debugInfo);
	}

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;

	private List<String> mColumn = new ArrayList<String>();
	private boolean mTableExist = false;

	private boolean mReadOnly = false;

	private boolean mOpenDebug = true;

}
