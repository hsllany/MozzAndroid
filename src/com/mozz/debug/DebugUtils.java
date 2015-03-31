package com.mozz.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mozz.utils.MozzConfig;

import android.os.Environment;
import android.util.Log;

public class DebugUtils {

	/**
	 * use this method first use MozzConfig create your log dir!
	 * @param log content mode append
	 * @return
	 */
	public static final boolean startLogs(String content) {

		File path_file = new File(MozzConfig.getAppLogDir());
		if (!path_file.exists()) {
			path_file.mkdirs();
		}
		File file = new File(path_file.getPath() + File.separator + "log.txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
