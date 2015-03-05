package com.mozz.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import android.content.Context;
import android.os.AsyncTask;

import com.mozz.file.MozzConfig;
import com.mozz.file.ObjectByte;

public class FileCache implements Cache {

	private final static long DEFAULT_KEEP_TIME = 60000;

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
	public void get(String key, GetCallback callback) {
		new GetAsynTask(key, callback).execute();
	}

	public void put(String key, Serializable item, PutCallback callback) {
		put(key, item, DEFAULT_KEEP_TIME, callback);
	}

	@Override
	public void put(String key, Serializable item, long duration,
			PutCallback callback) {
		new PutAsynTask(item, key, duration, callback).execute();
	}

	@Override
	public void clear() {
		File[] files = mCacheDir.listFiles();
		for (File file : files) {
			file.delete();
		}
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

	private class PutAsynTask extends AsyncTask<Void, Void, Void> {
		private Serializable mObject;
		private long mExpireTime;
		private String mKey;
		private PutCallback mCallback;
		private Exception mException;

		public PutAsynTask(Serializable object, String key, long duration,
				PutCallback callback) {
			mObject = object;
			mExpireTime = duration + System.currentTimeMillis();
			mCallback = callback;
			mKey = key;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			File file = new File(mCacheDir, mKey.hashCode() + "");
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				out.write(ObjectByte.toByteArray(new ObjectTimeWrapper(mObject,
						mExpireTime)));
			} catch (Exception e) {
				mException = e;
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
						mException = e;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mException == null)
				mCallback.onSuccess();
			else
				mCallback.onFail();
		}
	}

	private class GetAsynTask extends AsyncTask<Void, Void, Object> {
		private String mKey;
		private GetCallback mCallback;
		private Exception mException;

		public GetAsynTask(String key, GetCallback callback) {
			mCallback = callback;
			mKey = key;
		}

		@Override
		protected Object doInBackground(Void... arg0) {
			File file = new File(mCacheDir, mKey.hashCode() + "");

			if (!file.exists()) {
				return null;

			} else {
				byte[] objectBinary = inputStreamFromFile(file);
				Object object = ObjectByte.toObject(objectBinary);

				if (object instanceof ObjectTimeWrapper) {
					if (((ObjectTimeWrapper) object).expireTime() > System
							.currentTimeMillis()) {
						file.delete();
						return null;
					} else {
						return ((ObjectTimeWrapper) object).object();
					}
				} else {
					mException = new IllegalAccessException("put wrong");
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (mException == null)
				mCallback.onSuccess(result);
			else
				mCallback.onFail();
		}
	}

	class ObjectTimeWrapper implements Serializable {
		private static final long serialVersionUID = 1L;

		ObjectTimeWrapper(Serializable obj, long expireTime) {
			mObject = obj;
			mExpireTime = expireTime;
		}

		private long mExpireTime;
		private final Serializable mObject;

		public Object object() {
			return mObject;
		}

		public long expireTime() {
			return mExpireTime;
		}

		public void setExpireTime(long expireTime) {
			mExpireTime = expireTime;
		}
	}

}
