package com.mozzandroidutils.sqlite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mozzandroidutils.file.ObjectByte;

/**
 * 代表数据库中的表
 * 
 * @author Yang
 * 
 * @param <T>, 必须是Model类继承
 */
public abstract class Eloquent {

	private String DEBUG_TAG = this.getClass().getSimpleName();

	private final static String ID_COLUMN = "id";

	public static enum ORDER {
		DESC, ASC
	};

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

	public Eloquent all() {
		if (mTableExist) {
			mQueryBuilder.buildSelect("*");
		}

		return this;
	}

	public Eloquent select(String selectSQL) {
		mQueryBuilder.buildSelect(selectSQL);
		return this;
	}

	/**
	 * if you want to do the following query:
	 * "SELECT * FROM　students WHERE grade = 3"; you should invoke
	 * where("grade = 3");
	 * 
	 * @param whereSQL
	 * @return this
	 */
	public Eloquent where(String whereSQL) {
		mQueryBuilder.buildWhere(whereSQL);
		return this;
	}

	public Eloquent where(String[] keys, Object[] values) {

		if (keys.length != values.length || !mTableExist)
			return null;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			sb.append(" " + keys[i] + " = '" + values[i] + "' ");
			if (i < keys.length - 1)
				sb.append("AND");
		}

