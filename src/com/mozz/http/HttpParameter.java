package com.mozz.http;

public class HttpParameter {
	private static final String DEFAULT_AGENT = "MozzHttpClient";

	public final int soTimeOut;
	public final int connectTimeOut;

	public String userAgent = DEFAULT_AGENT;

	public HttpParameter(int soTimeOut, int connectTimeOut) {
		if (soTimeOut < 0 || connectTimeOut < 0)
			throw new IllegalArgumentException();
		this.soTimeOut = soTimeOut;
		this.connectTimeOut = connectTimeOut;
	}
}
