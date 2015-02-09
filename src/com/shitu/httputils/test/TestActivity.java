package com.shitu.httputils.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ydandroidutils.file.SDCard;
import com.ydandroidutils.http.DownloaderHttpUtils;
import com.ydandroidutils.http.HttpDownloadListener;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DownloaderHttpUtils httpUtils = new DownloaderHttpUtils();
		httpUtils.download("http://192.168.1.117/a.png",
				new HttpDownloadListener() {

					@Override
					public void onStart(int fileSize) {
						Log.d("downloadHttp", "fileSize=" + fileSize);

					}

					@Override
					public void onFinish(int status) {
						Log.d("downloadHttp", "status=" + status);

					}

					@Override
					public void onDownloading(int downloadSize) {
						Log.d("downloadHttp", "downloadSize=" + downloadSize);

					}
				}, SDCard.sdCardDir() + "a.png");
		
	}
}
