package com.test.mozz;

import android.test.AndroidTestCase;
import android.util.Log;

import com.mozz.search.StringUtils;

public class SearchTestCase extends AndroidTestCase {
	private static final String TAG = "SearchTestCase";

	/*
	 * should return aba
	 */
	public void lcsubstringTest() {
		Log.d(TAG, "" + StringUtils.lcsubstring("abab", "baba", new String()));
	}

	public void lcsubsquenceTest() {
		Log.d(TAG,
				"" + StringUtils.lcsubsquence("abacd", "abefcad", new String()));
	}
}
