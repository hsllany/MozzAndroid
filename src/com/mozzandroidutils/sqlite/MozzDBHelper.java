package com.mozzandroidutils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mozzandroidutils.file.MozzConfig;

public class MozzDBHelper {
	private static SQLiteDatabase mDatabase;

	public static SQLiteDatabase DBInstance(Context context) {
		return DB(context);
	}

	private static SQLiteDatabase DB(Context context) {
		String dbName = MozzConfig.getDBDir(context);
		if (dbName != null) {
			if (mDatabase == null || !mDatabase.isOpen())
				mDatabase = context.openOrCreateDatabase(dbName,
						Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null, null);
			return mDatabase;
		}
		return null;
	}

	public static void createTable(String createSQL, Context context) {
		SQLiteDatabase database = DBInstance(context);
		if (database != null)
			database.execSQL(createSQL);
	}

	public static void close() {
		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}
}
