package com.inmap.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.inmap.model.DatabaseHelper;
import com.inmap.salvadorshop.R;

public class UpdateDataService extends Service { // TODO Check internet, if offline, create a broadcast receiver

	private static final String DATE_FORMAT = "yyyy/MM/dd hh:mm:ss";
	private static final String INFRA_LAST_UPDATE_KEY = "infra_last_update_key";
	private static final String STORES_LAST_UPDATE_KEY = "stores_last_update_key";
	private AsyncTask<Void,Void,Void> mAsyncTask;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mAsyncTask = new UpdateDataAsyncTask();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else

			mAsyncTask.execute();
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if(mAsyncTask != null && mAsyncTask.getStatus() == Status.RUNNING)
			mAsyncTask.cancel(true);
	}

	private class UpdateDataAsyncTask extends AsyncTask<Void, Void, Void>{

		private SimpleDateFormat mDateFormat;
		private SharedPreferences mSharedPreferences;
		private JSONObject mInfoObject;
		private Bundle mManifestMetaData;
		private DatabaseHelper mDbHelper;
		private SQLiteDatabase mDb;

		@Override
		protected Void doInBackground(Void... params) {
			mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(UpdateDataService.this);
			mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		    try {
				mManifestMetaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			mDbHelper = new DatabaseHelper(UpdateDataService.this);
			mDb = mDbHelper.getWritableDatabase();
			try {
				Log.i("UpdateDataService.UpdateDataAsyncTask", "doInBackground "
						+ "start.");
				Date lastUpdate = getStoresLastUpdateLocal();
				if(lastUpdate == null || lastUpdate.before(getStoresLastUpdateServer()))
					updateStoresFromServer();
				lastUpdate = getInfraLastUpdateLocal();
				if(lastUpdate == null || lastUpdate.before(getInfraLastUpdateServer()))
					updateInfraFromServer();
			}catch(Exception e) {
				e.printStackTrace();
				// TODO if offline scheduale to future
			} finally {
				mDb.close();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
			Log.i("UpdateDataService.UpdateDataAsyncTask", "onPostExecute "
					+ "stopService.");
		}

		private void updateInfraFromServer() throws IOException {
			loadURL(mManifestMetaData.getString("url_infra"), new XMLInputStreamHandler() {
				
				@Override
				protected void handleXml(XmlPullParser xpp) {
					if(xpp != null) {
						try {
							mDbHelper.populateInfrastructures(mDb, xpp);
						} catch (XmlPullParserException e) {
							Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (IOException e) {
							Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
			});
			setInfraLastUpdateLocal(new Date());
		}

		private void updateStoresFromServer() throws IOException {
			loadURL(mManifestMetaData.getString("url_stores"), new XMLInputStreamHandler() {
				
				@Override
				protected void handleXml(XmlPullParser xpp) {
					if(xpp != null) {
						try {
							mDbHelper.populateStores(mDb, xpp);
						} catch (XmlPullParserException e) {
							Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (IOException e) {
							Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
			});
			setStoresLastUpdateLocal(new Date());
		}

		private void loadInfoObjectIfNeeded() throws IOException {
			if(mInfoObject == null)
				loadURL(mManifestMetaData.getString("url_info"), new StringInputStreamHandler() {
					
					@Override
					protected void handleString(String string) {
						try {
							mInfoObject = new JSONObject(string);
							Log.i("UpdateDataService.UpdateD...}",
									"handleString " + string);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		}

		private Date getInfraLastUpdateServer() throws IOException {
			loadInfoObjectIfNeeded();
			try {
				return mDateFormat.parse(mInfoObject.optString("infra_last_update"));
			} catch (ParseException e) {
				e.printStackTrace();
				Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		private Date getStoresLastUpdateServer() throws IOException {
			loadInfoObjectIfNeeded();
			try {
				return mDateFormat.parse(mInfoObject.optString("stores_last_update"));
			} catch (ParseException e) {
				e.printStackTrace();
				Toast.makeText(UpdateDataService.this, R.string.msg_error_server, Toast.LENGTH_SHORT).show();
				return null;
			}
		}

		private void setInfraLastUpdateLocal(Date date) {
			mSharedPreferences.edit().putString(INFRA_LAST_UPDATE_KEY, mDateFormat.format(date)).commit();
		}

		private Date getInfraLastUpdateLocal() {
			try {
				return mDateFormat.parse(mSharedPreferences.getString(INFRA_LAST_UPDATE_KEY, "2000/01/01 00:00:00")); // TODO Change default date to actual last update
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		private void setStoresLastUpdateLocal(Date date) {
			mSharedPreferences.edit().putString(STORES_LAST_UPDATE_KEY, mDateFormat.format(date)).commit();
		}

		private Date getStoresLastUpdateLocal() {
			try {
				return mDateFormat.parse(mSharedPreferences.getString(STORES_LAST_UPDATE_KEY, "2000/01/01 00:00:00")); // TODO Change default date to actual last update
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		private void loadURL(String urlString, InputStreamHandler handler) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				handler.handleInputStream(urlConnection.getInputStream());
			} finally {
					urlConnection.disconnect();
			}
		}

	}
}
