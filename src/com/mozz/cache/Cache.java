package com.mozz.cache;

import java.io.Serializable;

public interface Cache {
	public void get(String key, GetCallback callback);

	public void put(String key, Serializable item, long duration,
			PutCallback callback);

	public void clear();

	public boolean remove(String key);
}
