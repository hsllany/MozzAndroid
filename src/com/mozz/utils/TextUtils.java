package com.mozz.utils;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**   
 * @function:
 * @author ZhangDao
 * @file_name TextUtils.java
 * @package_name£ºcom.mozz.utils
 * @project_name£ºMozzAndroidUtils
 * @department£ºAndroidÑÐ·¢
 * @date 2015-4-20 ÏÂÎç4:40:54 
 * @version V1.0   
 */
public class TextUtils {

	public static float getTextWidth(Paint paint, String text) {
		if (paint != null && text != null && !text.equals("")) {
			float textWidth = paint.measureText(text);
			return textWidth;
		}
		return 0f;
	}

	public static float getTextHeight(Paint paint) {
		if (paint != null) {
			FontMetrics fm = paint.getFontMetrics();
			float ascent = Math.abs(fm.ascent);
			return ascent;
		}
		return 0f;
	}
}


