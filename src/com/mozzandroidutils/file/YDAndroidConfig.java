package com.mozzandroidutils.file;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class YDAndroidConfig {
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
	public static String getAppDir(Context context) {
		return (String) getMetaData(context, "YD_APP_DIR");
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
		return (String) getMetaData(context, "YD_DB_NAME");
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
}
