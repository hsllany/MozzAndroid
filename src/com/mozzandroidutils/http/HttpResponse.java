package com.mozzandroidutils.http;

public class HttpResponse {
	private String mEntity;
	private int mStatus = -1;

	public void setEntity(String entity) {
		mEntity = entity;
	}

	public String entity() {
		return mEntity;
	}

	public void setStatus(int status) {
		mStatus = status;
	}

	public int status() {
		return mStatus;
	}

	public boolean ok() {
		return mStatus == 0xc8;
	}

}
