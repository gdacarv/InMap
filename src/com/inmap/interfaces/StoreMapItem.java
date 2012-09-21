package com.inmap.interfaces;

import com.inmap.model.Store;


public interface StoreMapItem extends MapItem {
	
	float getCircularArea();
	String getTitle();
	String getSubtext();
	Store getStore();
	//Bitmap getIconBitmap();
	
}
