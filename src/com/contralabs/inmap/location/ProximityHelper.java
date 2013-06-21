package com.contralabs.inmap.location;

import java.util.Arrays;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class ProximityHelper {

	private static final int REQUEST_CODE = 1;

	public static final String PROXIMITY_ACTION = "com.contralabs.inmap.location.PROXIMITY_EVENT";

	private static final String TAG = "ProximityHelper";
	
	private Context mContext;
	private ApplicationDataFacade mApplicationDataFacade;
	private PendingIntent mPendingIntent;
	private boolean mProximityAlertIsOn;
	private LocationClient mLocationClient;


	public ProximityHelper(Context context, ApplicationDataFacade appData) {
		mContext = context;
		mApplicationDataFacade = appData;
	}
	
	public void setupProximityAlert() {
		Intent intent = new Intent(mContext, ProximityService.class);
		intent.putExtra(ProximityService.PLACE_NAME, mApplicationDataFacade.getPlaceName());
		mPendingIntent = PendingIntent.getService(mContext, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		mLocationClient = new LocationClient(mContext, connectionCallbacks, connectionFailedListener);
		mLocationClient.connect();
		
	}

	private void setupProximityAlertUsingLocationManager(PendingIntent pendingIntent) {
		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.addProximityAlert(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude(), mApplicationDataFacade.getAreaRadius(), -1, pendingIntent); 
		//locationManager.addProximityAlert(-12.987255d,-38.481578d, 10f, -1, pendingIntent); 
		mProximityAlertIsOn = true;
		Log.i(TAG, "Proximity Alert by LocationManager is on!");
	}
	
	private ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks() {
		
		@Override
		public void onDisconnected() {
			if(!mProximityAlertIsOn)
				setupProximityAlertUsingLocationManager(mPendingIntent);
		}
		
		@Override
		public void onConnected(Bundle arg0) {
			Geofence geofence = new Geofence.Builder()
				.setRequestId("1")
				.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
				.setExpirationDuration(Geofence.NEVER_EXPIRE)
				.setCircularRegion(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude(), mApplicationDataFacade.getAreaRadius())
				.build();
			mLocationClient.addGeofences(Arrays.asList(geofence), mPendingIntent, onAddGeofencesResultListener);
		}
	};

	private OnConnectionFailedListener connectionFailedListener = new OnConnectionFailedListener() {
		
		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			setupProximityAlertUsingLocationManager(mPendingIntent);
		}
	};

	
	private OnAddGeofencesResultListener onAddGeofencesResultListener = new OnAddGeofencesResultListener() {
		
		@Override
		public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
			if (LocationStatusCodes.SUCCESS != statusCode) {
				setupProximityAlertUsingLocationManager(mPendingIntent);
			} else {
				mProximityAlertIsOn = true;
				Log.i(TAG, "Proximity Alert by Geofences is on!");
			}
	        mLocationClient.disconnect();
		}
	};

}
