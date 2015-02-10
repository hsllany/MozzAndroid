package com.shitu.httputils.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shitu.httputils.R;
import com.ydandroidutils.sqlite.Model;
import com.ydandroidutils.sqlite.YDDBHelper;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String createSQL = "create table if not exists test (_id integer primary key autoincrement, name text)";
		YDDBHelper.createTable(createSQL, this);

		TestEloquent testTable = new TestEloquent(this);
		TestModel newModel = testTable.Find(1, new TestModel());

		System.out.println("adfa" + newModel.allOtherFields());
	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
	}

}

class TestModel extends Model {

	public String name;

}
