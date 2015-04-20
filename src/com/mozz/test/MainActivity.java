package com.mozz.test;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.cache.FileCache;
import com.mozz.cache.GetCallback;
import com.mozz.cache.PutCallback;
import com.mozz.http.HttpDownloadListener;
import com.mozz.http.HttpUtils;
import com.mozz.utils.MozzConfig;
import com.mozz.utils.SDCard;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
