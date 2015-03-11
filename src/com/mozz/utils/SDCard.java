package com.mozz.utils;

import java.io.File;

import android.os.Environment;

public class SDCard {
	public static String sdCardDir() {
		if (isSDCardMounted())
			return Environment.getExternalStorageDirectory().getAbsolutePath()
					.toString()
					+ File.separator;

		return null;
	}

	public static boolean isSDCardMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
}
