package com.shitu.httputils.libs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Http tools for Android application
 * 
 * @author Yang & Zd <hsllany@163.com & zhangdao@>
 * 
 */
public class HttpUtils {
	/**
	 * The executor for run http request
	 */
	protected final static ExecutorService httpExecutor = Executors
			.newCachedThreadPool();

	/**
	 * stores the listener
	 */
	protected final static Hashtable<String, HttpListener> listenerPool = new Hashtable<String, HttpListener>();

	/**
	 * stores the future
	 */
	protected final static Hashtable<String, Future<HttpResponse>> futureList = new Hashtable<String, Future<HttpResponse>>();

	public HttpUtils() {
		new Thread() {
			public void run() {
				while (isRun) {
					synchronized (futureList) {

						Iterator iterator = futureList.entrySet().iterator();
						while (iterator.hasNext()) {
							Map.Entry<String, Future<HttpResponse>> entry = (Map.Entry<String, Future<HttpResponse>>) iterator
									.next();
							Future<HttpResponse> future = entry.getValue();
							String key = (String) entry.getKey();

							if (future.isDone()) {
								try {
									listenerPool.get(key).onGet(future.get());
								} catch (InterruptedException
										| ExecutionException e) {
									e.printStackTrace();
								}

								iterator.remove();
							}
						}
					}

				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * after application finish, remember to invoke the close()
	 */
	public void close() {
		this.isRun = false;
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
		listenerPool.put(url, l);
		futureList.put(url, httpExecutor.submit(new HttpTask(url)));
	}

	protected boolean isRun = true;

	class HttpTask implements Callable<HttpResponse> {

		public HttpTask(String address) {
			this.mURL = address;
		}

		@Override
		public HttpResponse call() throws Exception {
			HttpResponse response = new HttpResponse();
			response.html = null;
			response.status = 101;
			StringBuilder sb = new StringBuilder();
			URL url;
			try {
				url = new URL(mURL);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				urlConnection.connect();
				InputStream in = urlConnection.getInputStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				in.close();
				urlConnection.disconnect();

				response.html = sb.toString();
				response.status = 200;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return response;
		}

		private String mURL;
	}

}
