package com.mozz.utils;

public class Log {
	private String mLogTag;

	public Log(Class<?> clazz) {
		mLogTag = clazz.getSimpleName();
	}

	public Log(String logTag) {
		mLogTag = logTag;
	}

	public void d(String log) {
		android.util.Log.d(mLogTag, log);
	}

	public void w(String log) {
		android.util.Log.w(mLogTag, log);
	}

	public void wtf(String log) {
		android.util.Log.wtf(mLogTag, log);
	}

	public void e(String log) {
		android.util.Log.e(mLogTag, log);
	}

	public void i(String log) {
		android.util.Log.i(mLogTag, log);
	}
}
