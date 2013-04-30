package com.contralabs.inmap.location;

import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.ProximityCheckDialogFragment;
import com.contralabs.inmap.notifications.NotificationHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

public class ProximityReceiver extends BroadcastReceiver {
	
	public static final String PLACE_NAME = "PLACE_NAME";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.hasExtra(LocationManager.KEY_PROXIMITY_ENTERING)) {
			if(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				/*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location.hasSpeed() && location.getSpeed() > 5f)*/
				if(isValid()) {
					Bundle extras = new Bundle(1);
					extras.putBoolean(ProximityCheckDialogFragment.SHOW, true);
					new NotificationHelper(context).showNotification(1, String.format(context.getString(R.string.msg_enter_area), intent.getStringExtra(PLACE_NAME)), context.getString(R.string.msg_notification_open), extras, true, true);
				}
			} else {
				new NotificationHelper(context).showNotification(1, String.format(context.getString(R.string.msg_exit_area), intent.getStringExtra(PLACE_NAME)), "", null, true, true);
			}
		}
	}

	private boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}
}
