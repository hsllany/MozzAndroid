package com.mozz.test;

import com.mozz.search.StringUtils;

import android.test.AndroidTestCase;
import android.util.Log;

public class SearchTestCase extends AndroidTestCase {
	private static final String TAG = "SearchTestCase";

	public void searchTest() {
		Log.d(TAG, "" + StringUtils.lcs("a", "ab"));
	}
}
