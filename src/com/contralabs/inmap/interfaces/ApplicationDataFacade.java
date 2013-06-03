package com.contralabs.inmap.interfaces;

import com.contralabs.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;

public interface ApplicationDataFacade {

	LevelInformation getLevelInformation();
	MapController getMapController();
	OnLevelSelectedListener[] getOnLevelSelectedListeners();
	OnInfrastructureCategoryChangedListener[] getOnInfrastructureCategoryChangedListeners();
	StoreOnMapController getStoreOnMapController();
	double getLatitude();
	double getLongitude();
	float getMapZoom();
	float getMapRotation();
	double getInitialLatitude();
	double getInitialLongitude();
	float getInitialMapZoom();
	String getPlaceName();
	
}
