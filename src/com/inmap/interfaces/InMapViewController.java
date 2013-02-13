package com.inmap.interfaces;

public interface InMapViewController {

	void setLevel(int level);
	
	void setMapController(MapController controller);
	
	void setOnStoreBallonClickListener(OnStoreBallonClickListener listener);
	
	void openStoreBallon(StoreMapItem storeMapItem);

}
