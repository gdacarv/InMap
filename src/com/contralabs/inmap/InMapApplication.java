package com.contralabs.inmap;

import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.location.ProximityHelper;
import com.contralabs.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;
import com.contralabs.inmap.server.UpdateDataService;
import com.google.analytics.tracking.android.EasyTracker;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class InMapApplication extends Application {

	private static final String FIRST_USE = "com.contralabs.inmap.FIRST_USE";

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate() {
		super.onCreate();
		Context applicationContext = getApplicationContext();
		EasyTracker.getInstance().setContext(applicationContext);
		startService(new Intent(this, UpdateDataService.class));
		SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		if(defaultSharedPreferences.getBoolean(FIRST_USE, true)) {
			onFirstStart();
			Editor editor = defaultSharedPreferences.edit().putBoolean(FIRST_USE, false);
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) 
				editor.apply();
			else
				editor.commit();
		}
	}
	
	private void onFirstStart() {
		new ProximityHelper(getApplicationContext(), getApplicationDataFacade()).setupProximityAlert();
	}

	public ApplicationDataFacade getApplicationDataFacade() {
		return SalvadorShopApplicationDataFacade.getInstance(this);
	}
}
