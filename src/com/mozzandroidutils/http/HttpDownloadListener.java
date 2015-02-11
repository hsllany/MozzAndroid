package com.mozzandroidutils.http;

public interface HttpDownloadListener {
	public void onStart(int fileSize);

	public void onDownloading(int downloadSize);

	public void onSuccessFinish();

	public void onFail();
}
