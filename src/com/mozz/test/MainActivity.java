package com.mozz.test;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.cache.FileCache;
import com.mozz.cache.GetCallback;
import com.mozz.cache.PutCallback;
import com.mozz.utils.MozzConfig;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Player test = new Player(System.currentTimeMillis());
		FileCache cache = FileCache.instance(this,
				new File(MozzConfig.getAppAbsoluteDir(this)));

		cache.putWithVersion("haha", test, 1L, null);

		cache.putWithExpireTime("he", test, 5000, new PutCallback() {

			@Override
			public void onSuccess() {
				Log.d("CACHE", "success");

			}

			@Override
			public void onFail() {
				Log.d("CACHE", "fail");

			}
		});

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Log.d("MOZZ", "hell ow");

		cache.getOrOldversion("haha", 2L, new GetCallback() {

			@Override
			public void onSuccess(Object item) {
				if (item != null) {
					Log.d("CACHE", "get" + ((Player) item).mId);
				} else {
					Log.d("CACHE", "null");
				}

			}

			@Override
			public void onFail() {
				Log.d("CACHE", "failed");

			}
		});
	}
}
