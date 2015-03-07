package com.mozz.cache;

import java.io.Serializable;

public interface Cache {
	public void getOrExpire(String key, GetCallback callback);

	public void getOrOldversion(String key, long newVersion,
			GetCallback callback);

	public void putWithExpireTime(String key, Serializable item, long duration,
			PutCallback callback);

	public void putWithVersion(String key, Serializable item, long version,
			PutCallback callback);

	public void clear();

	public boolean remove(String key);
}
