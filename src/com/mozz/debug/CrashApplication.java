package com.mozz.debug;

import android.app.Application;

import com.mozz.debug.CrashHandler.CrashingListener;

public class CrashApplication extends Application implements CrashingListener {

	CrashHandler crashHandler = null;

	@Override
	public void onCreate() {
		super.onCreate();
		crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext(),this);
	}

	protected void setCrashHandlerPath(String path) {
		crashHandler.setCrashPath(path);
	}
	
	protected void sendCrashLogToServer(String fileName){};

	@Override
	public void onCrashLogSaved(String fileName) {
		sendCrashLogToServer(fileName);
	}
	
}
