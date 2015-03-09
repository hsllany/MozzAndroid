package com.mozz.test;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mozz.cache.FileCache.CacheStratigy;
import com.mozz.cache.ObjectTimeWrapper;
import com.mozz.file.ObjectByte;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tst test = new Tst();
		ObjectTimeWrapper wrapper = new ObjectTimeWrapper(test,
				CacheStratigy.Cache_Expire);
		byte[] a = ObjectByte.toByteArray(wrapper);

		Log.d("MOZZ", "hell ow");
	}

	class Tst implements Serializable {
		private static final long serialVersionUID = 1L;

		int id = 3;
	}

}
