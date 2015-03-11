package com.mozz.file;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class MozzConfig {
	/**
	 * Read the App File Dir from manifest. if you should use this, please set
	 * metadata inside the Application node. Example:
	 * 
	 * <Application android:name=""...
	 * 
	 * <meta-data android:name="YD_APP_DIR" android:value="Some dir you set" />
	 * 
	 * </Application>
	 * 
	 * @param context
	 *            , Context
	 * @return AppDir, String
	 */
	public static String getAppAbsoluteDir(Context context) {
		return SDCard.sdCardDir()
				+ (String) getMetaData(context, "MOZZ_APP_DIR")
				+ File.separator;
	}

	public static void makeAppDirs(Context context) {
		File file = new File(getAppAbsoluteDir(context));
		if (!file.exists())
			file.mkdirs();
		File cacheFile = new File(getAppAbsoluteDir(context) + "/cache");
		if (!cacheFile.exists())
			cacheFile.mkdirs();
		File updateFile = new File(getAppAbsoluteDir(context) + "/update");
		if (!updateFile.exists())
			updateFile.mkdirs();

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
		return (String) getMetaData(context, "MOZZ_DB_NAME");
	}

	private static Object getMetaData(Context context, String key) {
		Object metaData = null;

		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			Bundle bundle = info.metaData;

			if (bundle != null)
				metaData = (String) bundle.get(key);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return metaData;
	}

	public static int getPackageVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static String getPackageVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDeviceId(Context context) {
		final TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

}
