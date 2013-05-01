package com.contralabs.inmap.interfaces;

public interface InMapViewController {

	void setLevel(int level);
	
	void setMapController(MapController controller);
	
	void setOnStoreBallonClickListener(OnStoreBallonClickListener listener);
	
	void openStoreBallon(StoreMapItem storeMapItem);

	void moveMapViewToPlacePosition(boolean instant, boolean fromInitial);
	
	int getLevel();

}
