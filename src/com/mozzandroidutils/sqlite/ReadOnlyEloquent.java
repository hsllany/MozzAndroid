package com.mozzandroidutils.sqlite;

import android.content.Context;

public class ReadOnlyEloquent<T extends Model> extends Eloquent<T> {

	public ReadOnlyEloquent(Context context) {
		super(context, true);
	}

	public ReadOnlyEloquent(Context context, String tableName) {
		super(context, tableName, true);
	}

}
