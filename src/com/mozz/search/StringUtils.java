package com.mozz.search;

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
	 * @return int, the length of the longest common subsquence
	 */
	public static final int lcs(String s1, String s2) {
		int[][] c = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			c[i][0] = 0;
		}

		for (int i = 0; i <= s2.length(); i++) {
			c[0][i] = 0;
		}

		for (int i = 0; i < s1.length(); i++) {
			for (int j = 0; j < s2.length(); j++) {
				char s1Char = s1.charAt(i);
				char s2Char = s2.charAt(j);

				if (s1Char == s2Char) {
					c[i + 1][j + 1] = c[i][j] + 1;
				} else {
					c[i + 1][j + 1] = c[i][j + 1] > c[i + 1][j] ? c[i][j + 1]
							: c[i + 1][j];
				}
			}
		}

		return c[s1.length()][s2.length()];
	}

}
