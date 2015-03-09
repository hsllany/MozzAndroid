package com.mozz.cache;

import java.io.Serializable;

import com.mozz.cache.FileCache.CacheStratigy;

public class ObjectTimeWrapper implements Serializable {
	private static final long serialVersionUID = 201503092309L;

	private long mExpireTime = -1;
	private final Serializable mObject;
	private long mVersion = -1;
	private final CacheStratigy mCacheStratigy;

	ObjectTimeWrapper(Serializable obj, CacheStratigy cacheStratigy) {
		mObject = obj;
		mCacheStratigy = cacheStratigy;
	}

	public Object object() {
		return mObject;
	}

	public CacheStratigy cacheStratigy() {
		return mCacheStratigy;
	}

	public long expireTime() {
		return mExpireTime;
	}

	public long version() {
		return mVersion;
	}

	public void setExpireTime(long expireTime) throws Exception {
		if (mCacheStratigy == CacheStratigy.Cache_Expire) {
			mExpireTime = expireTime;
		} else {
			throw new Exception(
					"cache stratigy is Cache_Version, can't set expireTime");
		}
	}

	public void setVersion(long version) throws Exception {
		if (mCacheStratigy == CacheStratigy.Cache_Version) {
			mVersion = version;
		} else {
			throw new Exception(
					"cache stratigy is Cache_Version, can't set expireTime");
		}
	}
}
