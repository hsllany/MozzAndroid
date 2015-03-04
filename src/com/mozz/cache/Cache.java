package com.mozz.cache;

import java.io.Serializable;

public interface Cache {
	public Object get(String key);

	public void put(String key, Serializable item);

	public void clear();

	public boolean remove(String key);
}
