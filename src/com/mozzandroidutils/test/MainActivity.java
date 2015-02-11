package com.mozzandroidutils.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.mozzandroidutils.sqlite.Eloquent;
import com.mozzandroidutils.sqlite.Eloquent.COLUMN_TYPE;
import com.mozzandroidutils.sqlite.Model;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Eloquent.create("student", new String[] { "name", "age" },
				new COLUMN_TYPE[] { COLUMN_TYPE.TYPE_TEXT,
						COLUMN_TYPE.TYPE_INTEGER }, this);

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

class TestEloquent extends Eloquent<TestModel> {

	public TestEloquent(Context context) {
		super(context);
	}

}
