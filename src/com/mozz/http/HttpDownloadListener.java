package com.mozz.http;

public interface HttpDownloadListener {
	public void onDownloadStart(long fileSize);

	public void onDownloading(long downloadSize, long incrementSize,
			float percentage);

	public void onDownloadSuccess();

	public void onDownloadFailed(Exception e);
}
