package com.mozz.search.balancetree;

import java.util.ArrayList;
import java.util.List;

import com.mozz.search.Searchable;

public final class RBTree<T extends Searchable> {
	private RBTreeNode<T> mRoot;
	private int mSize;

	public RBTree() {
		mRoot = null;
		mSize = 0;
	}

	public void insert(RBTreeNode<T> node) {
		if (mRoot == null) {
			mRoot = node;
			mSize++;
		} else {
			insert(node, mRoot);
		}
	}

	private void insert(RBTreeNode<T> node, RBTreeNode<T> mRoot) {

	}

	public T search(int key) {
		return search(key, mRoot).value();
	}

	private RBTreeNode<T> search(int key, RBTreeNode<T> root) {
		if (root == null)
			return null;

		if (key == root.key()) {
			return root;
		}
		if (key < root.key()) {
			if (root.hasLeft()) {
				return search(key, root.getLeft());
			} else {
				return null;
			}
		} else {
			if (root.hasRight()) {
				return search(key, root.getRight());
			} else {
				return null;
			}
		}
	}

	public List<T> ascResult() {
		List<T> result = new ArrayList<T>();
		ascResult(result, mRoot);
		return result;
	}

	private void ascResult(List<T> result, RBTreeNode<T> root) {
		if (root.hasLeft()) {
			ascResult(result, root.getLeft());
		}

		result.add(root.value());

		if (root.hasRight()) {
			ascResult(result, root.getRight());
		}
	}

	public List<T> descResult() {
		List<T> result = new ArrayList<T>();
		descResult(result, mRoot);
		return result;
	}

	private void descResult(List<T> result, RBTreeNode<T> root) {
		if (root.hasRight()) {
			descResult(result, root.getRight());
		}

		result.add(root.value());

		if (root.hasLeft()) {
			descResult(result, root.getLeft());
		}
	}

}
