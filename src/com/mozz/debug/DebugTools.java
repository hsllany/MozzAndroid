package com.mozz.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class DebugTools {
	
	private String PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath().toString()
			+ "/ShiTuTool/Log/";
	
	private String debug_head;

	public DebugTools() {

	}

	public boolean startLogs(String path, String content) {

		if (path != null) {
			if (!path.equals("")) {
				PATH = path;
			}
		}

		if (PATH != null) {
			if (!PATH.equals("")) {
				File path_file = new File(PATH);
				if (!path_file.exists()) {
					path_file.mkdirs();
				}
				File file = new File(PATH + "log.txt");
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
			return false;
		}
		return false;
	}
	
	public void set_debug_head(String head){
		this.debug_head = head;
	}
	
	public void debug_log(String value){
		Log.d(this.debug_head, value);
	}
}
