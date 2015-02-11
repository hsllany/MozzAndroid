package com.mozzandroidutils.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mozzandroidutils.sqlite.Model;
import com.mozzandroidutils.sqlite.MozzDBHelper;
import com.shitu.httputils.R;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String createSQL = "create table if not exists test (_id integer primary key autoincrement, name text)";
		MozzDBHelper.createTable(createSQL, this);

		TestEloquent testTable = new TestEloquent(this);
		TestModel newModel = testTable.Find(1, new TestModel());

		newModel.setName(System.currentTimeMillis() + "");
		testTable.save(newModel);

	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
	}

}

class TestModel extends Model {
	private String name;

	public void setName(String nm) {
		this.name = nm;
	}
}
