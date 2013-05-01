package com.contralabs.inmap.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

	public static boolean isOnline(Context context) {
		NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(activeNetworkInfo != null)
			return activeNetworkInfo.isConnected();
		return false;
	}
}
