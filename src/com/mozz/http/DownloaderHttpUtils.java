package com.mozz.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloaderHttpUtils extends HttpUtils {
	private static class HttpDownloaderTast implements Runnable {

		public HttpDownloaderTast(String url, HttpDownloadListener l,
				String path, String fileName) {
			mListener = l;
			mUrl = url;
			if (!path.endsWith(File.separator))
				path = path + File.separator;
			mPath = path + fileName;

			buffer = new byte[8 * 1024];

			mFile = new File(mPath);
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			InputStream in = null;
			OutputStream out = null;
			try {
				URL url = new URL(mUrl);
				URLConnection conn = url.openConnection();

				conn.setConnectTimeout(10000);
				conn.setReadTimeout(20000);
				conn.connect();

				fileSize = conn.getHeaderFieldInt("Content-Length", -1);
				if (fileSize >= 0) {
					mListener.onDownloadStart(fileSize);
				} else {
					// for chunked transfer encoding
					mListener.onDownloadStart(-1);
				}

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

					mListener.onDownloading(downloadSize);

				}

				mListener.onDownloadSuccess();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mListener.onDownloadFailed();
			} catch (IOException e) {
				mFile.delete();
				e.printStackTrace();
				mListener.onDownloadFailed();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if (out != null) {
					try {
						out.flush();
						out.close();
					} catch (IOException e) {
					}
				}
			}
		}

		protected byte[] buffer;

		private String mUrl;
		private HttpDownloadListener mListener;
		private String mPath;

		private File mFile;

		private int fileSize;

	}

	public void download(String url, HttpDownloadListener l, String path,
			String fileName) {
		httpExecutor.execute(new HttpDownloaderTast(url, l, path, fileName));
	}

	public String download(String url, HttpDownloadListener l, String pathOnly) {
		String fileName = "MozzFiles_" + System.currentTimeMillis() + ".mozz";
		httpExecutor
				.execute(new HttpDownloaderTast(url, l, pathOnly, fileName));
		return fileName;
	}
}
