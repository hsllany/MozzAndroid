package com.shitu.httputils.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.shitu.httputils.R;
import com.shitu.httputils.libs.HttpListener;
import com.shitu.httputils.libs.HttpResponse;
import com.shitu.httputils.libs.HttpUtils;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

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
		httpUtils.get("http://www.163.com", new HttpListener() {

			@Override
			public void onGet(HttpResponse response) {
				Log.d("HttpUtils", "from 163" + response.html);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
