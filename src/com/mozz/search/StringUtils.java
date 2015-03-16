package com.mozz.search;

public class StringUtils {

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
					c[i + 1][j + 1] = c[i][j];
				} else {
					c[i + 1][j + 1] = c[i][j + 1] > c[i + 1][j] ? c[i][j + 1]
							: c[i + 1][j];
				}
			}
		}

		return c[s1.length()][s2.length()];
	}

	public static final int lcs(String s1, String s2, int[][] c) {
		return 0;
	}
}
