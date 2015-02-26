package com.mozzandroidutils.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.mozzandroidutils.file.MozzConfig;
import com.mozzandroidutils.http.HttpDownloadListener;
import com.mozzandroidutils.http.UpgradeListener;
import com.mozzandroidutils.http.Upgrader;
import com.mozzandroidutils.sqlite.ColumnType;
import com.mozzandroidutils.sqlite.Eloquent;
import com.mozzandroidutils.sqlite.Model;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		testDB();
	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent();
		i.setClass(this, TestActivity.class);
	}

	public void onDestory() {

	}

	private void testDB() {
		StudentsEloquent studentsTable = new StudentsEloquent(this,
				Student.class);
		studentsTable.setDebug(true);

		List<Model> class402 = new ArrayList<Model>();

		for (int i = 0; i < 1000; i++) {
			Student student = new Student();
			student.name = "hello world + i" + i;

			class402.add(student);
		}

		long now = System.currentTimeMillis();
		System.out.println("now is " + now);

		studentsTable.insertMany(class402);

		System.out.println(" insert time spend "
				+ (System.currentTimeMillis() - now));

	}

	private void testEloquentCreate() {
		String[] columnNames = { "name", "gender", "age", "extra" };
		ColumnType[] columnTypes = { ColumnType.TYPE_TEXT,
				ColumnType.TYPE_TEXT, ColumnType.TYPE_INTEGER,
				ColumnType.TYPE_BLOB };
		Eloquent.create("students", columnNames, columnTypes, this);
	}

	private void testUpgrader() {
		final Upgrader upgrader = new Upgrader(
				"http://182.92.150.3/upgrade.json", this);
		upgrader.setOnUpgradeListener(new UpgradeListener() {

			@Override
			public void onNewVersion(int serverVersionCode,
					String serverVersion, String serverVersionDescription) {

				try {
					upgrader.download(null,
							MozzConfig.getAppAbsoluteDir(MainActivity.this),
							"newVersion.apk");
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNoNewVersion() {
				Log.d("Upgrader", "onNoNewVersion");
			}

			@Override
			public void onCheckFailed() {
				Log.d("Upgrader", "onCheckFailed:");
			}
		});

		upgrader.checkNewVersion();

		try {
			upgrader.download(new HttpDownloadListener() {

				@Override
				public void onDownloading(int downloadSize) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadStart(int fileSize) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onDownloadFailed() {
					// TODO Auto-generated method stub

				}
			}, "/AppDir", "newVersion.apk");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}

class StudentsEloquent extends Eloquent {
	public StudentsEloquent(Context context, Class<? extends Model> clazz) {
		super(context, clazz);
	}
}
