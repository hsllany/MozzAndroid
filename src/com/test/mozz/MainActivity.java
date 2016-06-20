package com.test.mozz;

import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.http.HttpListener;
import com.mozz.http.HttpResponse;
import com.mozz.http.HttpUtils;

public class MainActivity extends Activity {

	HttpUtils mHttp = new HttpUtils();

	static final String TAG = "HttpMainActivity";

	AtomicInteger integer = new AtomicInteger(0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (int i = 0; i < 100; i++) {
			mHttp.get("http://www.baidu.com", new HttpListener() {

				@Override
				public void onSuccess(HttpResponse response) {
					Log.d(TAG, response.status() + ", count=" + integer.addAndGet(1));

				}

				@Override
				public void onFail(Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "---destory");
		super.onDestroy();
		mHttp.shutdown();
	}
}
