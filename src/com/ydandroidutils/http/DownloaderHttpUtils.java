package com.ydandroidutils.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloaderHttpUtils extends HttpUtils {
	class HttpDownloaderTast implements Runnable {

		public HttpDownloaderTast(String url, HttpDownloadListener l,
				String path) {
			mListener = l;
			mUrl = url;
			mPath = path;

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

				fileSize = conn.getHeaderFieldInt("Content-Length", 0);

				mListener.onStart(fileSize);

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

				mListener.onFinish(200);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.out.println("MALFORED");
			} catch (IOException e) {
				mFile.delete();
				e.printStackTrace();
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

	public void download(String url, HttpDownloadListener l, String path) {
		httpExecutor.execute(new HttpDownloaderTast(url, l, path));
	}
}
