package com.mozz.search;

public abstract class Searchable implements Cloneable {
	protected MatchString mMatchString;

	public Searchable(String toSearchString, String keyword, int matchStrategy) {
		mMatchString = new MatchString(toSearchString, keyword, matchStrategy);
	}

	public MatchString toSearch() {
		return mMatchString;
	}

	public abstract Searchable clone();

	public int key() {
		return mMatchString.matchDistance();
	}

}
