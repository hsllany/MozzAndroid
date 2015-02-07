package com.shitu.httputils.libs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
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
	protected final static Hashtable<String, Future<String>> futureList = new Hashtable<String, Future<String>>();

	public HttpUtils() {
		new Thread() {
			public void run() {
				while (isRun) {
					for (String key : futureList.keySet()) {
						Future<String> future = futureList.get(key);
						if (future.isDone()) {
							futureList.remove(key);
							try {
								listenerPool.get(key).onGet(future.get());
							} catch (InterruptedException e) {
								e.printStackTrace();
							} catch (ExecutionException e) {
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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

	class HttpTask implements Callable<String> {

		public HttpTask(String address) {
			this.mURL = address;
		}

		@Override
		public String call() throws Exception {
			URL url;
			try {
				url = new URL(mURL);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();

				urlConnection.connect();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				urlConnection.disconnect();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		private String mURL;
	}

}
