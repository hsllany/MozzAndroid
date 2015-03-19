package com.mozz.search.balancetree;

public class RBTreeNode<K extends Comparable<K>, V> {
	private final K mKey;
	private V mValue;

	private RBTreeNode<K, V> mParent;
	private RBTreeNode<K, V> mLeft;
	private RBTreeNode<K, V> mRight;

	private RBTreeNode<K, V> mNil;

	private short mColor;

	public static final short RED = 0xff;
	public static final short BLACK = 0x00;

	public RBTreeNode(K key, V value, RBTreeNode<K, V> nilNode) {
		mKey = key;
		mValue = value;
		mNil = nilNode;

		mParent = mNil;
		mLeft = mNil;
		mRight = mNil;
	}

	// for T.nil
	RBTreeNode() {
		mKey = null;
		mValue = null;
		mColor = BLACK;
	}

	public K key() {
		return mKey;
	}

	public V value() {
		return mValue;
	}

	public void setParent(RBTreeNode<K, V> parent) {
		mParent = parent;
	}

	public RBTreeNode<K, V> getParent() {
		return mParent;
	}

	public boolean hasParent() {
		return mParent != mNil || mParent != null;
	}

	public void setLeft(RBTreeNode<K, V> left) {
		mLeft = left;
	}

	public RBTreeNode<K, V> getLeft() {
		return mLeft;
	}

	public boolean hasLeft() {
		return mLeft != mNil;
	}

	public void setRight(RBTreeNode<K, V> right) {
		mRight = right;
	}

	public RBTreeNode<K, V> getRight() {
		return mRight;
	}

	public boolean hasRight() {
		return mRight != mNil;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{key:" + mKey + ", value:" + mValue);
		if (mColor == BLACK) {
			sb.append(", BLACK");
		} else {
			sb.append(", RED");
		}
		if (hasLeft()) {
			sb.append(", left: " + getLeft());
		}

		if (hasRight()) {
			sb.append(", right: " + getRight());
		}

		sb.append("}");
		return sb.toString();
	}
}
