package com.mozz.sqlite;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mozz.file.MozzConfig;

public class MozzDB {
	private static SQLiteDatabase mDatabase;

	private static AtomicInteger databaseInstanceNum = new AtomicInteger(0);

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
			databaseInstanceNum.incrementAndGet();
			return mDatabase;
		}
		return null;
	}

	static void close() {
		databaseInstanceNum.decrementAndGet();
		if (databaseInstanceNum.get() == 0) {
			closeDB();
		}
	}

	private static void closeDB() {
		if (mDatabase != null && mDatabase.isOpen())
			mDatabase.close();
		mDatabase = null;
	}

	static boolean isDBClosed() {
		if (mDatabase == null)
			return true;
		else
			return !mDatabase.isOpen();
	}
}
