package com.mozz.sqlite;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mozz.utils.MozzConfig;

public class MozzDB {
	private static SQLiteDatabase mDatabase;

	private static AtomicInteger databaseInstanceNum = new AtomicInteger(0);

	static SQLiteDatabase writebleDatabase(Context context, String dbName) {
		SQLiteDatabase db = writebleDB(context, dbName);
		databaseInstanceNum.incrementAndGet();

		return db;
	}

	static SQLiteDatabase writebleDB(Context context, String dbName) {
		if (dbName == null)
			dbName = MozzConfig.getDBDir(context);
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

			return mDatabase;
		} else {
			throw new IllegalArgumentException("dbName can't be null");
		}

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
