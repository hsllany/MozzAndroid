package com.test.mozz;

import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.mozz.search.balancetree.RBTree;

public class RBTreeTestCase extends AndroidTestCase {

	private final static String TAG = "RBTreeTestCase";

	public void RBTreeConstructTest() {
		RBTree<Integer, String> rbTree = new RBTree<Integer, String>();
		rbTree.insert(41, "41");
		rbTree.insert(38, "38");
		rbTree.insert(31, "31");
		rbTree.insert(12, "12");
		rbTree.insert(19, "19");
		rbTree.insert(8, "8");

		List<String> result = rbTree.ascResult();

		for (int i = 0; i < result.size(); i++) {
			Log.d(TAG, result.get(i));
		}
	}

}
