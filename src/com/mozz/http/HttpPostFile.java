package com.mozz.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.mozz.http.HttpUtils.*;

class HttpPostFile extends HttpTask {
	private Map<String, File> mFiles;
	private String mUrl;
	private Map<String, String> mPostdata;
	private HttpUploadFileListener mListener;
	private HttpParameter parameter;

	public HttpPostFile(String url, Map<String, File> files, Map<String, String> postData, HttpUploadFileListener listener, HttpParameter parameter,
			List<HttpTask> tasks) {
		super(tasks);
		mUrl = url;
		mFiles = files;
		mPostdata = postData;
		mListener = listener;
		this.parameter = parameter;
	}

	@Override
	public void run() {
		super.run();
		URL postURL;
		DataOutputStream dos = null;
		HttpResponse response = null;
		InputStream in = null;
		BufferedReader br = null;
		HttpURLConnection urlConnection = null;
		try {
			postURL = new URL(mUrl);
			urlConnection = (HttpURLConnection) postURL.openConnection();
			HttpHelper.setURLConnectionParameters(UPLOAD_FILE, urlConnection, this.parameter);
			dos = new DataOutputStream(urlConnection.getOutputStream());

			String formdataNormal = HttpHelper.buildDataformPostdata(mPostdata);
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