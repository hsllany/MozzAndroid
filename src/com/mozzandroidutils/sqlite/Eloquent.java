package com.mozzandroidutils.sqlite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.mozzandroidutils.file.ObjectByte;

/**
 * Represent the table in the database.
 * 
 * @author Yang
 * 
 */
public abstract class Eloquent {

	/**
	 * default key autoincrement column name
	 */
	private final static String ID_COLUMN = "id";

	/**
	 * Create a new table with ID_COLUMN key by default.
	 * 
	 * @param tableName
	 * @param columnNames
	 * @param types
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

		Log.d("EloquentClass", createSQL);
		synchronized (database) {
			database.execSQL(createSQL);
		}

		return true;
	}

	/**
	 * @param context
	 *            , Context
	 */
	public Eloquent(Context context) {
		mDatabase = MozzDB.writebleDatabase(context);

		mModelClass = modelClass();
		if (mModelClass == null)
			throw new IllegalArgumentException(
					"must pass a no-null class in the ModelClass()");

		TableName tableAnotation = getClass().getAnnotation(TableName.class);
		if (tableAnotation != null)
			mTableName = tableAnotation.tablename();
		else {
			String className = this.getClass().getSimpleName();
			mTableName = className.substring(0, className.indexOf("Eloquent"))
					.toLowerCase();
		}

		checkTableExistAndColumn();

		mQueryBuilder = new QueryBuilder(mTableName, mDatabase, mColumn);
	}

	/**
	 * 
	 * @param context
	 *            , Context
	 * @param readOnly
	 *            , boolean, whether use a readonly database or writeble
	 *            database
	 */
	Eloquent(Context context, boolean readOnly) {
		if (readOnly) {
			mDatabase = MozzDB.readOnlyDatabase(context);
			mReadOnly = true;
		} else {
			mDatabase = MozzDB.writebleDatabase(context);
		}

		mModelClass = (Class<? extends Model>) modelClass();

		if (mModelClass == null)
			throw new IllegalArgumentException(
					"must pass a no-null class in the ModelClass()");

		TableName tableAnotation = getClass().getAnnotation(TableName.class);
		if (tableAnotation != null)
			mTableName = tableAnotation.tablename();
		else {
			String className = this.getClass().getSimpleName();
			mTableName = className.substring(0, className.indexOf("Eloquent"))
					.toLowerCase();
		}

		checkTableExistAndColumn();

		mQueryBuilder = new QueryBuilder(mTableName, mDatabase, mColumn);
	}

	abstract protected Class<? extends Model> modelClass();

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
	 * "SELECT * FROM¡¡students WHERE grade = 3"; you should invoke
	 * where("grade = 3");
	 * 
	 * @param whereSQL
	 * @return this
	 */
	public Eloquent where(String whereSQL) {
		mQueryBuilder.buildWhere(whereSQL);
		return this;
	}

	public Eloquent orderBy(String orderBy) {
		mQueryBuilder.buildOrderBy(orderBy);
		return this;
	}

	public Eloquent orderByDesc(String orderBy) {
		mQueryBuilder.buildOrderByDesc(orderBy);
		return this;
	}

	public Eloquent groupBy(String groupBy) {
		mQueryBuilder.buildGroupBy(groupBy);
		return this;
	}

