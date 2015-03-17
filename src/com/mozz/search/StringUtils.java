package com.mozz.search;

import android.util.Log;

/**
 * 
 * @author Yang Tao<hsllany@163.com>
 * @version 1.0
 */
public class StringUtils {

	/**
	 * compute the longest common subsquence lengths. Using bottom-top stratigy
	 * of dynamic programming.
	 * 
	 * @param s1
	 *            , String
	 * @param s2
	 *            , String
	 * @param subsquenece
	 *            , String, store the longest common subsquence
	 * @return int, the length of the longest common subsquence
	 */
	public static final int lcsubsquence(String s1, String s2,
			String subsquenece) {
		String[][] result = new String[s1.length() + 1][s2.length() + 1];
		int[][] c = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			c[i][0] = 0;
			result[i][0] = "";
		}

		for (int i = 0; i <= s2.length(); i++) {
			c[0][i] = 0;
			result[0][i] = "";
		}

		for (int i = 0; i < s1.length(); i++) {
			for (int j = 0; j < s2.length(); j++) {
				char s1Char = s1.charAt(i);
				char s2Char = s2.charAt(j);

				if (s1Char == s2Char) {
					c[i + 1][j + 1] = c[i][j] + 1;
					result[i + 1][j + 1] = result[i][j] + s1Char;
				} else {
					if (c[i][j + 1] > c[i + 1][j]) {
						c[i + 1][j + 1] = c[i][j + 1];
						result[i + 1][j + 1] = result[i][j + 1];
					} else {
						c[i + 1][j + 1] = c[i + 1][j];
						result[i + 1][j + 1] = result[i + 1][j];
					}
				}
			}
		}
		if (subsquenece instanceof String) {
			subsquenece = result[s1.length()][s2.length()];
			Log.d("StringUtils", subsquenece);
		}
		return c[s1.length()][s2.length()];
	}

	/**
	 * compute the longest common substring lengths. Using bottom-top stratigy
	 * of dynamic programming.
	 * 
	 * @param s1
	 *            , String
	 * @param s2
	 *            , String
	 * @param substring
	 *            , store the longest common substring
	 * @return int, the length of the longest common substring
	 */
	public static final int lcsubstring(String s1, String s2, String substring) {
		String[][] result = new String[s1.length() + 1][s2.length() + 1];
		int[][] c = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			c[i][0] = 0;
			result[i][0] = "";
		}

		for (int i = 0; i <= s2.length(); i++) {
			c[0][i] = 0;
			result[0][i] = "";
		}

		int maxLength = 0;

		for (int i = 0; i < s1.length(); i++) {
			for (int j = 0; j < s2.length(); j++) {
				char s1Char = s1.charAt(i);
				char s2Char = s2.charAt(j);

				if (s1Char == s2Char) {
					c[i + 1][j + 1] = c[i][j] + 1;
					result[i + 1][j + 1] = result[i][j] + s1Char;
					if (c[i + 1][j + 1] > maxLength) {
						maxLength = c[i + 1][j + 1];
						if (substring instanceof String)
							substring = result[i + 1][j + 1];
					}
				}
			}
		}

		if (substring instanceof String) {
			Log.d("StringUtils", ":" + substring);
		}

		return maxLength;
	}
}
