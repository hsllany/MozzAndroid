package com.mozzandroidutils.http;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

public class NetIndicator {
	public static boolean isWifiOpen(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return manager.isWifiEnabled();
	}

	public static void setMobileDataEnabled(boolean enable, Context context) {
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

	public static void setWifiEnabled(boolean enable, Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (!manager.isWifiEnabled()) {
			manager.setWifiEnabled(enable);
		}
	}
}
