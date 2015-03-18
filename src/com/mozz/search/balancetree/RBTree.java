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

	public synchronized void insert(RBTreeNode<T> node) {
		if (mRoot == null) {
			mRoot = node;
			mSize++;
			return;
		}

		RBTreeNode<T> goNode = mRoot;

		while (true) {
			if (node.key() > goNode.key()) {
				if (goNode.hasRight()) {
					goNode = goNode.getRight();
					continue;
				} else {
					goNode.setRight(node);
					node.setParent(goNode);
					mSize++;
					break;
				}
			} else {
				if (goNode.hasLeft()) {
					goNode = goNode.getLeft();
					continue;
				} else {
					goNode.setLeft(node);
					node.setParent(goNode);
					mSize++;
					break;
				}
			}
		}

		node.setRed();
		insertFixUp(node);

	}

	private synchronized void insertFixUp(RBTreeNode<T> node) {
		RBTreeNode<T> goNode = node;

		while (goNode.getParent().getColor() == RBTreeNode.RED) {
			if (goNode.getParent() == goNode.getParent().getParent().getLeft()) {
				RBTreeNode<T> uncleNode = goNode.getParent().getParent()
						.getRight();

				// case 1
				if (uncleNode.getColor() == RBTreeNode.RED) {
					goNode.getParent().setBlack();
					uncleNode.setBlack();
					goNode.getParent().getParent().setRed();
					goNode = goNode.getParent().getParent();
					continue;
				} else if (goNode == goNode.getParent().getRight()) {
					goNode = goNode.getParent();
					leftRotate(mRoot, goNode);
				}

				goNode.getParent().setBlack();
				goNode.getParent().getParent().setRed();
				rightRotate(mRoot, goNode);
			} else {
				RBTreeNode<T> uncleNode = goNode.getParent().getParent()
						.getLeft();

				// case 1
				if (uncleNode.getColor() == RBTreeNode.RED) {
					goNode.getParent().setBlack();
					uncleNode.setBlack();
					goNode.getParent().getParent().setRed();
					goNode = goNode.getParent().getParent();
					continue;
				} else if (goNode == goNode.getParent().getLeft()) {
					goNode = goNode.getParent();
					rightRotate(mRoot, goNode);
				}

				goNode.getParent().setBlack();
				goNode.getParent().getParent().setRed();
				leftRotate(mRoot, goNode);
			}
		}

	}

	private void leftRotate(RBTreeNode<T> root, RBTreeNode<T> node) {

	}

	private void rightRotate(RBTreeNode<T> root, RBTreeNode<T> node) {

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
