package com.contralabs.inmap.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;

import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.ProximityCheckDialogFragment;
import com.contralabs.inmap.notifications.NotificationHelper;

public class ProximityService extends IntentService {

	public static final String PLACE_NAME = "PLACE_NAME";

	public ProximityService() {
		super("ProximityService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(intent.hasExtra(LocationManager.KEY_PROXIMITY_ENTERING)) {
			if(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
				/*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if(location.hasSpeed() && location.getSpeed() > 5f)*/
				if(isValid()) {
					Bundle extras = new Bundle(1);
					extras.putBoolean(ProximityCheckDialogFragment.SHOW, true);
					new NotificationHelper(this).showNotification(1, String.format(getString(R.string.msg_enter_area), intent.getStringExtra(PLACE_NAME)), getString(R.string.msg_notification_open), extras, true, true);
				}
			} else {
				new NotificationHelper(this).showNotification(1, String.format(getString(R.string.msg_exit_area), intent.getStringExtra(PLACE_NAME)), "", null, true, true);
			}
		}
	}

	private boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
