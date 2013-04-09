package com.inmap;

import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;
import com.inmap.server.UpdateDataService;

import android.app.Application;
import android.content.Intent;

public class InMapApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		startService(new Intent(this, UpdateDataService.class));
	}
	
	public ApplicationDataFacade getApplicationDataFacade() {
		return SalvadorShopApplicationDataFacade.getInstance(this);
		
	}
}
