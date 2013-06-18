package com.contralabs.inmap.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

public class WebUtils {

	public static Object loadURL(String urlString, InputStreamHandler handler) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			InputStream inputStream = urlConnection.getInputStream();
			if(handler == null)
				return null;
			return handler.handleInputStream(inputStream);
		} finally {
			urlConnection.disconnect();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void loadURLAsync(String urlString, InputStreamHandler handler, Receiver<T> receiver) {
		LoaderAsyncTask<T> asyncTask = new LoaderAsyncTask<T>(handler, receiver, (byte[]) null);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
		else
			asyncTask.execute(urlString);
	}

	public static boolean isOnline(Context context) {
		NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if(activeNetworkInfo != null)
			return activeNetworkInfo.isConnected();
		return false;
	}

	public static Object doPostRequest(String urlString, InputStreamHandler handler, byte[] data) throws IllegalStateException, IOException {
		ByteArrayEntity byteArrayEntity = new ByteArrayEntity(data);
		byteArrayEntity.setContentType("image/jpeg");//"multipart/form-data");
		return doPostRequest(urlString, handler, byteArrayEntity);
	}
	
	public static Object doPostRequest(String urlString, InputStreamHandler handler, List<? extends NameValuePair> parameters) throws IllegalStateException, IOException {
		return doPostRequest(urlString, handler, new UrlEncodedFormEntity(parameters, "UTF-8"));
	}
	
	public static Object doPostRequest(String urlString, InputStreamHandler handler, JSONObject json) throws IllegalStateException, IOException {
		StringEntity entity = new StringEntity(json.toString(), "UTF-8");
		entity.setContentType("application/json");
		return doPostRequest(urlString, handler, entity);
	}

	static Object doPostRequest(String urlString, InputStreamHandler handler, HttpEntity entity) throws IOException, ClientProtocolException {
		HttpPost httppost = new HttpPost(urlString);
		httppost.setEntity(entity);
		return doPostRequest(handler, httppost);
	} 

	static Object doPostRequest(InputStreamHandler handler, HttpPost httppost) throws IOException, ClientProtocolException {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		
		// Execute HTTP Post Request
		InputStream response = httpclient.execute(httppost).getEntity().getContent();

		try {
			if(handler == null)
				return null;
			return handler.handleInputStream(response);
		} finally {
			response.close();
		}
	} 
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void doPostRequestAsync(String urlString, InputStreamHandler handler, byte[] data, Receiver<T> receiver) {
		LoaderAsyncTask<T> asyncTask = new LoaderAsyncTask<T>(handler, receiver, data);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
		else
			asyncTask.execute(urlString);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void doPostRequestAsync(String urlString, InputStreamHandler handler, List<? extends NameValuePair> parameters, Receiver<T> receiver) {
		LoaderAsyncTask<T> asyncTask = new LoaderAsyncTask<T>(handler, receiver, parameters);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
		else
			asyncTask.execute(urlString);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <T> void doPostRequestAsync(String urlString, InputStreamHandler handler, JSONObject object, Receiver<T> receiver) {
		LoaderAsyncTask<T> asyncTask = new LoaderAsyncTask<T>(handler, receiver, object);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, urlString);
		else
			asyncTask.execute(urlString);
	}

	private static class LoaderAsyncTask<T> extends AsyncTask<String,Void,T>{

		private InputStreamHandler mInputStreamHandler;
		private final Receiver<T> mReceiver;
		private byte[] mData;
		private List<? extends NameValuePair> mParameters;
		private JSONObject mJSONObject;

		private LoaderAsyncTask(InputStreamHandler handler, Receiver<T> receiver, byte[] data) {
			mInputStreamHandler = handler;
			mReceiver = receiver;
			mData = data;
		}

		public LoaderAsyncTask(InputStreamHandler handler, Receiver<T> receiver, List<? extends NameValuePair> parameters) {
			mInputStreamHandler = handler;
			mReceiver = receiver;
			mParameters = parameters;
		}

		public LoaderAsyncTask(InputStreamHandler handler, Receiver<T> receiver, JSONObject object) {
			mInputStreamHandler = handler;
			mReceiver = receiver;
			mJSONObject = object;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T doInBackground(String... strings) {
			try {
				if(mData != null)
					return (T) doPostRequest(strings[0], mInputStreamHandler, mData);
				if(mParameters != null)
					return (T) doPostRequest(strings[0], mInputStreamHandler, mParameters);
				if(mJSONObject != null)
					return (T) doPostRequest(strings[0], mInputStreamHandler, mJSONObject);
				return (T) loadURL(strings[0], mInputStreamHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(T data) {
			super.onPostExecute(data);
			if(mReceiver != null)
				mReceiver.onReceived(data);
		}
	}
}
