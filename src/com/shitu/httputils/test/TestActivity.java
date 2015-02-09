package com.shitu.httputils.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ydandroidutils.http.DownloaderHttpUtils;
import com.ydandroidutils.http.HttpListener;
import com.ydandroidutils.http.HttpResponse;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DownloaderHttpUtils httpUtils = new DownloaderHttpUtils();
		httpUtils.get("http://www.qq.com", new HttpListener() {

			@Override
			public void onGet(HttpResponse response) {
				Log.d("HttpUtils", "from baidu" + response.html);
			}
		});
	}
}