		mQueryBuilder.buildWhere(sb.toString());
		return this;
	}

	public Eloquent orderBy(String orderBy) {
		mQueryBuilder.buildOrderBy(orderBy);
		return this;
	}

	public Eloquent groupBy(String groupBy) {
		mQueryBuilder.buildGroupBy(groupBy);
		return this;
	}

	public List<Model> get() {
		try {
			if (mTableExist)
				return mQueryBuilder.get();
			else
				return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int count() {
		try {
			return mQueryBuilder.count();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public Model first() {
		try {
			if (mTableExist)
				return mQueryBuilder.first();
			else
				return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Model find(int id) {

		if (mTableExist) {

			Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
					+ " WHERE " + ID_COLUMN + " = " + id, null);

			if (cursor.moveToFirst()) {

				Model model = new Model();

				int columnCount = cursor.getColumnCount();

				for (int i = 0; i < columnCount; i++) {
					String columnName = cursor.getColumnName(i);
					int columnType = cursor.getType(cursor
							.getColumnIndex(columnName));

					switch (columnType) {
					case Cursor.FIELD_TYPE_INTEGER:
						setField(model, columnName, cursor.getInt(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_BLOB:
						Object obj = ObjectByte.toObject(cursor.getBlob(cursor
								.getColumnIndex(columnName)));
						setField(model, columnName, obj);
						break;

					case Cursor.FIELD_TYPE_FLOAT:
						setField(model, columnName, cursor.getFloat(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_STRING:
						setField(model, columnName, cursor.getString(cursor
								.getColumnIndex(columnName)));
						break;

					case Cursor.FIELD_TYPE_NULL:
						setField(model, columnName, null);
						break;
					}
				}
				model.id = id;
				model.mTable = mTableName;
				return model;
			}

			return null;

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

		mQueryBuilder = new QueryBuilder(mTableName, mDatabase, mColumn);
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

		mQueryBuilder = new QueryBuilder(mTableName, mDatabase, mColumn);
	}

	public Eloquent(Context context, String tableName) {
		this(context);

		mTableName = tableName.toLowerCase();

		mQueryBuilder.changeTableName(mTableName);
	}

	Eloquent(Context context, String tableName, boolean readOnly) {
		this(context, readOnly);

		mTableName = tableName.toLowerCase();

		mQueryBuilder.changeTableName(mTableName);
	}

	// TODO
	public boolean saveAll(List<Model> modelList) {
		// SQLiteStatement insertStatement = mDatabase.compileStatement(sql);

		// if (mTableExist) {
		// if (mReadOnly)
		// return false;
		// }
		return false;
	}

	public boolean save(Model model) {

		if (mTableExist) {
			if (mReadOnly)
				return false;

			boolean insertMode = true;
			if (model.hasSetId()) {
				Cursor cursor = null;
				synchronized (mDatabase) {
					cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
							+ " WHERE " + ID_COLUMN + " = " + model.id(), null);
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
				sb.append("INSERT INTO " + mTableName + "(");
				valueSb.append(") values(");

				Set<Entry<String, Object>> entrySet = model.fieldsAndValues();
				Iterator<Entry<String, Object>> it = entrySet.iterator();

				int i = 0;
				while (it.hasNext()) {
					if (i > 0) {
						sb.append(",");
						valueSb.append(",");
					}
					Entry<String, Object> entry = it.next();
					String fieldName = entry.getKey();
					Object value = entry.getValue();

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
					}

					i++;
				}

				String sqlInsert = sb.toString() + valueSb.toString() + ")";
				Cursor lastInsertIdCursor = null;
				synchronized (mDatabase) {
					mDatabase.execSQL(sqlInsert);
					lastInsertIdCursor = mDatabase.rawQuery(
							"select last_insert_rowid();", null);
				}

				if (lastInsertIdCursor != null) {
					if (lastInsertIdCursor.moveToFirst()) {
						model.id = lastInsertIdCursor.getInt(1);
					}

					lastInsertIdCursor.close();
					return true;
				}

				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				Set<Entry<String, Object>> entrySet = model.fieldsAndValues();
				Iterator<Entry<String, Object>> it = entrySet.iterator();

				int i = 0;

				while (it.hasNext()) {

					if (i > 0)
						sb.append(",");

					Entry<String, Object> entry = it.next();
					String fieldName = entry.getKey();
					Object value = entry.getValue();

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

					}
				}

				String upgrateSQL = "UPDATE " + mTableName + " SET "
						+ sb.toString() + " WHERE " + ID_COLUMN + " = "
						+ model.id;
				synchronized (mDatabase) {
					mDatabase.execSQL(upgrateSQL);
				}

				return true;
			}

		} else {
			return false;
		}
	}

	public boolean delete(Model t) {
		if (t.hasSetId() && mTableExist && !mReadOnly) {
			String deleteSQL = "DELETE FROM table " + mTableName + " WHERE "
					+ ID_COLUMN + " = " + t.id;

			synchronized (mDatabase) {
				mDatabase.execSQL(deleteSQL);
			}

			return true;
		}
		return false;
	}

	public boolean delete() {
		return mQueryBuilder.delete();
	}

	public void dropTable() {
		if (mTableExist && !mReadOnly) {
			String dropSQL = "DROP TABLE " + mTableName;
			synchronized (mDatabase) {
				mDatabase.execSQL(dropSQL);
			}

			mTableExist = false;
		}
	}

	private void setField(Model t, String fieldName, Object value)
			throws IllegalArgumentException {
		if (value == null)
			return;

		if (value instanceof Number || value instanceof String
				|| value instanceof byte[])
			t.set(fieldName, value);
		else
			throw new IllegalArgumentException(
					"value must be instance of Number, String, or byte[]");

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

	public void setDebug(boolean openDebug) {
		mQueryBuilder.setDebugMode(openDebug);
	}

	private static Map<String, ColumnType> createSQLParser(String createSQL) {
		createSQL = createSQL.substring(createSQL.indexOf('(') + 1,
				createSQL.length() - 1);

		String[] parts = createSQL.split(",");
		HashMap<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
		for (int i = 0; i < parts.length; i++) {
			String[] singleColumn = parts[i].split(" ");
			int j = 0;
			while (singleColumn[j].equals(" ") || singleColumn[j].equals("")) {
				j++;
			}
			String keys = singleColumn[j];

			int otherChar = singleColumn[j + 1].indexOf('(');
			String typeString = singleColumn[j + 1].toLowerCase();

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

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;

	private QueryBuilder mQueryBuilder;

	private Map<String, ColumnType> mColumn = new HashMap<String, ColumnType>();
	private boolean mTableExist = false;
	private boolean mReadOnly = false;

}
