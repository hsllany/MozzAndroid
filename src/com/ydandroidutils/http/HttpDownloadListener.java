package com.ydandroidutils.http;

public interface HttpDownloadListener {
	public void onStart(int fileSize);

	public void onDownloading(int downloadSize);

	public void onFinish(int status);
}
