package com.mozzandroidutils.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.mozzandroidutils.file.MozzConfig;

import android.content.Context;

public class Upgrader {

	public Upgrader(String upgradeURL, Context context) {
		mUpgradeUrl = upgradeURL;
		mContext = context;
		mHttp = new DownloaderHttpUtils();
	}

	public void setOnUpgradeListener(UpgradeListener upgradeListener) {
		mUpgradeListener = upgradeListener;
	}

	public void checkNewVersion() {
		mHttp.get(mUpgradeUrl, new HttpListener() {

			@Override
			public void onSuccess(HttpResponse response) {
				if (response.status == HTTP_OK && mUpgradeListener != null) {
					try {
						JSONObject jsonObject = new JSONObject(response.html);
						int serverCode = jsonObject.getInt("code");
						String serverVersion = jsonObject.getString("version");
						String serverVersionDescription = jsonObject
								.getString("des");
						mDownloadUrl = jsonObject.getString("downloadurl");
						synchronized (lockObject) {
							lockObject.notifyAll();
						}

						if (MozzConfig.getPackageVersionCode(mContext) < serverCode) {
							mUpgradeListener.onCheckNewVersion(true,
									serverCode, serverVersion,
									serverVersionDescription);
						} else {
							mUpgradeListener.onCheckNewVersion(false,
									serverCode, serverVersion,
									serverVersionDescription);
						}

					} catch (JSONException e) {
						e.printStackTrace();
						if (mUpgradeListener != null) {
							mUpgradeListener.onCheckFailed();
						}
					}
				} else if (mUpgradeListener != null)
					mUpgradeListener.onCheckFailed();
			}

			@Override
			public void onFail(HttpResponse response) {
				if (mUpgradeListener != null) {
					mUpgradeListener.onCheckFailed();
				}
			}
		});
	}

	public void download() {
		if (mDownloadUrl == null) {
			checkNewVersion();

			try {
				synchronized (lockObject) {
					lockObject.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		mHttp.download(mDownloadUrl, new HttpDownloadListener() {

			@Override
			public void onDownloading(int downloadSize) {
				if (mUpgradeListener != null)
					mUpgradeListener.onDownloading(downloadSize);
			}

			@Override
			public void onDownloadStart(int fileSize) {
				if (mUpgradeListener != null)
					mUpgradeListener.onDownloadStart(fileSize);
			}

			@Override
			public void onDownloadSuccess() {
				if (mUpgradeListener != null)
					mUpgradeListener.onDownloadSuccess();
			}

			@Override
			public void onDownloadFailed() {
				if (mUpgradeListener != null)
					mUpgradeListener.onDownloadFailed();
			}
		}, MozzConfig.getAppAbsoluteDir(mContext) + "newVersion.apk");
	}

	private String mUpgradeUrl;
	private String mDownloadUrl;
	private Context mContext;
	private DownloaderHttpUtils mHttp;
	private UpgradeListener mUpgradeListener;

	private Object lockObject = new Object();
}
