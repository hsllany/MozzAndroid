package com.shitu.httputils.test;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

		Toast.makeText(this, YDAndroidConfig.getAppDir(this), Toast.LENGTH_LONG)
				.show();

		Button button = (Button) findViewById(R.id.test);
		button.setOnClickListener(this);

		HttpUtils http = new HttpUtils();

		http.get("http://www.baidu.com", new HttpListener() {

			@Override
			public void onSuccess(HttpResponse response) {
				if (response.status == HTTP_OK) {

				}
			}

			@Override
			public void onFail(HttpResponse response) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
		startActivity(i);
	}

}
