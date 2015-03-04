package com.mozz.test;

import android.test.AndroidTestCase;

import com.mozz.cache.FileCache;

public class FileCacheTestCase extends AndroidTestCase {
	public void putTest() {
		FileCache fileCache = new FileCache(this.getContext());
		fileCache.put("hello world", new String("1234"));
	}
}
