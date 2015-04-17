package com.mozz.http;

public interface HttpDownloadListener {
	public void onDownloadStart(long fileSize);

	public void onDownloading(long downloadSize);

	public void onDownloadSuccess();

	public void onDownloadFailed(Exception e);
}
