package com.shitu.httputils.test;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.shitu.httputils.R;
import com.ydandroidutils.file.SDCard;
import com.ydandroidutils.file.YDAndroidConfig;
import com.ydandroidutils.http.DownloaderHttpUtils;
import com.ydandroidutils.http.HttpDownloadListener;
import com.ydandroidutils.http.HttpListener;
import com.ydandroidutils.http.HttpResponse;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toast.makeText(this, YDAndroidConfig.getAppDir(this), Toast.LENGTH_LONG)
				.show();

		Button button = (Button) findViewById(R.id.test);
		button.setOnClickListener(this);

		DownloaderHttpUtils httpUtils = new DownloaderHttpUtils();
		httpUtils.get("http://www.baidu.com", new HttpListener() {

			@Override
			public void onGet(HttpResponse response) {
				Log.d("HttpUtils", "from baidu" + response.html);
			}
		});

		httpUtils.get("http://www.sina.com.cn", new HttpListener() {

			@Override
			public void onGet(HttpResponse response) {
				Log.d("HttpUtils", "from sina" + response.html);
			}
		});

		HashMap<String, String> sendData = new HashMap<String, String>();

		sendData.put("h", "haha");
		sendData.put("y", "BB");
		httpUtils.post("http://192.168.1.117/postTest.php", new HttpListener() {

			@Override
			public void onGet(HttpResponse response) {
				Log.d("HttpUtils", "from 163" + response.html);
			}
		}, sendData);

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

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
		startActivity(i);
	}

}
