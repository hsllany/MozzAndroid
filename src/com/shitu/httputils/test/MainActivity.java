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
import com.ydandroidutils.file.YDAndroidConfig;
import com.ydandroidutils.http.HttpListener;
import com.ydandroidutils.http.HttpResponse;
import com.ydandroidutils.http.HttpUtils;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toast.makeText(this, YDAndroidConfig.getAppFileDir(this),
				Toast.LENGTH_LONG).show();

		Button button = (Button) findViewById(R.id.test);
		button.setOnClickListener(this);

		HttpUtils httpUtils = new HttpUtils();
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
	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
		startActivity(i);
	}

}
