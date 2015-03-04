package com.mozz.http;

public interface UpgradeListener {
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
	public void onNewVersion(int serverVersionCode, String serverVersion,
			String serverVersionDescription);

	public void onNoNewVersion();

	/**
	 * check failed due to network problems.
	 */
	public void onCheckFailed();

}
