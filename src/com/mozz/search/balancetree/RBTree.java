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
					// case 2
					goNode = goNode.getParent();
					leftRotate(mRoot, goNode);
				}

				// case 3
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
					// case 2
					goNode = goNode.getParent();
					rightRotate(mRoot, goNode);
				}

				// case 3
				goNode.getParent().setBlack();
				goNode.getParent().getParent().setRed();
				leftRotate(mRoot, goNode);
			}
		}

	}

	private void leftRotate(RBTreeNode<T> root, RBTreeNode<T> node) {
		RBTreeNode<T> y = node.getRight();

		node.setRight(y.getLeft());
		if (y.getLeft() != null) {
			y.getLeft().setParent(node);
		}

		y.setParent(node.getParent());

		if (node.getParent() == null) {
			root = y;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(y);
		} else {
			node.getParent().setRight(y);
		}

		y.setLeft(node);
		node.setParent(y);
	}

	private void rightRotate(RBTreeNode<T> root, RBTreeNode<T> node) {
		RBTreeNode<T> x = node.getLeft();

		node.setLeft(x.getLeft());
		if (x.getLeft() != null) {
			x.getLeft().setParent(node);
		}

		x.setParent(node.getParent());
		if (node.getParent() == null) {
			root = x;
		} else if (node == node.getParent().getLeft()) {
			node.getParent().setLeft(x);
		} else {
			node.getParent().setRight(x);
		}

		x.setRight(x);
		node.setParent(x);

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
