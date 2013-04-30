package com.contralabs.inmap;

import com.contralabs.inmap.location.ProximityHelper;
import com.contralabs.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public  class OnBootReceiver extends  BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Context applicationContext = context.getApplicationContext();
			new ProximityHelper(applicationContext, SalvadorShopApplicationDataFacade.getInstance(applicationContext)).setupProximityAlert();
		}
	}
}