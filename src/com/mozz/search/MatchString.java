package com.mozz.search;

public class MatchString {
	public static final int MODE_STRICT_EQUAL = 0x11;
	public static final int MODE_LCSUBSEQUENCE = 0x12;
	public static final int MODE_LCSUBSTRING = 0x13;
	public static final int MODE_CONTAIN_KEYWORD = 0x14;

	private String mContent;
	private String mKeyword;
	private int mMatchStrategy;
	private int mMatchDistance;
	private boolean mHasRefreshKeyword = false;

	public MatchString(String content, String keyword, int matchStrategy) {
		mContent = content;
		mKeyword = keyword;
		mMatchDistance = -1;
		mHasRefreshKeyword = true;
		setMatchStrategy(matchStrategy);
	}

	public MatchString(String content, int matchStrategy) {
		this(content, null, matchStrategy);
	}

	public void setMatchStrategy(int matchStrategy) {
		if (matchStrategy == MODE_STRICT_EQUAL
				|| matchStrategy == MODE_LCSUBSEQUENCE
				|| matchStrategy == MODE_LCSUBSTRING
				|| matchStrategy == MODE_CONTAIN_KEYWORD) {
			mMatchStrategy = matchStrategy;
			mHasRefreshKeyword = true;
		} else {
			throw new IllegalArgumentException(
					"strategy must be one of MODE_STRICT, MODE_LCSUBSEQUENCE or MODE_LCSUBSTRING");
		}
	}

	public void setKeyword(String keyword) {
		mKeyword = keyword;
		mHasRefreshKeyword = true;
	}

	public String keyword() {
		return mKeyword;
	}

	public int matchDistance() {
		if (mHasRefreshKeyword) {
			try {
				match();
				mHasRefreshKeyword = false;
			} catch (Exception e) {
				mHasRefreshKeyword = true;
				e.printStackTrace();
			}
		}

		return mMatchDistance;
	}

	public void match() {
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

	public int match(String keyword) {
		setKeyword(keyword);

		return matchDistance();
	}

}
