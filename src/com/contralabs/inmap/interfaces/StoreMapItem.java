package com.contralabs.inmap.interfaces;

import com.contralabs.inmap.model.Store;


public interface StoreMapItem extends MapItem {
	
	String getTitle();
	String getSubtext();
	Store getStore();
	
}
