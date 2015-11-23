package com.mozz.utils;

import java.lang.reflect.Method;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * 
 * @author demonknoxkyo, hsllany
 * @version 1.1
 */
public class SystemInfo {
	/**
	 * check the mobile data
	 * 
	 * @param context
	 * @return true, if mobile data on
	 */
	public static boolean isMobileDataOn(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectManager.getActiveNetworkInfo();
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/**
	 * open mobile data
	 * 
	 * @param context
	 */
	public static void openMobileData(Context context) {
		if (!isMobileDataOn(context))
			setMobileDataEnabled(true, context);
	}

	/**
	 * close mobile data
	 * 
	 * @param context
	 */
	public static void closeMobileData(Context context) {
		if (isMobileDataOn(context))
			setMobileDataEnabled(false, context);
	}

	/**
	 * set mobile data
	 * 
	 * @param enable
	 *            , open mobile data if true
	 * @param context
	 */
	private static void setMobileDataEnabled(boolean enable, Context context) {
		ConnectivityManager connectivityManager = null;
		try {
			connectivityManager = (ConnectivityManager) context
					.getSystemService("connectivity");
			Class<?> connectivityManagerClazz = connectivityManager.getClass();
			Method method = connectivityManagerClazz.getMethod(
					"setMobileDataEnabled", new Class<?>[] { boolean.class });
			method.invoke(connectivityManager, enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * return if wifi open
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiOpen(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return manager.isWifiEnabled();
	}

	/**
	 * return if wifi scanning always available
	 * 
	 * @param context
	 * @return
	 */
	public static boolean scanningWifiAlwaysAvailable(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return manager.isScanAlwaysAvailable();
	}

	/**
	 * jump to wifi setting activity
	 * 
	 * @param activity
	 */
	public static void toWifiSetting(Activity activity) {

		if (activity != null) {
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}

	/**
	 * open wifi
	 * 
	 * @param context
	 */
	public static void openWifi(Context context) {
		if (!isWifiOpen(context))
			setWifiEnabled(true, context);
	}

	/**
	 * close wifi
	 * 
	 * @param context
	 */
	public static void closeWifi(Context context) {
		if (isWifiOpen(context))
			setWifiEnabled(false, context);
	}

	/**
	 * set wifi enabled
	 * 
	 * @param enable
	 *            , open wifi if true
	 * @param context
	 */
	private static void setWifiEnabled(boolean enable, Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!manager.isWifiEnabled()) {
			manager.setWifiEnabled(enable);
		}
	}

	/**
	 * is gps switcher on
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isGpsOn(Context context) {
		ContentResolver resolver = context.getContentResolver();
		@SuppressWarnings("deprecation")
		boolean open = Settings.Secure.isLocationProviderEnabled(resolver,
				LocationManager.GPS_PROVIDER);
		return open;
	}

	/**
	 * to gps settings
	 * 
	 * @param activity
	 */
	public static void toGpsSetting(Activity activity) {
		if (activity != null) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}

	/**
	 * get bluetooth adpater
	 * 
	 * @param context
	 * @return bluetooth adpater
	 */
	@SuppressLint("InlinedApi")
	private static BluetoothAdapter getBluetoothAdapter(Context context) {
		BluetoothAdapter adapter = null;
		if (getSDKVersion() >= 18) {
			BluetoothManager manager = (BluetoothManager) context
					.getSystemService(Context.BLUETOOTH_SERVICE);
			adapter = manager.getAdapter();

		} else {
			adapter = BluetoothAdapter.getDefaultAdapter();
		}

		return adapter;
	}

	/**
	 * bluetooth status
	 * 
	 * @param context
	 * @return true if bluetooth open
	 */
	public static boolean isBluetoothOpen(Context context) {
		BluetoothAdapter adapter = getBluetoothAdapter(context);

		if (adapter != null) {
			return adapter.isEnabled();
		}

		return false;
	}

	/**
	 * open bluetooth
	 * 
	 * @param context
	 * @return
	 */
	public static boolean openBluetooth(Context context) {
		BluetoothAdapter adapter = getBluetoothAdapter(context);

		if (adapter != null)
			return adapter.enable();

		return false;
	}

	/**
	 * close bluetooth
	 * 
	 * @param context
	 * @return
	 */
	public static boolean closeBluetooth(Context context) {
		BluetoothAdapter adapter = getBluetoothAdapter(context);

		if (adapter != null)
			return adapter.disable();

		return false;
	}

	/**
	 * to bluetooth setting activity
	 * 
	 * @param activity
	 */
	public static void toBluetoothSetting(Activity activity) {
		if (activity != null) {
			Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}

	/**
	 * get device id
	 * 
	 * @param context
	 * @return device id
	 */
	public static String getDeviceId(Context context) {
		final TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

	/**
	 * get Phone model
	 * 
	 * @return
	 */
	public static String getDeviceModel() {
		return Build.MODEL;
	}

	/**
	 * sdk version
	 * 
	 * @return
	 */
	public static int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * Retrieve phone num. Not garentee
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNum(Context context) {
		final TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getLine1Number();
	}

	/**
	 * get current time
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		month += 1;
		int day = Calendar.getInstance().get(Calendar.DATE);

		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int min = Calendar.getInstance().get(Calendar.MINUTE);
		int second = Calendar.getInstance().get(Calendar.SECOND);

		String d = year + "-" + month + "-" + day + " " + hour + ":" + min
				+ ":" + second;

		return d;
	}

	/**
	 * get package version
	 * 
	 * @param context
	 * @return package version
	 */
	public static int getPackageVersionCode(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String getPackageVersionName(Context context) {
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getDeviceMAC(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String macAddress = wInfo.getMacAddress();

		return macAddress;
	}


}
