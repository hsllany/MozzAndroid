package com.mozz.search.balancetree;

import com.mozz.search.Searchable;

public class RBTreeNode<T extends Searchable> {
	private final int mKey;
	private T mValue;

	private RBTreeNode<T> mParent;
	private RBTreeNode<T> mLeft;
	private RBTreeNode<T> mRight;

	private short mColor;

	public static final short RED = 0xff;
	public static final short BLACK = 0x00;

	public RBTreeNode(T value) {
		mKey = value.toSearch().matchDistance();
		mValue = value;
	}

	public int key() {
		return mKey;
	}

	public T value() {
		return mValue;
	}

	public void setParent(RBTreeNode<T> parent) {
		mParent = parent;
	}

	public RBTreeNode<T> getParent() {
		return mParent;
	}

	public void setLeft(RBTreeNode<T> left) {
		mLeft = left;
	}

	public RBTreeNode<T> getLeft() {
		return mLeft;
	}

	public boolean hasLeft() {
		return mLeft != null;
	}

	public void setRight(RBTreeNode<T> right) {
		mRight = right;
	}

	public RBTreeNode<T> getRight() {
		return mRight;
	}

	public boolean hasRight() {
		return mRight != null;
	}

	public void setBlack() {
		mColor = BLACK;
	}

	public void setRed() {
		mColor = RED;
	}

	public short getColor() {
		return mColor;
	}

}
