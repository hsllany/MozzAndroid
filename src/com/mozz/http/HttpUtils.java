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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

/**
 * Http tools for Android application
 * 
 * @author Yang & Zd <hsllany@163.com & zhangdao@>
 * 
 */
public class HttpUtils {

	private static final String DEBUG_TAG = "HttpUtils";

	private static final String AGENT = "mozz";

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

	public HttpUtils() {
		if (httpExecutor == null) {
			httpExecutor = Executors.newCachedThreadPool();
			mInstanceCount.addAndGet(1);
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

	public void download(String url, HttpDownloadListener l, String path,
			String fileName) {
		httpExecutor.execute(new HttpDownloaderTask(url, l, path, fileName));
	}

	public String download(String url, HttpDownloadListener l, String pathOnly) {
		String fileName = "MozzFiles_" + System.currentTimeMillis() + ".mozz";
		httpExecutor
				.execute(new HttpDownloaderTask(url, l, pathOnly, fileName));
		return fileName;
	}

	public void upload(String url, HttpUploadFileListener l,
			Map<String, File> fileList, Map<String, String> postData) {
		httpExecutor.execute(new HttpPostFile(url, fileList, postData, l));
	}

	public void upload(String url, HttpUploadFileListener l, File file,
			Map<String, String> postData) {
		Map<String, File> fileList = new HashMap<String, File>();
		fileList.put("file", file);
		upload(url, l, fileList, postData);
	}

	public void upload(String url, HttpUploadFileListener l,
			Map<String, File> fileList) {
		upload(url, l, fileList, null);
	}

	public void upload(String url, HttpUploadFileListener l, File file) {
		Map<String, File> fileList = new HashMap<String, File>();
		fileList.put("file", file);
		upload(url, l, fileList);
	}

	/**
	 * If you met any situation that should use full compacity of CPU, you
	 * should release the HttpUtils
	 */
	private static void release() {
		mInstanceCount.decrementAndGet();
		if (mInstanceCount.get() == 0) {
			httpExecutor.shutdown();
			httpExecutor = null;
		}
	}

	/**
	 * @see release()
	 */
	public void releaseHttp() {
		release();
	}

	private static class HttpPost implements Runnable {
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
			OutputStreamWriter osw = null;
			InputStream in = null;
			BufferedReader br = null;
			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();

				setURLConnectionParameters("post", urlConnection);

				if (postData != null && postData.length() > 512)
					urlConnection.setChunkedStreamingMode(5);
				urlConnection.connect();

				if (postData != null) {
					osw = new OutputStreamWriter(
							urlConnection.getOutputStream(), "UTF-8");
					osw.write(postData);
					osw.flush();
				}

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
					osw.close();
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

	}

	/**
	 * get Task
	 * 
	 * @author Yang Tao
	 * 
	 */
	private static class HttpGet implements Runnable {

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
			InputStream in = null;
			BufferedReader br = null;

			try {
				url = new URL(mURL);
				urlConnection = (HttpURLConnection) url.openConnection();

				setURLConnectionParameters("get", urlConnection);

				urlConnection.connect();
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
	}

	/**
	 * download file task
	 * 
	 * @author YangTao
	 * 
	 */
	private static class HttpDownloaderTask implements Runnable {

		public HttpDownloaderTask(String url, HttpDownloadListener l,
				String path, String fileName) {
			mListener = l;
			mUrl = url;
			if (!path.endsWith(File.separator))
				path = path + File.separator;
			mPath = path + fileName;
			
			buffer = new byte[8 * 1024];
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			mFile = new File(mPath);
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

				conn.setConnectTimeout(10000);
				conn.setReadTimeout(20000);
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
						mListener.onDownloading(downloadSize,(float)downloadSize/(float)fileSize);
						Log.d("Downloading", ""+downloadSize);
					}

				}

				out.flush();

				if (mListener != null) {
					mListener.onDownloadSuccess();
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	}

	private static class HttpPostFile implements Runnable {
		private Map<String, File> mFiles;
		private String mUrl;
		private Map<String, String> mPostdata;
		private HttpUploadFileListener mListener;

		public HttpPostFile(String url, Map<String, File> files,
				Map<String, String> postData, HttpUploadFileListener listener) {
			mUrl = url;
			mFiles = files;
			mPostdata = postData;
			mListener = listener;
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

				urlConnection.setRequestMethod("POST");
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setUseCaches(false);

				urlConnection.setRequestProperty("Connection", "Keep-Alive");
				urlConnection.setRequestProperty("Charset", CHARSET);
				urlConnection.setRequestProperty("Content-Type",
						MUTIPART_FORMDATA + ";boundary=" + BOUNDARY);

				dos = new DataOutputStream(urlConnection.getOutputStream());

				String formdataNormal = buildDataformPostdata(mPostdata);
				if (formdataNormal != null) {
					dos.write(formdataNormal.getBytes());
				}

				long allFileSize = 0;

				Iterator<Entry<String, File>> itr = mFiles.entrySet()
						.iterator();
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
							.append("Content-Disposition: form-data;"
									+ " name=\"" + key + "\";" + "filename=\""
									+ file.getName() + "\"" + CRLF)
							.append("Content-Type:" + CONTENTTYPE).append(CRLF)
							.append(CRLF);

					dos.write(sb.toString().getBytes());

					FileInputStream fis = new FileInputStream(file);

					int len = 0;

					while ((len = fis.read(writeBuffer)) != -1) {
						dos.write(writeBuffer, 0, len);
						completeSize += len;

						if (mListener != null) {
							mListener.onUploading(completeSize);
						}
					}
					dos.write(CRLF.getBytes());
					fis.close();
				}

				dos.write((PREFIX + BOUNDARY + PREFIX + CRLF).getBytes());
				dos.flush();

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

	private static void setURLConnectionParameters(String method,
			HttpURLConnection urlConnection) {

		urlConnection.setConnectTimeout(2000);
		urlConnection.setReadTimeout(2000);

		if (method.equalsIgnoreCase("get")) {
			try {
				urlConnection.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);

		} else if (method.equalsIgnoreCase("POST")) {
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			try {
				urlConnection.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);
		}
	}

	private static String buildDataformPostdata(Map<String, String> postData) {

		if (postData != null) {
			Iterator<Entry<String, String>> itr = postData.entrySet()
					.iterator();

			StringBuilder builder = new StringBuilder();

			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();

				builder.append(PREFIX + BOUNDARY + CRLF);
				builder.append("Content-Disposition:form-data;name=\""
						+ entry.getKey() + "\"" + CRLF + CRLF);
				builder.append(entry.getValue() + CRLF);
			}
			return builder.toString();
		}
		return null;

	}
}
