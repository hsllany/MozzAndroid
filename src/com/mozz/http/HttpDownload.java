package com.mozz.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.mozz.http.HttpUtils.*;

/**
 * download file task
 * 
 * @author YangTao
 * 
 */
class HttpDownload extends HttpTask {

	public HttpDownload(String url, HttpDownloadListener l, String path, String fileName, HttpParameter parameter, List<HttpTask> tasks) {
		super(tasks);
		mListener = l;
		mUrl = HttpHelper.urlEncording(url);
		if (!path.endsWith(File.separator))
			path = path + File.separator;
		mPath = path + fileName;

		buffer = new byte[8 * 1024];
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		mFile = new File(mPath);
		this.paramter = parameter;
	}

	@Override
	public void run() {
		super.run();
		InputStream in = null;
		OutputStream out = null;
		HttpURLConnection conn = null;
		try {
			mFile.createNewFile();
			URL url = new URL(mUrl);
			conn = (HttpURLConnection) url.openConnection();
			HttpHelper.setURLConnectionParameters(DOWNLOAD_FILE, conn, this.paramter);
			conn.connect();

			fileSize = conn.getHeaderFieldInt("Content-Length", -1);
			if (mListener != null) {
				if (fileSize >= 0) {
					mListener.onDownloadStart(fileSize);
				} else {
					// for chunked transfer encoding
					mListener.onDownloadStart(-1);
				}
			}

			int status = conn.getResponseCode();

			if (status >= 400)
				in = conn.getErrorStream();
			else
				in = conn.getInputStream();

			out = new FileOutputStream(mFile);
			int downloadSize = 0;
			for (;;) {
				int bytes = in.read(buffer);
				if (bytes == -1) {
					break;
				}

				downloadSize += bytes;
				out.write(buffer, 0, bytes);
				if (mListener != null) {
					mListener.onDownloading(downloadSize, bytes, (float) downloadSize / (float) fileSize);
				}

			}

			out.flush();

		} catch (Exception e) {
			e.printStackTrace();
			if (mFile != null && mFile.exists()) {
				mFile.delete();
			}
			if (mListener != null) {
				mListener.onDownloadFailed(e);
			}
		} finally {

			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
				} finally {
					if (conn != null)
						conn.disconnect();

					if (mListener != null) {
						mListener.onDownloadSuccess();
					}
				}
			}
		}
	}

	protected byte[] buffer;

	private String mUrl;
	private HttpDownloadListener mListener;
	private String mPath;

	private File mFile;

	private long fileSize;

	private HttpParameter paramter;

}
