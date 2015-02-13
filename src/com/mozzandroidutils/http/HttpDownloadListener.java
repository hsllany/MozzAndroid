package com.mozzandroidutils.http;

public interface HttpDownloadListener {
	public void onDownloadStart(int fileSize);

	public void onDownloading(int downloadSize);

	public void onDownloadSuccess();

	public void onDownloadFailed();
}
