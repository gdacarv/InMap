package com.inmap.server;

import java.util.Date;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.IBinder;

public class UpdateDataService extends Service {

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

		@Override
		protected Void doInBackground(Void... params) {
			Date lastUpdate = getStoresLastUpdateLocal();
			if(lastUpdate == null || lastUpdate.before(getStoresLastUpdateServer()))
				updateStoresFromServer();
			lastUpdate = getInfraLastUpdateLocal();
			if(lastUpdate == null || lastUpdate.before(getInfraLastUpdateServer()))
				updateInfraFromServer();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			stopSelf();
		}

		private void updateInfraFromServer() {
			// TODO Auto-generated method stub
			setInfraLastUpdateLocal(new Date());
		}

		private void setInfraLastUpdateLocal(Date date) {
			// TODO Auto-generated method stub
			
		}

		private Date getInfraLastUpdateServer() {
			// TODO Auto-generated method stub
			return null;
		}

		private Date getInfraLastUpdateLocal() {
			// TODO Auto-generated method stub
			return null;
		}

		private void updateStoresFromServer() {
			// TODO Auto-generated method stub
			setStoresLastUpdateLocal(new Date());
		}

		private void setStoresLastUpdateLocal(Date date) {
			// TODO Auto-generated method stub
			
		}

		private Date getStoresLastUpdateServer() {
			// TODO Auto-generated method stub
			return null;
		}

		private Date getStoresLastUpdateLocal() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
