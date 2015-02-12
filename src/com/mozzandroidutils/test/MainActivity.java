package com.mozzandroidutils.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mozzandroidutils.sqlite.ColumnType;
import com.mozzandroidutils.sqlite.Eloquent;
import com.mozzandroidutils.sqlite.Model;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Eloquent.create("test", new String[] { "name" },
				new ColumnType[] { ColumnType.TYPE_TEXT }, this);

		final TestEloquent testTable = new TestEloquent(this);

		new Thread() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					TestModel newModel = new TestModel();
					newModel.setName(System.currentTimeMillis() + "_" + 1);
					testTable.save(newModel);
				}
			};
		}.start();

		new Thread() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					TestModel newModel = new TestModel();
					newModel.setName(System.currentTimeMillis() + "_" + 2);
					testTable.save(newModel);
				}
			};
		}.start();

		new Thread() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					TestModel newModel = new TestModel();
					newModel.setName(System.currentTimeMillis() + "_" + 3);
					testTable.save(newModel);
				}
			};
		}.start();

		new Thread() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					TestModel newModel = new TestModel();
					newModel.setName(System.currentTimeMillis() + "_" + 4);
					testTable.save(newModel);
				}

			};
		}.start();

		testTable.close();

	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
	}

	public void onDestory() {

	}

}

class TestModel extends Model {
	private String name;

	public void setName(String nm) {
		this.name = nm;
	}
}

class TestEloquent extends Eloquent<TestModel> {

	public TestEloquent(Context context) {
		super(context);
	}

}
