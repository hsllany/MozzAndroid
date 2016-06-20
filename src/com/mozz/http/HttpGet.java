package com.mozz.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.mozz.http.HttpUtils.*;

class HttpGet extends HttpTask {

	public HttpGet(String address, HttpListener l, HttpParameter parameter, List<HttpTask> tasks) {
		super(tasks);
		this.mURL = address;
		this.mListener = l;
		this.parameter = parameter;
	}

	@Override
	public void run() {
		super.run();
		HttpResponse response = null;
		StringBuilder sb = new StringBuilder();
		HttpURLConnection urlConnection = null;
		URL url = null;
		InputStream in = null;
		BufferedReader br = null;

		try {
			url = new URL(mURL);
			urlConnection = (HttpURLConnection) url.openConnection();
			HttpHelper.setURLConnectionParameters(GET, urlConnection, this.parameter);

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
