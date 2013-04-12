package com.inmap.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

	public static void loadURL(String urlString, InputStreamHandler handler) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			handler.handleInputStream(urlConnection.getInputStream());
		} finally {
			urlConnection.disconnect();
		}
	}
}
