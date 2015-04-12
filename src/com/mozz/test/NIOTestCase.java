package com.mozz.test;

import java.io.File;
import java.io.FileNotFoundException;

import com.mozz.nio.file.NioFileUtils;
import com.mozz.utils.SDCard;

import android.test.AndroidTestCase;

public class NIOTestCase extends AndroidTestCase {
	public void newFileTest() throws Exception {
		NioFileUtils.newFileOnSD("hellotest", "test123.123");
	}

	public void writeSingleStringTest() throws FileNotFoundException {
		File file = new File(SDCard.sdCardDir() + File.separator + "hellotest"
				+ File.separator + "test123.123");
		NioFileUtils.writeString("dididada", file, false);
	}

	public void writeStringTest() throws Exception {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 1000; i++) {
			sb.append("hello world\r\n");
		}
		NioFileUtils.writeString(sb.toString(),
				new File(SDCard.sdCardDir() + File.separator + "hellotest"
						+ File.separator + "test123.123"), true);
	}

	public void copyTest() throws FileNotFoundException {
		File fromFile = new File(SDCard.sdCardDir() + File.separator
				+ "hellotest" + File.separator + "test123.123");
		File toFile = new File(SDCard.sdCardDir() + File.separator
				+ "hellotest" + File.separator + "test456.123");

		NioFileUtils.copy(fromFile, toFile);
	}
}
