package com.ydandroidutils.file;

import java.io.File;

import android.os.Environment;

public class SDCard {
	public static String sdCardDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				.toString()
				+ File.separator;
	}
}
