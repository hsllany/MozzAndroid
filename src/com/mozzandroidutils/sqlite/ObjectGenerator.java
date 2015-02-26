package com.mozzandroidutils.sqlite;

import java.lang.reflect.Field;

import android.util.Log;

public class ObjectGenerator {
	public static Object newObject(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static boolean set(Object object, String fieldName, Object value) {
		Class<?> objectClazz = object.getClass();
		try {
			Field field = objectClazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			try {
				field.set(object, value);
				return true;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			return false;
		} catch (NoSuchFieldException e1) {
			Log.w("ObjectGenerator", "no such field of "
					+ object.getClass().getSimpleName() + ", " + fieldName);
			return false;
		}
	}

}
