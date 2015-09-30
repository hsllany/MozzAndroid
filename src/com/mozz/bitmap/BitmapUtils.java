package com.mozz.bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Log;

/**
 * 
 * @author Demonknoxkyo
 * 
 */
public class BitmapUtils {

	/**
	 * 根据资源文件ID来生成bitmap
	 * 
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @param inSampleSize
	 *            if compress_ratio != -1 there is no need for reqWidth and
	 *            reqHeight
	 * @return Bitmap
	 */
	public static final synchronized Bitmap decodeBitmapFromResource(
			Context context, int resId, int reqWidth, int reqHeight,
			int inSampleSize) {
		Bitmap temp = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resId, options);

		// Calculate inSampleSize
		if (inSampleSize != -1) {
			options.inSampleSize = inSampleSize;
		} else {
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		temp = BitmapFactory.decodeResource(context.getResources(), resId,
				options);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap final_bitmap = null;
		if (temp != null) {
			temp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
			temp.recycle();
			final_bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
					baos.size());
		}
		return final_bitmap;
	}

	/**
	 * 根据文件路径来生成bitmap
	 * 
	 * @param file_name
	 * @param reqWidth
	 * @param reqHeight
	 * @param inSampleSize
	 *            if compress_ratio != -1 there is no need for reqWidth and
	 *            reqHeight
	 * @return Bitmap
	 */
	public synchronized final static Bitmap decodeBitmapFromFilePath(
			String file_name, int reqWidth, int reqHeight, int inSampleSize) {
		Bitmap temp = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file_name, options);

		// Calculate inSampleSize
		if (inSampleSize != -1) {
			options.inSampleSize = inSampleSize;
		} else {
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		options.inJustDecodeBounds = false;
		temp = BitmapFactory.decodeFile(file_name, options);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Bitmap final_bitmap = null;
		if (temp != null) {
			temp.compress(Bitmap.CompressFormat.PNG, 50, baos);
			temp.recycle();
			final_bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
					baos.size());
		}
		return final_bitmap;
	}

	/**
	 * 根据文件file来生成bitmap
	 * 
	 * @param file
	 * @param reqWidth
	 * @param reqHeight
	 * @param inSampleSize
	 *            if compress_ratio != -1 there is no need for reqWidth and
	 *            reqHeight
	 * @return Bitmap
	 */
	public synchronized final static Bitmap decodeBitmapFromFile(File file,
			int reqWidth, int reqHeight, int inSampleSize) {
		Bitmap temp = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			BitmapFactory.decodeStream(fis, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		if (inSampleSize != -1) {
			options.inSampleSize = inSampleSize;
		} else {
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		temp = BitmapFactory.decodeStream(fis);
		Bitmap final_bitmap = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (temp != null) {
			temp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
			temp.recycle();
			final_bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
					baos.size());
		}
		return final_bitmap;
	}

	/**
	 * 获取照片旋转角度
	 * 
	 * @param filePath
	 * @return degree
	 */
	public static final int getPicRotateDegree(String filePath) {
		ExifInterface exifInterface = getPicExifInterface(filePath);
		int degree = 0;
		if (exifInterface != null) {
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			default:
				break;
			}
		}
		return degree;
	}

	/**
	 * 获取图片Exif信息
	 * 
	 * @param filePath
	 * @return ExifInterface
	 */
	public static final ExifInterface getPicExifInterface(String filePath) {
		ExifInterface exifInterface = null;
		if (filePath != null && !filePath.equals("")) {
			try {
				exifInterface = new ExifInterface(filePath);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return exifInterface;
	}

	/**
	 * 根据显示屏大小计算索要生成图片的宽高以及压缩比！
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		Log.d("OOM", ">>>>" + inSampleSize);
		return inSampleSize;
	}

}
