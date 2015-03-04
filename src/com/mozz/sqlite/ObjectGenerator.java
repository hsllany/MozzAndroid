package com.mozz.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.util.Log;

public class ObjectGenerator {
	public static <T> T newObject(Class<T> clazz) {
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
			int modifier = field.getModifiers();
			if ((modifier & Modifier.FINAL) > 0
					|| (modifier & Modifier.STATIC) > 0
					|| (modifier & Modifier.NATIVE) > 0)
				return false;

			Ingnore annotation = field.getAnnotation(Ingnore.class);
			if (annotation != null)
				return false;

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
