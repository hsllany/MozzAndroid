package com.mozz.http;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.mozz.utils.SystemInfo;

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
				if (response.status() == HTTP_OK && mUpgradeListener != null) {
					try {
						JSONObject jsonObject = new JSONObject(response
								.entity());
						int serverCode = jsonObject.getInt("versionCode");
						int forceCode = jsonObject.getInt("forceCode");
						String serverVersion = jsonObject
								.getString("versionName");
						String serverVersionDescription = jsonObject
								.getString("des");

						if (SystemInfo.getPackageVersionCode(mContext) < serverCode) {
							boolean forceUpgrade = false;
							if (forceCode==1) {
								forceUpgrade = true;
							}
							mDownloadUrl = jsonObject.getString("downloadurl");
							mUpgradeListener.onNewVersion(forceUpgrade,serverCode,
									serverVersion, serverVersionDescription,mDownloadUrl);
						} else {
							mUpgradeListener.onNoNewVersion();
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
			public void onFail(Exception e) {
				if (mUpgradeListener != null) {
					mUpgradeListener.onCheckFailed();
				}
			}
		});
	}

	public void download(HttpDownloadListener l, String path, String fileName)
			throws IllegalAccessException {
		if (mDownloadUrl == null) {
			throw new IllegalAccessException(
					"download URL is null, please make sure that download() can be only invoked after checkNewVersion() success which is guaranteed by the onNewVersion().");
		}

		mHttp.download(mDownloadUrl, l, path, fileName);
	}

	private String mUpgradeUrl;
	private String mDownloadUrl;
	private Context mContext;
	private DownloaderHttpUtils mHttp;
	private UpgradeListener mUpgradeListener;
}
