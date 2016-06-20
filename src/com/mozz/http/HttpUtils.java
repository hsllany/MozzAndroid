package com.mozz.http;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

/**
 * HTTP tools for Android application
 * 
 * @author hsllany
 * 
 */
public class HttpUtils {
	/**
	 * Executor parameters, reference of {@code AsyncTask}
	 */
	static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

	static final int GET = 0x01;
	static final int POST = 0x02;
	static final int DOWNLOAD_FILE = 0x05;
	static final int UPLOAD_FILE = 0x06;

	/**
	 * for only debug
	 */
	private static final boolean DEBUG = false;
	private static final String DEBUG_TAG = "HttpUtils";

	/**
	 * Post data chars
	 */
	static final String CRLF = "\r\n";
	final static String BOUNDARY = "MOZZEEDN9JOFxnp3Q8_2lXBfEexEE_Rnsey";
	final static String PREFIX = "--";
	final static String MUTIPART_FORMDATA = "multipart/form-data";
	final static String CHARSET = "UTF-8";
	final static String CONTENTTYPE = "application/octet-stream";

	private static AtomicInteger mInstanceCount = new AtomicInteger(0);

	/**
	 * The executor for run http request
	 */
	protected static ThreadPoolExecutor httpExecutor;

	/**
	 * global HttpParameter
	 */
	protected static HttpParameter globleParameters;

	/**
	 * default HttpParameter
	 */
	protected static final HttpParameter defaultParamter;

	private static BlockingQueue<Runnable> sWorkingQueue = new LinkedBlockingQueue<Runnable>();

	static {
		defaultParamter = new HttpParameter(3000, 5000);
		globleParameters = defaultParamter;
	}

	private List<HttpTask> mTasks = new LinkedList<HttpTask>();

	public HttpUtils() {
		if (httpExecutor == null) {
			httpExecutor = new ThreadPoolExecutor(CPU_COUNT, MAXIMUM_POOL_SIZE, 1, TimeUnit.SECONDS, sWorkingQueue);
			mInstanceCount.addAndGet(1);
		}
	}

	/**
	 * update global http parameter
	 * 
	 * @param parameter
	 */
	public static void updateGlobalParameter(HttpParameter parameter) {
		if (parameter != null)
			globleParameters = parameter;
		else
			throw new NullPointerException("HttpParameter can't be null.");
	}

	private void execute(HttpTask task) {
		httpExecutor.execute(task);
	}

	/**
	 * Get method using global parameter.
	 * 
	 * @see #updateGlobalParameter(HttpParameter)
	 * @see #get(String, HttpListener, HttpParameter)
	 */
	public void get(String url, HttpListener l) {
		execute(new HttpGet(url, l, globleParameters, mTasks));
	}

	/**
	 * get method, using specific HttpParameter.
	 * 
	 * @param url
	 * @param l
	 * @param parameter
	 */
	public void get(String url, HttpListener l, HttpParameter parameter) {
		execute(new HttpGet(url, l, parameter, mTasks));
	}

	/**
	 * Post method using specific HttpParameter
	 * 
	 * @see #post(String, HttpListener, Map)
	 * @param postData
	 *            , Map<String, String> that contains name field and value field
	 *            of post data;
	 */
	public void post(String url, HttpListener l, Map<String, String> postData, HttpParameter parameter) {

		if (postData != null) {
			StringBuilder sb = new StringBuilder();
			Iterator<Map.Entry<String, String>> itr = postData.entrySet().iterator();

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

			execute(new HttpPost(url, l, sb.toString(), parameter, mTasks));
		} else {
			execute(new HttpPost(url, l, null, parameter, mTasks));
		}
	}

	/**
	 * Post method using global http parameter
	 * 
	 * @see #post(String, HttpListener, Map, HttpParameter)
	 * @see #updateGlobalParameter(HttpParameter)
	 */
	public void post(String url, HttpListener l, Map<String, String> postData) {
		this.post(url, l, postData, globleParameters);
	}

	/**
	 * Download file and store it at 'path/fileName', using specific http
	 * parameter
	 * 
	 * @see #download(String, HttpDownloadListener, String, String)
	 * 
	 */
	public void download(String url, HttpDownloadListener l, String path, String fileName, HttpParameter parameter) {
		execute(new HttpDownload(url, l, path, fileName, parameter, mTasks));
	}

	/**
	 * Download file and store it at 'path/fileName', using specific http
	 * parameter
	 * 
	 * @see #download(String, HttpDownloadListener, String, String,
	 *      HttpParameter)
	 * @see #updateGlobalParameter(HttpParameter)
	 */
	public void download(String url, HttpDownloadListener l, String path, String fileName) {
		this.download(url, l, path, fileName, globleParameters);
	}

	/**
	 * Post file and data to URL with fileList
	 * 
	 * @see #upload(String, HttpUploadFileListener, Map, Map)
	 * @see #upload(String, HttpUploadFileListener, File)
	 * @see #upload(String, HttpUploadFileListener, File, HttpParameter)
	 * 
	 */
	public void upload(String url, HttpUploadFileListener l, Map<String, File> fileList, Map<String, String> postData, HttpParameter parameter) {
		execute(new HttpPostFile(url, fileList, postData, l, parameter, mTasks));
	}

	/**
	 * Post single file to URL
	 * 
	 * @see #upload(String, HttpUploadFileListener, Map, Map, HttpParameter)
	 * @see #upload(String, HttpUploadFileListener, Map, Map)
	 * @see #upload(String, HttpUploadFileListener, File)
	 */
	public void upload(String url, HttpUploadFileListener l, File file, HttpParameter parameter) {
		Map<String, File> fileList = new HashMap<String, File>();
		fileList.put("file", file);
		upload(url, l, fileList, null, parameter);
	}

	/**
	 * Post file and data to URL using global http parameter
	 * 
	 * @see #upload(String, HttpUploadFileListener, Map, Map, HttpParameter)
	 * @see #upload(String, HttpUploadFileListener, File, HttpParameter)
	 * @see #upload(String, HttpUploadFileListener, File)
	 */
	public void upload(String url, HttpUploadFileListener l, Map<String, File> fileList, Map<String, String> postData) {
		this.upload(url, l, fileList, postData, globleParameters);
	}

	/**
	 * Post single file to URL using global http parameter
	 *
	 * @see #upload(String, HttpUploadFileListener, File, HttpParameter)
	 * @see #upload(String, HttpUploadFileListener, Map, Map)
	 * @see #upload(String, HttpUploadFileListener, Map, Map, HttpParameter)
	 */
	public void upload(String url, HttpUploadFileListener l, File file) {
		this.upload(url, l, file, globleParameters);
	}

	/**
	 * If you met any situation that should use full compacity of CPU, you
	 * should {@code shutdown()}
	 */
	private static void release() {

		if (mInstanceCount.decrementAndGet() == 0) {
			synchronized (sWorkingQueue) {
				sWorkingQueue.clear();
			}
			httpExecutor.shutdown();
			httpExecutor = null;
		}
	}

	/**
	 * Release all resources.
	 */
	public void shutdown() {

		synchronized (this) {
			Iterator<HttpTask> itr = mTasks.iterator();

			while (itr.hasNext()) {
				HttpTask task = itr.next();
				if (task.getRunStatus() == HttpTask.WAIT) {
					synchronized (sWorkingQueue) {
						if (sWorkingQueue.contains(task))
							sWorkingQueue.remove(task);
					}

				}

				itr.remove();
			}
		}
		release();
	}

	public static void debug(String msg) {
		if (DEBUG) {
			Log.d(DEBUG_TAG, msg);
		}
	}
}
