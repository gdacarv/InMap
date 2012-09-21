package com.inmap.interfaces;

public interface InMapViewController {

	void zoomIn();

	void zoomOut();

	void setLevel(int level, int mapResource);
	
	void setMapController(MapController controller);

}
