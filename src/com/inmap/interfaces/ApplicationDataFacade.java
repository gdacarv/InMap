package com.inmap.interfaces;

import com.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;

public interface ApplicationDataFacade {

	LevelInformation getLevelInformation();
	MapController getMapController();
	OnLevelSelectedListener[] getOnLevelSelectedListeners();
	OnInfrastructureCategoryChangedListener[] getOnInfrastructureCategoryChangedListeners();
	StoreOnMapController getStoreOnMapController();
	
}
