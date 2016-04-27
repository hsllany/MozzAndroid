package com.mozz.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

	private static final int GET = 0x01;
	private static final int POST = 0x02;
	private static final int DOWNLOAD_FILE = 0x05;
	private static final int UPLOAD_FILE = 0x06;

	/**
	 * for only debug
	 */
	private static final boolean DEBUG = false;
	private static final String DEBUG_TAG = "HttpUtils";

	/**
	 * Post data chars
	 */
	private static final String CRLF = "\r\n";
	private final static String BOUNDARY = "MOZZEEDN9JOFxnp3Q8_2lXBfEexEE_Rnsey";
	private final static String PREFIX = "--";
	private final static String MUTIPART_FORMDATA = "multipart/form-data";
	private final static String CHARSET = "UTF-8";
	private final static String CONTENTTYPE = "application/octet-stream";

	private static AtomicInteger mInstanceCount = new AtomicInteger(0);

	/**
	 * The executor for run http request
	 */
	protected static ExecutorService httpExecutor;

	/**
	 * global HttpParameter
	 */
	protected static HttpParameter globleParameters;

	/**
	 * default HttpParameter
	 */
	protected static final HttpParameter defaultParamter;

	static {
		defaultParamter = new HttpParameter(3000, 5000);
		globleParameters = defaultParamter;
	}

	public HttpUtils() {
		if (httpExecutor == null) {
			httpExecutor = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);
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

	/**
	 * Get method using global parameter.
	 * 
	 * @see #updateGlobalParameter(HttpParameter)
	 * @see #get(String, HttpListener, HttpParameter)
	 */
	public void get(String url, HttpListener l) {
		httpExecutor.execute(new HttpGet(url, l, globleParameters));
	}

	/**
	 * get method, using specific HttpParameter.
	 * 
	 * @param url
	 * @param l
	 * @param parameter
	 */
	public void get(String url, HttpListener l, HttpParameter parameter) {
		httpExecutor.execute(new HttpGet(url, l, parameter));
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

			httpExecutor.execute(new HttpPost(url, l, sb.toString(), parameter));
		} else {
			httpExecutor.execute(new HttpPost(url, l, null, parameter));
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
		httpExecutor.execute(new HttpDownloaderTask(url, l, path, fileName, parameter));
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
		httpExecutor.execute(new HttpPostFile(url, fileList, postData, l, parameter));
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
			httpExecutor.shutdown();
			httpExecutor = null;
		}
	}

	/**
	 * Release all resources.
	 */
	public void shutdown() {
		release();
	}

	private static class HttpPost implements Runnable {
		public HttpPost(String address, HttpListener l, String postData, HttpParameter httpParameter) {
			this.mListener = l;
			this.mURL = address;
			this.postData = postData;
			this.parameter = httpParameter;
		}

		@Override
		public void run() {
			HttpResponse response = null;
			StringBuilder sb = new StringBuilder();
			HttpURLConnection urlConnection = null;
			URL url = null;
			OutputStreamWriter osw = null;
			InputStream in = null;
			BufferedReader br = null;
			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				setURLConnectionParameters(POST, urlConnection, this.parameter);

				if (postData != null && postData.length() > 512)
					urlConnection.setChunkedStreamingMode(5);
				urlConnection.connect();

				if (postData != null) {
					osw = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
					osw.write(postData);
					osw.flush();
				}

				int status = urlConnection.getResponseCode();

				if (status >= 400)
					in = urlConnection.getErrorStream();
				else
					in = urlConnection.getInputStream();

				br = new BufferedReader(new InputStreamReader(in));

				String line = null;

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

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
				try {
					if (osw != null) {
						osw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (in != null)
								in.close();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (urlConnection != null)
								urlConnection.disconnect();
						}
					}
				}

			}
		}

		private String mURL;
		private HttpListener mListener;
		private String postData;
		private HttpParameter parameter;

	}

	/**
	 * Get
	 * 
	 * @author shitupublic
	 *
	 */
	private static class HttpGet implements Runnable {

		public HttpGet(String address, HttpListener l, HttpParameter parameter) {
			this.mURL = address;
			this.mListener = l;
			this.parameter = parameter;
		}

		@Override
		public void run() {
			HttpResponse response = null;
			StringBuilder sb = new StringBuilder();
			HttpURLConnection urlConnection = null;
			URL url = null;
			InputStream in = null;
			BufferedReader br = null;

			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				setURLConnectionParameters(GET, urlConnection, this.parameter);

				urlConnection.connect();
				int status = urlConnection.getResponseCode();

				if (status >= 400)
					in = urlConnection.getErrorStream();
				else
					in = urlConnection.getInputStream();

				br = new BufferedReader(new InputStreamReader(in));

				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				in.close();

				response = new HttpResponse();
				response.setEntity(sb.toString());
				response.setStatus(urlConnection.getResponseCode());
				if (mListener != null)
					mListener.onSuccess(response);
			} catch (Exception e) {
				e.printStackTrace();
				if (mListener != null)
					mListener.onFail(e);
			} finally {

				try {
					if (br != null)
						br.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (in != null)
							in.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (urlConnection != null)
							urlConnection.disconnect();
					}
				}

			}
		}

		private String mURL;
		private HttpListener mListener;
		private HttpParameter parameter;
	}

	/**
	 * download file task
	 * 
	 * @author YangTao
	 * 
	 */
	private static class HttpDownloaderTask implements Runnable {

		public HttpDownloaderTask(String url, HttpDownloadListener l, String path, String fileName, HttpParameter parameter) {
			mListener = l;
			mUrl = urlEncording(url);
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
			InputStream in = null;
			OutputStream out = null;
			HttpURLConnection conn = null;
			try {
				mFile.createNewFile();
				URL url = new URL(mUrl);
				conn = (HttpURLConnection) url.openConnection();
				setURLConnectionParameters(DOWNLOAD_FILE, conn, this.paramter);
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

	private static class HttpPostFile implements Runnable {
		private Map<String, File> mFiles;
		private String mUrl;
		private Map<String, String> mPostdata;
		private HttpUploadFileListener mListener;
		private HttpParameter parameter;

		public HttpPostFile(String url, Map<String, File> files, Map<String, String> postData, HttpUploadFileListener listener,
				HttpParameter parameter) {
			mUrl = url;
			mFiles = files;
			mPostdata = postData;
			mListener = listener;
			this.parameter = parameter;
		}

		@Override
		public void run() {
			URL postURL;
			DataOutputStream dos = null;
			HttpResponse response = null;
			InputStream in = null;
			BufferedReader br = null;
			HttpURLConnection urlConnection = null;
			try {
				postURL = new URL(mUrl);
				urlConnection = (HttpURLConnection) postURL.openConnection();
				setURLConnectionParameters(UPLOAD_FILE, urlConnection, this.parameter);
				dos = new DataOutputStream(urlConnection.getOutputStream());

				String formdataNormal = buildDataformPostdata(mPostdata);
				if (formdataNormal != null) {
					dos.write(formdataNormal.getBytes());
				}

				long allFileSize = 0;

				Iterator<Entry<String, File>> itr = mFiles.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<String, File> entry = itr.next();
					File file = entry.getValue();
					allFileSize += file.length();
				}

				if (mListener != null) {
					mListener.onUploadStart(allFileSize);
				}

				byte[] writeBuffer = new byte[1024 * 2];

				long completeSize = 0;

				itr = mFiles.entrySet().iterator();
				while (itr.hasNext()) {
					Entry<String, File> entry = itr.next();

					File file = entry.getValue();
					String key = entry.getKey();

					StringBuffer sb = new StringBuffer("");
					sb.append(PREFIX + BOUNDARY + CRLF)
							.append("Content-Disposition: form-data;" + " name=\"" + key + "\";" + "filename=\"" + file.getName() + "\"" + CRLF)
							.append("Content-Type:" + CONTENTTYPE).append(CRLF).append(CRLF);

					dos.write(sb.toString().getBytes());

					FileInputStream fis = new FileInputStream(file);

					int len = 0;
					int j = 0;
					while ((len = fis.read(writeBuffer)) != -1) {
						dos.write(writeBuffer, 0, len);
						dos.flush();
						completeSize += len;
						if (j++ % 5 == 0 && mListener != null)
							mListener.onUploading(completeSize, (float) completeSize / (float) allFileSize);
					}
					dos.write(CRLF.getBytes());

					fis.close();
				}

				dos.write((PREFIX + BOUNDARY + PREFIX + CRLF).getBytes());
				dos.flush();
				if (mListener != null)
					mListener.onUploading(completeSize, 1);
				StringBuffer sb = new StringBuffer();
				in = urlConnection.getInputStream();
				br = new BufferedReader(new InputStreamReader(in));

				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				response = new HttpResponse();
				response.setEntity(sb.toString());
				response.setStatus(urlConnection.getResponseCode());

				mListener.onSuccess(response);

			} catch (Exception e) {
				if (mListener != null) {
					mListener.onFail(e);
				}
			} finally {
				try {
					if (dos != null)
						dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {

					try {
						if (br != null)
							br.close();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (in != null)
								in.close();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (urlConnection != null)
								urlConnection.disconnect();
						}
					}
				}
			}
		}

	}

	private static void setURLConnectionParameters(int methodHint, HttpURLConnection urlConnection, HttpParameter parameter) {

		urlConnection.setConnectTimeout(parameter.connectTimeOut);
		urlConnection.setReadTimeout(parameter.soTimeOut);
		urlConnection.setRequestProperty("User-Agent", parameter.userAgent);

		switch (methodHint) {
		case GET:
			try {
				urlConnection.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);
			break;
		case POST:
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			try {
				urlConnection.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);
			break;
		case UPLOAD_FILE:
			urlConnection.setConnectTimeout(parameter.connectTimeOut);
			urlConnection.setReadTimeout(parameter.soTimeOut);

			try {
				urlConnection.setRequestMethod("POST");
			} catch (ProtocolException e) {
			}
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setChunkedStreamingMode(1024);

			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", CHARSET);
			urlConnection.setRequestProperty("Content-Type", MUTIPART_FORMDATA + ";boundary=" + BOUNDARY);
			break;
		case DOWNLOAD_FILE:
		}
	}

	private static String urlEncording(String url) {
		String resultUrl = url;
		try {

			int lastSlash = url.lastIndexOf("/");
			String pureUrl = url.substring(0, lastSlash + 1);
			String fileName = URLEncoder.encode(url.substring(lastSlash + 1), "UTF-8");
			resultUrl = pureUrl + fileName;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resultUrl;

	}

	private static String buildDataformPostdata(Map<String, String> postData) {
		if (postData != null) {
			Iterator<Entry<String, String>> itr = postData.entrySet().iterator();

			StringBuilder builder = new StringBuilder();

			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();

				builder.append(PREFIX + BOUNDARY + CRLF);
				builder.append("Content-Disposition:form-data;name=\"" + entry.getKey() + "\"" + CRLF + CRLF);
				builder.append(entry.getValue() + CRLF);
			}
			return builder.toString();
		}
		return null;

	}

	public static void debug(String msg) {
		if (DEBUG) {
			Log.d(DEBUG_TAG, msg);
		}
	}
}
