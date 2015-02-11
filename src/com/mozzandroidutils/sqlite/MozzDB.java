package com.mozzandroidutils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mozzandroidutils.file.MozzConfig;

public class MozzDB {
	private static SQLiteDatabase mDatabase;

	public static SQLiteDatabase database(Context context) {
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

	public static void close() {
		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		} else if (!mDatabase.isOpen()) {
			mDatabase = null;
		}
	}
}
