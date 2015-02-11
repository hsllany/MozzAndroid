package com.mozzandroidutils.http;

public interface HttpListener {
	public void onSuccess(HttpResponse response);

	public void onFail(HttpResponse response);

	public static final int HTTP_OK = 200;
}
