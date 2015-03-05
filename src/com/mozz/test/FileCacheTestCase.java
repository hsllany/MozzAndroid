package com.mozz.test;

import android.test.AndroidTestCase;

import com.mozz.cache.FileCache;

public class FileCacheTestCase extends AndroidTestCase {
	private static final String TAG = "FileCacheTestCase";

	public void putTest() {
		FileCache fileCache = new FileCache(this.getContext());
	}

	public void getTest() {
		FileCache fileCache = new FileCache(this.getContext());
	}
}
