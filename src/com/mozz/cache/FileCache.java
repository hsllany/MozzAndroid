package com.mozz.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.mozz.utils.ObjectByte;

/**
 * 
 * @author yang tao
 * 
 */
public class FileCache implements Cache {
	/*
	 * debug tag
	 */
	private static final String DEBUG_TAG = "FileCache";

	/*
	 * singleton
	 */
	private static FileCache mCache;

	/*
	 * cache strategy cache by expire
	 */
	public static final byte CACHE_BY_EXPIRE = 0x1;

	/*
	 * cache strategy cache by version
	 */
	public static final byte CACHE_BY_VERSION = 0x0;

	/*
	 * singleton instance
	 */
	public static FileCache instance(Context context, File cacheDir) {
		if (mCache == null)
			mCache = new FileCache(context, cacheDir);

		return mCache;
	}

	private LruCache<String, File> mFileList = new LruCache<String, File>(25);

	/*
	 * cache dirs
	 */
	private File mCacheDir;

	/*
	 * private constructors
	 */
	private FileCache(Context context, File cacheDir) {
		mCacheDir = cacheDir;
		if (!mCacheDir.exists())
			if (!mCacheDir.mkdirs())
				throw new RuntimeException("Cannot make cache directories");
	}

	@Override
	public void getOrExpire(String key, GetCallback callback) {
		File cacheFile = getFileByKey(key);
		new GetAsynTask(key, callback, cacheFile).execute();

	}

	@Override
	public void getOrOldversion(String key, long newVersion,
			GetCallback callback) {
		File cacheFile = getFileByKey(key);
		new GetAsynTask(key, callback, cacheFile, newVersion).execute();
	}

	@Override
	public void putWithExpireTime(String key, Serializable item, long duration,
			PutCallback callback) {
		ObjectTimeWrapper wrapper = new ObjectTimeWrapper(item, CACHE_BY_EXPIRE);
		try {
			long expireTime = System.currentTimeMillis() + duration;
			wrapper.setExpireTime(expireTime);
			File cacheFile = getFileByKey(key);
			new PutAsynTask(wrapper, cacheFile, callback).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void putWithVersion(String key, Serializable item, long version,
			PutCallback callback) {
		ObjectTimeWrapper wrapper = new ObjectTimeWrapper(item,
				CACHE_BY_VERSION);
		try {
			wrapper.setVersion(version);
			File cacheFile = getFileByKey(key);
			new PutAsynTask(wrapper, cacheFile, callback).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	@Override
	public synchronized void clear() {
		File[] files = mCacheDir.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		mFileList.evictAll();

	}

	/**
	 * get file instance from mFileList
	 * 
	 * @param key
	 * @return
	 */
	private synchronized File getFileByKey(String key) {
		File file = mFileList.get(key);
		if (file != null) {
			return file;
		} else {
			File file2 = new File(mCacheDir, key.hashCode() + "");
			mFileList.put(key, file2);
			return file2;
		}
	}

	@Override
	public synchronized boolean remove(String key) {
		File file = getFileByKey(key);
		boolean deleteDone = false;
		synchronized (file) {
			deleteDone = file.delete();
		}
		if (deleteDone)
			mFileList.remove(key);
		return deleteDone;
	}

	private static byte[] readFromFile(File file) {
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

	private static void writeIntoFile(Serializable object, File file)
			throws Exception {
		FileOutputStream out = null;

		if (file == null || object == null)
			throw new NullPointerException("file/object can't be null");

		synchronized (file) {
			try {
				file.createNewFile();
				out = new FileOutputStream(file);
				out.write(ObjectByte.toByteArray(object));
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
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
	}

	private class PutAsynTask extends AsyncTask<Void, Void, Void> {
		private Serializable mObject;
		private PutCallback mCallback;
		private Exception mException;
		private File mCacheFile;

		public PutAsynTask(ObjectTimeWrapper object, File file,
				PutCallback callback) {
			mObject = object;
			mObject = object;
			mCacheFile = file;
			mCallback = callback;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				writeIntoFile(mObject, mCacheFile);
			} catch (Exception e) {
				e.printStackTrace();
				mException = e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mCallback != null) {
				if (mException == null)
					mCallback.onSuccess();
				else
					mCallback.onFail();
			}
		}
	}

	private class GetAsynTask extends AsyncTask<Void, Void, Object> {
		private GetCallback mCallback;
		private Exception mException;
		private long mNewversion;
		private byte mMethodStrategy;
		private File mCacheFile;

		public GetAsynTask(String key, GetCallback callback, File file,
				long newVersion) {
			mCallback = callback;
			mNewversion = newVersion;
			mCacheFile = file;
			Log.d(DEBUG_TAG, "new get");

			mMethodStrategy = CACHE_BY_VERSION;
		}

		public GetAsynTask(String key, GetCallback callback, File file) {
			this(key, callback, file, -1);

			mMethodStrategy = CACHE_BY_EXPIRE;
		}

		@Override
		protected Object doInBackground(Void... arg0) {
			Log.d(DEBUG_TAG, "doInBackground");

			byte[] objectBinary = readFromFile(mCacheFile);
			Object object = ObjectByte.toObject(objectBinary);

			if (object == null)
				return null;

			if (object instanceof ObjectTimeWrapper) {
				ObjectTimeWrapper wrapper = (ObjectTimeWrapper) object;

				// this should proceed if and only if get strategy equals
				// wrapper's strategy
				if (wrapper.cacheStratigy() == mMethodStrategy) {

					switch (mMethodStrategy) {

					case CACHE_BY_EXPIRE:
						if (wrapper.expireTime() < System.currentTimeMillis()) {
							Log.d(DEBUG_TAG, "expired:" + wrapper.expireTime());
							synchronized (this) {
								mCacheFile.delete();
							}
							return null;
						} else {
							Log.d(DEBUG_TAG, "got it");
							return ((ObjectTimeWrapper) object).object();
						}

					case CACHE_BY_VERSION:
						if (wrapper.version() >= mNewversion) {
							Log.d(DEBUG_TAG, "version got it");
							return wrapper.object();
						} else {
							Log.d(DEBUG_TAG, "version old");
							synchronized (this) {
								mCacheFile.delete();
							}
							return null;
						}
					}
				} else {
					mException = new IllegalAccessException(
							"get strategy wrong");

					return null;
				}

			} else {
				mException = new IllegalAccessException(
						"none ObjectWrapper exception");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			Log.d(DEBUG_TAG, "onPostExecute:" + (mCallback != null));
			if (mCallback != null) {
				if (mException == null)
					mCallback.onSuccess(result);
				else
					mCallback.onFail();
			}
		}
	}

}
