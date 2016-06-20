package com.mozz.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.mozz.http.HttpUtils.*;

class HttpPost extends HttpTask {
	public HttpPost(String address, HttpListener l, String postData, HttpParameter httpParameter, List<HttpTask> tasks) {
		super(tasks);
		this.mListener = l;
		this.mURL = address;
		this.postData = postData;
		this.parameter = httpParameter;
	}

	@Override
	public void run() {
		super.run();
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
			HttpHelper.setURLConnectionParameters(POST, urlConnection, this.parameter);

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
