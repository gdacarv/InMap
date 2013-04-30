package com.contralabs.inmap.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.contralabs.inmap.R;
import com.contralabs.inmap.activities.MainActivity;
import com.contralabs.inmap.activities.StoreDetailsActivity;
import com.contralabs.inmap.model.DbAdapter;

public class NotificationHelper {

	private static final int REQUEST_MOVIE = 2;
	private static final int REQUEST_SEARCH = 1;
	private static final int REQUEST_OPEN = 0;
	private static final long CINEMARK_ID = 409;
	
	private Context mContext;

	public NotificationHelper(Context context) {
		mContext = context;
	}

	public void showNotification(int id, String title, String text, Bundle extras) {
		NotificationCompat.Builder mBuilder = createBuilder(title, text, extras);
		show(id, mBuilder);
	}
	
	public void showNotification(int id, String title, String text, Bundle extras, boolean searchButton, boolean moviesButton) {
		NotificationCompat.Builder mBuilder = createBuilder(title, text, extras);
		if(searchButton) {
			Intent searchIntent = new Intent(mContext, MainActivity.class);
			if(extras != null)
				searchIntent.putExtras(extras);
			searchIntent.putExtra(MainActivity.SHOW_SEARCH, true);
			mBuilder.addAction(R.drawable.bt_pesquisar_small, mContext.getString(R.string.pesquisar_lojas), PendingIntent.getActivity(mContext, REQUEST_SEARCH, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		}
		if(moviesButton) {
			Intent movieIntent = new Intent(mContext, StoreDetailsActivity.class);
			DbAdapter db = DbAdapter.getInstance(mContext).open();
			try {
				movieIntent.putExtra(StoreDetailsActivity.STORE, db.getStore(CINEMARK_ID));
			} finally {
				db.close();
			}
			if(extras != null)
				movieIntent.putExtras(extras);
			movieIntent.putExtra(StoreDetailsActivity.SHOW_EXTRA, true);
			mBuilder.addAction(R.drawable.ico_eventos_lojas, mContext.getString(R.string.programa_o_do_cinema), PendingIntent.getActivity(mContext, REQUEST_MOVIE, movieIntent, PendingIntent.FLAG_UPDATE_CURRENT));
		}
		show(id, mBuilder);
	}

	private void show(int id, NotificationCompat.Builder mBuilder) {
		NotificationManager mNotificationManager =
			(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}

	private NotificationCompat.Builder createBuilder(String title, String text, Bundle extras) {
		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(mContext)
			.setSmallIcon(R.drawable.ic_home) // TODO Change icon
			.setContentTitle(title)
			.setContentText(text)
			.setTicker(title);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, MainActivity.class);
		if(extras != null)
			resultIntent.putExtras(extras);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
			stackBuilder.getPendingIntent(
					REQUEST_OPEN,
					PendingIntent.FLAG_UPDATE_CURRENT
			);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setVibrate(new long[] {0, 200, 100, 200});
		mBuilder.setLights(0xff0b73a8, 800, 1000);
		return mBuilder;
	}
}
