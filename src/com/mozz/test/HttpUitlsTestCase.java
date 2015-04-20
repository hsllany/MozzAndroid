package com.mozz.test;

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
		http.download(
				"http://182.92.169.194/shitu_upload/%E7%9C%8B%E7%9C%8B1429261862406.png",
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