	public List<Model> get() {
		try {
			if (mTableExist)
				return mQueryBuilder.get(mModelClass);
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

	public Object first() {
		try {
			if (mTableExist)
				return mQueryBuilder.first(mModelClass);
			else
				return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Model find(long id) {

		if (mTableExist) {

			Cursor cursor = null;
			Model model = null;
			try {
				cursor = mDatabase.rawQuery("SELECT * FROM " + mTableName
						+ " WHERE " + ID_COLUMN + " = " + id, null);
				if (cursor.moveToFirst()) {

					model = (Model) ObjectGenerator.newObject(mModelClass);

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
							Object obj = ObjectByte
									.toObject(cursor.getBlob(cursor
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
					model.setId(id);
				}
			} finally {
				closeCursor(cursor);
			}

			return model;

		} else {
			return null;
		}
	}

	public boolean insertMany(Collection<? extends Model> modelList)
			throws IllegalArgumentException {

		mDatabase.beginTransaction();
		try {
			for (Model model : modelList) {
				model.setId(insertUsingStatement(model));
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return false;
	}

	public boolean insertMany(Model[] modelList) {
		mDatabase.beginTransaction();
		try {
			for (int i = 0; i < modelList.length; i++) {
				Model model = modelList[i];
				model.setId(insertUsingStatement(model));
			}
			mDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			throw e;
		} finally {
			mDatabase.endTransaction();
		}

		return false;
	}

	public boolean save(Model model) {

		if (mTableExist) {
			if (mReadOnly)
				return false;

			boolean insertMode = true;
			if (model.hasSetId()) {
				Cursor cursor = null;
				try {
					synchronized (mDatabase) {
						cursor = mDatabase.rawQuery("SELECT * FROM "
								+ mTableName + " WHERE " + ID_COLUMN + " = "
								+ model.id(), null);
					}

					if (cursor.moveToFirst()) {
						int countId = cursor.getInt(0);

						if (countId > 0)
							insertMode = false;
					}

				} finally {
					closeCursor(cursor);
				}
			}

			if (insertMode) {
				synchronized (mDatabase) {
					long newId = insertUsingStatement(model);
					if (newId > 0) {
						model.setId(newId);
						return true;
					}
				}

				return false;
			} else {
				StringBuilder sb = new StringBuilder();
				Field[] allFields = mModelClass.getDeclaredFields();

				for (int i = 0; i < allFields.length; i++) {
					Field field = allFields[i];

					int modifier = field.getModifiers();
					if ((modifier & Modifier.FINAL) > 0
							|| (modifier & Modifier.STATIC) > 0
							|| (modifier & Modifier.NATIVE) > 0)
						continue;

					Ingnore annotation = field.getAnnotation(Ingnore.class);
					if (annotation != null)
						continue;

					String fieldName = field.getName();
					Object value;
					try {
						value = field.get(model);

						if (i > 0)
							sb.append(",");

						if (value != null && mColumn.containsKey(fieldName)) {
							ColumnType type = mColumn.get(fieldName);

							if (type == ColumnType.TYPE_BLOB) {
								sb.append(fieldName + "=");
								byte[] valueByte = ObjectByte
										.toByteArray(value);

								for (int j = 0; j < valueByte.length; j++) {
									sb.append(valueByte[j]);
								}

							} else {
								sb.append(fieldName + " = '" + value.toString()
										+ "'");
							}
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}

				String upgrateSQL = "UPDATE " + mTableName + " SET "
						+ sb.toString() + " WHERE " + ID_COLUMN + " = "
						+ model.id();
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
					+ ID_COLUMN + " = " + t.id();

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

	public void execSQL(String sql) {
		mDatabase.execSQL(sql);
	}

	public Cursor query(String sql) {
		return mDatabase.rawQuery(sql, null);
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
		try {
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
		} finally {
			closeCursor(cursor);
		}

	}

	private void buildInsertAndColumnOrder() {

		if (mColumnOrder == null || mInsertStatement == null) {
			mColumnOrder = new HashMap<String, Integer>();

			StringBuilder insertBuilder = new StringBuilder();
			StringBuilder valueBuilder = new StringBuilder();

			insertBuilder.append("INSERT INTO " + mTableName + " (");

			Iterator<Entry<String, ColumnType>> itr = mColumn.entrySet()
					.iterator();

			int order = 1;
			while (itr.hasNext()) {
				Entry<String, ColumnType> entry = itr.next();

				String columnName = entry.getKey();

				insertBuilder.append(columnName);
				valueBuilder.append("?");

				if (order < mColumn.size()) {
					insertBuilder.append(",");
					valueBuilder.append(",");
				}

				mColumnOrder.put(columnName, order++);

			}

			insertBuilder.append(") VALUES (" + valueBuilder.toString() + ")");
			mInsertStatement = mDatabase.compileStatement(insertBuilder
					.toString());
		}
	}

	/**
	 * Not thread safe
	 * 
	 * @param model
	 * @throws IllegalArgumentException
	 */
	private long insertUsingStatement(Model model)
			throws IllegalArgumentException {

		buildInsertAndColumnOrder();

		Field[] allFields = mModelClass.getDeclaredFields();
		mInsertStatement.clearBindings();
		for (int i = 0; i < allFields.length; i++) {
			Field field = allFields[i];

			int modifier = field.getModifiers();
			if ((modifier & Modifier.FINAL) > 0
					|| (modifier & Modifier.STATIC) > 0
					|| (modifier & Modifier.NATIVE) > 0)
				continue;

			Ingnore annotation = field.getAnnotation(Ingnore.class);
			if (annotation != null)
				continue;

			String fieldName = field.getName();
			Object value;
			try {
				value = field.get(model);

				ColumnType type = mColumn.get(fieldName);
				int order = mColumnOrder.get(fieldName);

				switch (type) {
				case TYPE_REAL:
				case TYPE_INTEGER:
					if (value instanceof Number)
						if (type == ColumnType.TYPE_INTEGER)
							mInsertStatement.bindLong(order,
									((Number) value).longValue());
						else if (type == ColumnType.TYPE_REAL)
							mInsertStatement.bindDouble(order,
									((Number) value).doubleValue());
						else
							throw new IllegalArgumentException(fieldName
									+ " column must be Numbers");
					break;

				case TYPE_TEXT:
					mInsertStatement.bindString(order, value.toString());
					break;
				case TYPE_BLOB:
					mInsertStatement.bindBlob(order,
							ObjectByte.toByteArray(value));
					break;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return mInsertStatement.executeInsert();
	}

	/**
	 * should be positioned in the onDestory() of Activity, Service, or
	 * Appliction
	 */
	public void close() {
		if (mReadOnly) {
			if (mDatabase != null && mDatabase.isOpen())
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

	private void closeCursor(Cursor cursor) {
		if (cursor != null)
			cursor.close();
		cursor = null;
	}

	protected String mTableName = null;
	private SQLiteDatabase mDatabase;
	private QueryBuilder mQueryBuilder;

	private Map<String, ColumnType> mColumn = new HashMap<String, ColumnType>();
	private boolean mTableExist = false;
	private boolean mReadOnly = false;

	// for insertion
	private Map<String, Integer> mColumnOrder = null;
	private SQLiteStatement mInsertStatement = null;
	private Class<? extends Model> mModelClass = null;
}
