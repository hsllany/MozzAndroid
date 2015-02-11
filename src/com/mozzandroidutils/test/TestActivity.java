package com.mozzandroidutils.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozzandroidutils.file.SDCard;
import com.mozzandroidutils.http.DownloaderHttpUtils;
import com.mozzandroidutils.http.HttpDownloadListener;
import com.mozzandroidutils.http.HttpListener;
import com.mozzandroidutils.http.HttpResponse;
import com.mozzandroidutils.http.HttpUtils;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HttpUtils httpUtils = new HttpUtils();
		httpUtils.get("http://www.baidu.com", new HttpListener() {

			@Override
			public void onSuccess(HttpResponse response) {
				if (response.status == HTTP_OK)
					System.out.println(response.html);

			}

			@Override
			public void onFail(HttpResponse response) {
				// TODO Auto-generated method stub

			}
		});

	}
}
