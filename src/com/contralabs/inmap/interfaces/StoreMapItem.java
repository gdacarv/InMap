package com.contralabs.inmap.interfaces;

import com.contralabs.inmap.model.Store;


public interface StoreMapItem extends MapItem {
	
	//float getCircularArea(); XXX Maybe useless
	String getTitle();
	String getSubtext();
	Store getStore();
	//Bitmap getIconBitmap();
	
}
