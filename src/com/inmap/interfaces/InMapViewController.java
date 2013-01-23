package com.inmap.interfaces;

import com.inmap.views.InMapImageView.OnStoreBallonClickListener;

public interface InMapViewController {

	void zoomIn();

	void zoomOut();

	void setLevel(int level, int mapResource);
	
	void setMapController(MapController controller);
	
	void setOnStoreBallonClickListener(OnStoreBallonClickListener listener);
	
	void openStoreBallon(StoreMapItem storeMapItem);

}
