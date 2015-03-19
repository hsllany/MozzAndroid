package com.mozz.search;

public class MatchString {
	/**
	 * content.equals(keyword)
	 */
	public static final int MODE_STRICT_EQUAL = 0x11;

	/**
	 * content and keyword has the longest common sequence
	 */
	public static final int MODE_LCSUBSEQUENCE = 0x12;

	/**
	 * content and keyword has the longest common substring
	 */
	public static final int MODE_LCSUBSTRING = 0x13;

	/**
	 * content contains the keyword as substring
	 */
	public static final int MODE_CONTAIN_KEYWORD = 0x14;

	private String mContent;
	private String mKeyword;
	private final int mMatchStrategy;
	private int mMatchDistance;

	public MatchString(String content, String keyword, int matchStrategy) {
		if (matchStrategy == MODE_STRICT_EQUAL
				|| matchStrategy == MODE_LCSUBSEQUENCE
				|| matchStrategy == MODE_LCSUBSTRING
				|| matchStrategy == MODE_CONTAIN_KEYWORD) {
			mMatchStrategy = matchStrategy;
		} else {
			throw new IllegalArgumentException(
					"strategy must be one of MODE_STRICT, MODE_LCSUBSEQUENCE or MODE_LCSUBSTRING");
		}

		mContent = content;
		mKeyword = keyword;
		mMatchDistance = -1;

		match();
	}

	public String keyword() {
		return mKeyword;
	}

	public int matchDistance() {
		return mMatchDistance;
	}

	private void match() {
		if (mKeyword == null)
			throw new NullPointerException("keyword must not null");

		switch (mMatchStrategy) {
		case MODE_STRICT_EQUAL:
			if (mContent.equals(mKeyword))
				mMatchDistance = 1;
			else
				mMatchDistance = 0;
			break;
		case MODE_LCSUBSEQUENCE:
			mMatchDistance = StringUtils.lcsubsquence(mContent, mKeyword, null);
			break;
		case MODE_LCSUBSTRING:
			mMatchDistance = StringUtils.lcsubstring(mContent, mKeyword, null);
			break;
		case MODE_CONTAIN_KEYWORD:
			if (mContent.contains(mKeyword)) {
				mMatchDistance = 1;
			} else {
				mMatchDistance = 0;
			}
			break;
		}
	}

	public MatchString clone() {
		MatchString o = null;
		try {
			o = (MatchString) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

}
