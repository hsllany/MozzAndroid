package com.mozz.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Http tools for Android application
 * 
 * @author Yang & Zd <hsllany@163.com & zhangdao@>
 * 
 */
public class HttpUtils {

	private String DEBUG_TAG = this.getClass().getSimpleName();
	/**
	 * The executor for run http request
	 */
	protected static ExecutorService httpExecutor;

	public HttpUtils() {
		if (httpExecutor == null) {
			httpExecutor = Executors.newCachedThreadPool();
		}
	}

	/**
	 * Http Get function
	 * 
	 * @param url
	 *            , string
	 * @param l
	 *            , HttpListener
	 */
	public void get(String url, HttpListener l) {
		httpExecutor.execute(new HttpGet(url, l));
	}

	/**
	 * If you met any situation that should use full compacity of CPU, you
	 * should release the HttpUtils
	 */
	public static void release() {
		httpExecutor.shutdown();
		httpExecutor = null;
	}

	/**
	 * @see release()
	 */
	public void releaseHttp() {
		release();
	}

	/**
	 * Http Post function
	 * 
	 * @param url
	 *            , String
	 * @param l
	 *            , HttpListener
	 * @param parameters
	 *            , HashMap<String, String> that contains name field and value
	 *            field of post data;
	 */
	public void post(String url, HttpListener l, Map<String, String> parameters) {

		if (parameters != null) {
			StringBuilder sb = new StringBuilder();
			Iterator<Map.Entry<String, String>> itr = parameters.entrySet()
					.iterator();

			int i = 0;

			while (itr.hasNext()) {
				Map.Entry<String, String> entry = itr.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if (i == 0)
					sb.append(key + "=" + value);
				else
					sb.append("&" + key + "=" + value);

				i++;
			}

			httpExecutor.execute(new HttpPost(url, l, sb.toString()));
		} else {
			httpExecutor.execute(new HttpPost(url, l, null));
		}

	}

	class HttpPost implements Runnable {
		public HttpPost(String address, HttpListener l, String parameters) {
			this.mListener = l;
			this.mURL = address;
			this.postData = parameters;
		}

		@Override
		public void run() {
			HttpResponse response = null;
			StringBuilder sb = new StringBuilder();
			HttpURLConnection urlConnection = null;
			URL url = null;

			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false);
				if (postData != null && postData.length() > 512)
					urlConnection.setChunkedStreamingMode(5);
				urlConnection.connect();

				if (postData != null) {
					OutputStreamWriter osw = new OutputStreamWriter(
							urlConnection.getOutputStream(), "UTF-8");
					osw.write(postData);
					osw.flush();
					osw.close();
				}

				InputStream in = urlConnection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				in.close();

				response = new HttpResponse();
				response.setEntity(sb.toString());
				response.setStatus(urlConnection.getResponseCode());

				mListener.onSuccess(response);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mListener.onFail(e);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onFail(e);
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
		}

		private String mURL;
		private HttpListener mListener;
		private String postData;

	}

	class HttpGet implements Runnable {

		public HttpGet(String address, HttpListener l) {
			this.mURL = address;
			this.mListener = l;
		}

		@Override
		public void run() {
			HttpResponse response = null;
			StringBuilder sb = new StringBuilder();
			HttpURLConnection urlConnection = null;
			URL url = null;

			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setUseCaches(true);
				urlConnection.connect();
				InputStream in = urlConnection.getInputStream();

				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				in.close();

				response = new HttpResponse();
				response.setEntity(sb.toString());
				response.setStatus(urlConnection.getResponseCode());

				mListener.onSuccess(response);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				mListener.onFail(e);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onFail(e);
			} finally {
				if (urlConnection != null)
					urlConnection.disconnect();
			}
		}

		private String mURL;
		private HttpListener mListener;
	}

}
