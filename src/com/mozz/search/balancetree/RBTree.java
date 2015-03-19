package com.mozz.search.balancetree;

import java.util.ArrayList;
import java.util.List;

public final class RBTree<K extends Comparable<K>, V> {
	private RBTreeNode<K, V> mRoot;
	private RBTreeNode<K, V> mNil;
	private int mSize;

	public RBTree() {
		mRoot = null;
		mSize = 0;

		mNil = new RBTreeNode<K, V>();
	}

	protected synchronized void insert(RBTreeNode<K, V> node) {
		if (mRoot == null) {
			mRoot = node;
			mRoot.setBlack();

			mSize++;
			return;
		}

		RBTreeNode<K, V> goNode = mRoot;

		while (true) {
			// bigger
			if (node.key().compareTo(goNode.key()) > 0) {
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

	public synchronized void insert(K key, V value) {
		RBTreeNode<K, V> node = new RBTreeNode<K, V>(key, value, mNil);
		insert(node);
	}

	private synchronized void insertFixUp(RBTreeNode<K, V> node) {
		RBTreeNode<K, V> goNode = node;

		while (goNode.getParent().getColor() == RBTreeNode.RED) {
			if (goNode.getParent() == goNode.getParent().getParent().getLeft()) {
				RBTreeNode<K, V> uncleNode = goNode.getParent().getParent()
						.getRight();

				// case 1
				if (uncleNode.getColor() == RBTreeNode.RED) {

					goNode.getParent().setBlack();
					uncleNode.setBlack();
					goNode.getParent().getParent().setRed();
					goNode = goNode.getParent().getParent();
					if (goNode.getParent() == mNil)
						break;
					continue;

				} else if (goNode == goNode.getParent().getRight()) {
					// case 2
					goNode = goNode.getParent();
					leftRotate(goNode);
				}

				// case 3
				goNode.getParent().setBlack();
				goNode.getParent().getParent().setRed();
				rightRotate(goNode.getParent().getParent());
				break;
			} else {
				RBTreeNode<K, V> uncleNode = goNode.getParent().getParent()
						.getLeft();

				// case 1
				if (uncleNode.getColor() == RBTreeNode.RED) {

					goNode.getParent().setBlack();
					uncleNode.setBlack();
					goNode.getParent().getParent().setRed();
					goNode = goNode.getParent().getParent();
					if (goNode.getParent() == mNil)
						break;
					continue;

				} else if (goNode == goNode.getParent().getLeft()) {
					// case 2
					goNode = goNode.getParent();
					rightRotate(goNode);
				}

				// case 3
				goNode.getParent().setBlack();
				goNode.getParent().getParent().setRed();
				leftRotate(goNode.getParent().getParent());
				break;
			}
		}

		mRoot.setBlack();

	}

	private void leftRotate(RBTreeNode<K, V> node) {
		RBTreeNode<K, V> y = node.getRight();

		node.setRight(y.getLeft());
		if (y.hasLeft()) {
			y.getLeft().setParent(node);
		}

		y.setParent(node.getParent());

		if (node.getParent() == mNil) {
			mRoot = y;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(y);
		} else {
			node.getParent().setRight(y);
		}

		y.setLeft(node);
		node.setParent(y);
	}

	private void rightRotate(RBTreeNode<K, V> node) {
		RBTreeNode<K, V> x = node.getLeft();

		node.setLeft(x.getRight());
		if (x.hasRight()) {
			x.getRight().setParent(node);
		}

		x.setParent(node.getParent());
		if (node.getParent() == mNil) {
			mRoot = x;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(x);
		} else {
			node.getParent().setRight(x);
		}

		x.setRight(node);
		node.setParent(x);

	}

	public V search(K key) {
		return search(key, mRoot).value();
	}

	private RBTreeNode<K, V> search(K key, RBTreeNode<K, V> root) {
		if (root == null)
			return null;

		if (key.compareTo(root.key()) == 0) {
			return root;
		}
		if (key.compareTo(root.key()) < 0) {
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

	public List<V> ascResult() {
		List<V> result = new ArrayList<V>();
		ascResult(result, mRoot);
		return result;
	}

	private void ascResult(List<V> result, RBTreeNode<K, V> root) {
		if (root.hasLeft()) {
			ascResult(result, root.getLeft());
		}

		result.add(root.value());

		if (root.hasRight()) {
			ascResult(result, root.getRight());
		}
	}

	public List<V> descResult() {
		List<V> result = new ArrayList<V>();
		descResult(result, mRoot);
		return result;
	}

	private void descResult(List<V> result, RBTreeNode<K, V> root) {
		if (root.hasRight()) {
			descResult(result, root.getRight());
		}

		result.add(root.value());

		if (root.hasLeft()) {
			descResult(result, root.getLeft());
		}
	}

}
