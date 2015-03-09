package com.mozz.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;

import com.mozz.file.MozzConfig;
import com.mozz.file.ObjectByte;

public class FileCache implements Cache {

	private static FileCache mCache;

	public static FileCache instance(Context context) {
		if (mCache == null)
			mCache = new FileCache(context);

		return mCache;
	}

	private Map<String, SoftReference<File>> mFileList = Collections
			.synchronizedMap(new HashMap<String, SoftReference<File>>());

	private File mCacheDir;
	private Context mContext;

	private FileCache(Context context) {
		mContext = context;
		MozzConfig.makeAppDirs(mContext);
		mCacheDir = new File(MozzConfig.getAppAbsoluteDir(context) + "/cache");
	}

	@Override
	public void getOrExpire(String key, GetCallback callback) {
		new GetAsynTask(key, callback, -1).execute();

	}

	@Override
	public void getOrOldversion(String key, long newVersion,
			GetCallback callback) {
		new GetAsynTask(key, callback, newVersion).execute();
	}

	@Override
	public void putWithExpireTime(String key, Serializable item, long duration,
			PutCallback callback) {
		ObjectTimeWrapper wrapper = new ObjectTimeWrapper(item,
				CacheStratigy.Cache_Expire);
		try {
			wrapper.setExpireTime(System.currentTimeMillis() + duration);
			new PutAsynTask(wrapper, key, callback).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void putWithVersion(String key, Serializable item, long version,
			PutCallback callback) {
		ObjectTimeWrapper wrapper = new ObjectTimeWrapper(item,
				CacheStratigy.Cache_Version);
		try {
			wrapper.setVersion(version);
			new PutAsynTask(wrapper, key, callback).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	};

	@Override
	public void clear() {
		File[] files = mCacheDir.listFiles();
		for (File file : files) {
			file.delete();
		}

		mFileList.clear();
	}

	private File getFile(String key) {
		SoftReference<File> softReference = mFileList.get(key);
		if (softReference != null) {
			File file = softReference.get();
			if (file != null) {
				return file;
			} else {
				mFileList.remove(key);
			}
		}

		File file = new File(mCacheDir, key.hashCode() + "");
		SoftReference<File> softRef = new SoftReference<File>(file);
		mFileList.put(key, softRef);

		return file;
	}

	@Override
	public boolean remove(String key) {
		File file = getFile(key);
		boolean deleteDone = false;
		synchronized (file) {
			deleteDone = file.delete();
		}
		if (deleteDone)
			mFileList.remove(key);
		return deleteDone;
	}

	private byte[] readFromFile(File file) {
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

	private void writeIntoFile(Serializable object, File file) throws Exception {
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
		private String mKey;
		private PutCallback mCallback;
		private Exception mException;

		public PutAsynTask(ObjectTimeWrapper object, String key,
				PutCallback callback) {
			mObject = object;
			mObject = object;
			mCallback = callback;
			mKey = key;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			File file = getFile(mKey);
			try {
				writeIntoFile(mObject, file);
			} catch (Exception e) {
				e.printStackTrace();
				mException = e;
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
		private long mNewversion;

		public GetAsynTask(String key, GetCallback callback, long newVersion) {
			mCallback = callback;
			mKey = key;
		}

		@Override
		protected Object doInBackground(Void... arg0) {
			File file = getFile(mKey);

			byte[] objectBinary = readFromFile(file);
			Object object = ObjectByte.toObject(objectBinary);

			if (object instanceof ObjectTimeWrapper) {
				ObjectTimeWrapper wrapper = (ObjectTimeWrapper) object;

				switch (wrapper.cacheStratigy()) {
				case Cache_Expire:
					if (wrapper.expireTime() > System.currentTimeMillis()) {
						synchronized (file) {
							file.delete();
							mFileList.remove(mKey);
						}
						return null;
					} else {
						return ((ObjectTimeWrapper) object).object();
					}
				case Cache_Version:
					if (wrapper.version() >= mNewversion) {
						return wrapper.object();
					} else {
						synchronized (file) {
							file.delete();
							mFileList.remove(mKey);
						}
						return null;
					}
				}

			} else {
				mException = new IllegalAccessException("put wrong");
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

	enum CacheStratigy {
		Cache_Expire, Cache_Version
	}

}
