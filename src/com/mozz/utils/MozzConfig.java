package com.mozz.utils;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class MozzConfig {

	private static String appCacheDir;
	private static String appUpdateDir;
	private static String appLogDir;

	/**
	 * Read the App File Dir from manifest. if you should use this, please set
	 * metadata inside the Application node. Example:
	 * 
	 * <Application android:name=""...
	 * 
	 * <meta-data android:name="MOZZ_APP_DIR" android:value="Some dir you set"
	 * />
	 * 
	 * </Application>
	 * 
	 * @param context
	 *            , Context
	 * @return AppDir, String
	 */
	public static String getAppAbsoluteDir(Context context) {
		try {
			return SDCard.sdCardDir()
					+ (String) getMetaData(context, "MOZZ_APP_DIR")
					+ File.separator;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAppAbsoluteDir(String dirPath) {
		return SDCard.sdCardDir() + dirPath + File.separator;
	}

	public static String getAppCacheDir() {
		return appCacheDir;
	}

	public static String getAppUpdateDir() {
		return appUpdateDir;
	}

	public static String getAppLogDir() {
		return appLogDir;
	}

	public static void makeAppDirs(String fileDir) {
		File cacheFile = new File(getAppAbsoluteDir(fileDir) + "/cache");
		appCacheDir = cacheFile.getPath();
		if (!cacheFile.exists())
			cacheFile.mkdirs();
		File updateFile = new File(getAppAbsoluteDir(fileDir) + "/update");
		appUpdateDir = updateFile.getPath();
		if (!updateFile.exists())
			updateFile.mkdirs();
		File logFile = new File(getAppAbsoluteDir(fileDir) + "/log");
		appLogDir = logFile.getPath();
		if (!logFile.exists())
			logFile.mkdirs();
	}

	public static void makeAppDirs(Context context) {
		String appDir = getAppAbsoluteDir(context);
		makeAppDirs(appDir);
	}

	/**
	 * Read the DB Dir from manifest. if you should use this, please set
	 * metadata inside the Application node. Example:
	 * 
	 * <Application android:name=""...
	 * 
	 * <meta-data android:name="YD_DB_NAME" android:value="Some dir you set" />
	 * 
	 * </Application>
	 * 
	 * @see getAppDir()
	 * 
	 * @param context
	 *            , Context
	 * @return AppDir, String
	 */
	public static String getDBDir(Context context) {
		try {
			return (String) getMetaData(context, "MOZZ_DB_NAME");
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean inDebug(Context context) {
		String debugOpen;
		try {
			debugOpen = (String) getMetaData(context, "MOZZ_DEBUG");
			if (debugOpen.equalsIgnoreCase("true"))
				return true;
			return false;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String getMetaData(Context context, String key)
			throws NameNotFoundException {
		String metaData = null;

		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			Bundle bundle = info.metaData;

			if (bundle != null)
				metaData = (String) bundle.get(key);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			throw e;
		}

		return metaData;
	}
}
