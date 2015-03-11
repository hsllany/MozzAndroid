package com.mozz.test;

import java.io.Serializable;

public class Player implements Serializable {
	public Player(long id) {
		mId = id;
	}

	public long mId = 3;
}
