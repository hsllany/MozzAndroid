package com.test.mozz;

import java.io.Serializable;

import android.test.AndroidTestCase;
import android.util.Log;

import com.mozz.utils.ObjectByte;

public class FileCacheTestCase extends AndroidTestCase {
	private static final String TAG = "FileCacheTestCase";

	public void putTest() {

		Player test = new Player();
		ObjectByte.toByteArray(test);
		Log.d(TAG, (test instanceof Serializable) + "");

		// FileCache cache = FileCache.instance(this.getContext());
		//
		// cache.putWithExpireTime("hello", new TestObject(), 30000,
		// new PutCallback() {
		//
		// @Override
		// public void onSuccess() {
		// Log.d(TAG, "success");
		//
		// }
		//
		// @Override
		// public void onFail() {
		// Log.d(TAG, "failed");
		//
		// }
		//
		// });
	}

	public void getTest() {
	}

	class Player implements java.io.Serializable {
		private static final long serialVersionUID = 1L;

		int id = 3;
	}

}
