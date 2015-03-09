package com.mozz.test;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.cache.FileCache;
import com.mozz.cache.GetCallback;
import com.mozz.cache.PutCallback;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Player test = new Player();
		FileCache cache = FileCache.instance(this);

		cache.putWithExpireTime("he", test, 3000, new PutCallback() {

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

		cache.getOrExpire("he", new GetCallback() {

			@Override
			public void onSuccess(Object item) {
				Log.d("CACHE", "GET" + item.getClass() + ","
						+ ((Player) (item)).id);

			}

			@Override
			public void onFail() {
				Log.d("CACHE", "£ç£å£ô¡¡fail");

			}
		});
	}


}
