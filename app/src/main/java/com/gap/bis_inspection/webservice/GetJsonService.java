package com.gap.bis_inspection.webservice;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GetJsonService {

	public GetJsonService() {
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	public String JsonReguest(String url) {
		String json = "";
		String result = "";
		 String urls = null;
		

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
			// only for gingerbread and newer versions
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		} else {

		}
		
		urls=url.replace(" ","%20");
		url=urls;
		HttpClient httpclient = new DefaultHttpClient();

		// Prepare a request object

		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("Accept", "application/json");
		httpget.setHeader("Content-Type", "application/json");

		HttpResponse response;
		try {

			response = httpclient.execute(httpget);
			response.setHeader("Content-Type", "UTF-8");
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();

				result = convertStreamToString(instream);
				

			}
		} catch (Exception e) {
			Log.e("Error", e.toString());
		}
		return result;
	}

	public static String convertStreamToString(InputStream is)
			throws UnsupportedEncodingException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"utf-8"));

		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}



}
