package com.mozz.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.mozz.file.MozzConfig;

public class MozzConfigTestCase extends AndroidTestCase {
	private static final String Tag = "MozzConfigTestCase";

	public void makeAppDirTest() throws Throwable {
		Context context = this.mContext;
		MozzConfig.makeAppDirs(context);
	}
}
