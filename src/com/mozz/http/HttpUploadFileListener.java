package com.mozz.http;

public interface HttpUploadFileListener extends HttpListener {
	public void onUploading(long completeSize);

	public void onUploadStart(long allSize);
}
