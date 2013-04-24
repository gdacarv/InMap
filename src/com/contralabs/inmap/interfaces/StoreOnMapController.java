package com.contralabs.inmap.interfaces;

import com.contralabs.inmap.model.Store;

public interface StoreOnMapController{
	
	void setStores(Store... stores);
	
	void clearMarkers();

}
