package com.contralabs.inmap.interfaces;

public interface MapController {

	MapItem[] getMapItems();
	void setMapItemsListener(MapItemsListener listener);
}
