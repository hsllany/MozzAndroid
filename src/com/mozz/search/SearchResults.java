package com.mozz.search;

import java.util.List;

import com.mozz.search.balancetree.RBTree;

public class SearchResults<T extends Searchable> {
	private RBTree<Integer, T> mRBTree;

	public SearchResults(String keyword, int matchStrategy) {

		if (matchStrategy == MatchString.MODE_STRICT_EQUAL
				|| matchStrategy == MatchString.MODE_LCSUBSEQUENCE
				|| matchStrategy == MatchString.MODE_LCSUBSTRING
				|| matchStrategy == MatchString.MODE_CONTAIN_KEYWORD) {
			mRBTree = new RBTree<Integer, T>();
		} else {
			throw new IllegalArgumentException(
					"match strategy muse be one of MODE_STRICT_EQUAL, MODE_LCSUBSEQUENCE, MODE_LCSUBSTRING, MODE_CONTAIN_KEYWORD");
		}

	}

	public void add(T newToSearch) {
		if (newToSearch.toSearch() != null) {
			mRBTree.insert(newToSearch.key(), newToSearch);
		}
	}

	public List<T> ascResult() {
		return mRBTree.ascResult();
	}

	public List<T> descResult() {
		return mRBTree.descResult();
	}
}
