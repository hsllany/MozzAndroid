package com.mozz.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import android.content.Context;

import com.mozz.file.MozzConfig;
import com.mozz.file.ObjectByte;

public class FileCache implements Cache {
	private File mCacheDir;
	private Context mContext;

	public FileCache(Context context, String path) {
		mContext = context;
		if (path == null)
			throw new IllegalArgumentException("path cannot be null");

		mCacheDir = new File(path);

		if (!mCacheDir.exists())
			mCacheDir.mkdir();
	}

	public FileCache(Context context) {
		mContext = context;

		mCacheDir = new File(MozzConfig.getAppAbsoluteDir(context) + "/cache");
		MozzConfig.makeAppDirs(mContext);
	}

	@Override
	public Object get(String key) {
		File file = new File(mCacheDir, key.hashCode() + "");

		if (!file.exists())
			return null;

		else {
			byte[] objectBinary = inputStreamFromFile(file);
			return ObjectByte.toObject(objectBinary);
		}
	}

	@Override
	public void put(String key, Serializable item) {
		File file = new File(mCacheDir, key.hashCode() + "");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(ObjectByte.toByteArray(item));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void clear() {
	}

	@Override
	public boolean remove(String key) {
		File file = new File(mCacheDir, key.hashCode() + "");
		return file.delete();
	}

	private static byte[] inputStreamFromFile(File file) {
		RandomAccessFile randomFile = null;
		try {
			if (!file.exists())
				return null;
			randomFile = new RandomAccessFile(file, "r");
			byte[] byteArray = new byte[(int) randomFile.length()];
			randomFile.read(byteArray);
			return byteArray;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
