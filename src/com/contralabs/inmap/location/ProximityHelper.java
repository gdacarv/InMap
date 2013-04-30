package com.contralabs.inmap.location;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.contralabs.inmap.interfaces.ApplicationDataFacade;

public class ProximityHelper {

	private static final float RADIUS = 170f;

	private static final int REQUEST_CODE = 1;

	public static final String PROXIMITY_ACTION = "com.contralabs.inmap.location.PROXIMITY_EVENT";
	
	private Context mContext;
	private ApplicationDataFacade mApplicationDataFacade;

	public ProximityHelper(Context context, ApplicationDataFacade appData) {
		mContext = context;
		mApplicationDataFacade = appData;
	}
	
	public void setupProximityAlert() {
		//Intent intent = new Intent(PROXIMITY_ACTION);
		Intent intent = new Intent(mContext, ProximityService.class);
		intent.putExtra(ProximityReceiver.PLACE_NAME, mApplicationDataFacade.getPlaceName());
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendingIntent = PendingIntent.getService(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		//locationManager.addProximityAlert(-12.987353,-38.481604, 10f, -1, pendingIntent);
		locationManager.addProximityAlert(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude(), RADIUS, -1, pendingIntent); // XXX Maybe this use a lot of battery
	}
}
