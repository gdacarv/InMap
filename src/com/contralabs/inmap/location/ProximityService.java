package com.contralabs.inmap.location;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.ProximityCheckDialogFragment;
import com.contralabs.inmap.notifications.NotificationHelper;
import com.contralabs.inmap.social.DummyPeopleInsideAPI;
import com.contralabs.inmap.social.FacebookHelper;
import com.contralabs.inmap.social.PeopleInsideAPI;

public class ProximityService extends IntentService {

	public static final String PLACE_NAME = "PLACE_NAME";

	public ProximityService() {
		super("ProximityService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if(!intent.hasExtra(LocationManager.KEY_PROXIMITY_ENTERING))
			return;
		
		PeopleInsideAPI peopleInsideAPI = new DummyPeopleInsideAPI();
		String facebookId = getFacebookId();
		if(isReallyInside(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false))) {
			SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			boolean showNotification = defaultSharedPreferences.getBoolean(getString(R.string.pref_key_notif_proximity), true);
			if(showNotification) {
				Bundle extras = new Bundle(1);
				extras.putBoolean(ProximityCheckDialogFragment.SHOW, true);
				new NotificationHelper(this).showNotification(1, String.format(getString(R.string.msg_enter_area), intent.getStringExtra(PLACE_NAME)), getString(R.string.msg_notification_open), extras, true, true);
			}
			if(facebookId != null)
				peopleInsideAPI.onEnteredArea(facebookId, defaultSharedPreferences.getBoolean(getString(R.string.pref_key_show_im_inside), true));
		} else {
			if(facebookId != null)
				peopleInsideAPI.onExitedArea(facebookId);
		}
	}

	private String getFacebookId() {
		return FacebookHelper.getFacebookId(this);
	}

	private boolean isReallyInside(boolean intentValue) {
		/*LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location.hasSpeed() && location.getSpeed() > 5f)
		 	return false;*/		
		return intentValue;
	}

}
