package com.mozz.test;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.cache.FileCache;
import com.mozz.cache.GetCallback;
import com.mozz.cache.PutCallback;
import com.mozz.http.HttpDownloadListener;
import com.mozz.http.HttpUtils;
import com.mozz.utils.MozzConfig;
import com.mozz.utils.SDCard;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HttpUtils http = new HttpUtils();

		http.download(
				"http:\/\/182.92.169.194\/shitu_upload\/¿´¿´1429260219154.png",
				new HttpDownloadListener() {

					@Override
					public void onDownloading(long downloadSize) {
						Log.d("DownloadTest", downloadSize + "");

					}

					@Override
					public void onDownloadSuccess() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDownloadStart(long fileSize) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onDownloadFailed(Exception e) {
						// TODO Auto-generated method stub

					}
				}, SDCard.sdCardDir(), "test.png");
	}
}
