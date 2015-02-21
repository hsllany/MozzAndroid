package com.mozzandroidutils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mozzandroidutils.file.MozzConfig;

public class MozzDB {
	private static SQLiteDatabase mDatabase;

	private static int databaseInstanceNum = 0;

	static SQLiteDatabase writebleDatabase(Context context) {
		return writebleDB(context);
	}

	static SQLiteDatabase readOnlyDatabase(Context context) {
		String dbName = MozzConfig.getDBDir(context);

		String path = context.getDatabasePath(dbName).getPath();

		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		return database;
	}

	private static SQLiteDatabase writebleDB(Context context) {

		String dbName = MozzConfig.getDBDir(context);
		if (dbName != null) {

			if (mDatabase != null) {
				if (!mDatabase.isOpen()) {
					mDatabase = null;
				}
			}

			if (mDatabase == null) {
				mDatabase = context.openOrCreateDatabase(dbName,
						Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null, null);
				mDatabase.enableWriteAheadLogging();
			}
			databaseInstanceNum++;
			return mDatabase;
		}
		return null;
	}

	static void close() {
		databaseInstanceNum--;

		if (databaseInstanceNum == 0) {
			closeDB();
		}
	}

	private static void closeDB() {
		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		} else if (!mDatabase.isOpen()) {
			mDatabase = null;
		}
	}

	static boolean isDBClosed() {
		if (mDatabase == null)
			return true;
		else
			return !mDatabase.isOpen();
	}
}
