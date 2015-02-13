package com.mozzandroidutils.http;

public interface UpgradeListener extends HttpDownloadListener {
	/**
	 * check if there is new version.
	 * 
	 * @param hasNew
	 *            , true if there is new version.
	 * @param serverVersionCode
	 *            , server version code
	 * @param serverVersion
	 *            , server version string
	 * @param serverVersionDescription
	 *            , server version description
	 */
	public void onCheckNewVersion(boolean hasNew, int serverVersionCode,
			String serverVersion, String serverVersionDescription);

	/**
	 * check failed due to network problems.
	 */
	public void onCheckFailed();

}
