package com.test.mozz;

import android.test.AndroidTestCase;
import android.util.Log;

import com.mozz.http.HttpDownloadListener;
import com.mozz.http.HttpListener;
import com.mozz.http.HttpResponse;
import com.mozz.http.HttpUtils;
import com.mozz.utils.SDCard;

public class HttpUitlsTestCase extends AndroidTestCase {
	private static final String Tag = "HttpUitlsTestCase";

	public void getTest() throws Throwable {
		HttpUtils http = new HttpUtils();
		http.get("http://www.baidu.com", new HttpListener() {

			@Override
			public void onSuccess(HttpResponse response) {
				Log.d(Tag, response.status() + "" + response.entity());
			}

			@Override
			public void onFail(Exception e) {
				try {
					throw e;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});
	}

	public void downloadTest() {
		HttpUtils http = new HttpUtils();

	}

}
