package com.mozz.http;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class HttpHelper {

	private HttpHelper() {
		throw new UnsupportedOperationException("can't create a HttpHelper object");
	}

	static String buildDataformPostdata(Map<String, String> postData) {
		if (postData != null) {
			Iterator<Entry<String, String>> itr = postData.entrySet().iterator();

			StringBuilder builder = new StringBuilder();

			while (itr.hasNext()) {
				Entry<String, String> entry = itr.next();

				builder.append(HttpUtils.PREFIX + HttpUtils.BOUNDARY + HttpUtils.CRLF);
				builder.append("Content-Disposition:form-data;name=\"" + entry.getKey() + "\"" + HttpUtils.CRLF + HttpUtils.CRLF);
				builder.append(entry.getValue() + HttpUtils.CRLF);
			}
			return builder.toString();
		}
		return null;

	}

	static void setURLConnectionParameters(int methodHint, HttpURLConnection urlConnection, HttpParameter parameter) {

		urlConnection.setConnectTimeout(parameter.connectTimeOut);
		urlConnection.setReadTimeout(parameter.soTimeOut);
		urlConnection.setRequestProperty("User-Agent", parameter.userAgent);

		switch (methodHint) {
		case HttpUtils.GET:
			try {
				urlConnection.setRequestMethod("GET");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);
			break;
		case HttpUtils.POST:
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			try {
				urlConnection.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
			}
			urlConnection.setUseCaches(false);
			break;
		case HttpUtils.UPLOAD_FILE:
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
			urlConnection.setRequestProperty("Charset", HttpUtils.CHARSET);
			urlConnection.setRequestProperty("Content-Type", HttpUtils.MUTIPART_FORMDATA + ";boundary=" + HttpUtils.BOUNDARY);
			break;
		case HttpUtils.DOWNLOAD_FILE:
		}
	}

	static String urlEncording(String url) {
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

}
