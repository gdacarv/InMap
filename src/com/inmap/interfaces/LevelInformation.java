package com.inmap.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface LevelInformation {

	int getLevelsCount();

	String getTitle(int position);
	
	int getMapResource(int position);
	
	int getInitLevel();

	/** Latitude from center of map */
	double getLevelLatitude(int i);

	/** Longitude from center of map */
	double getLevelLongitude(int i);

	/** Width, in meters, of map */
	float getLevelWidth(int i);

	LatLng getNorthwestBound(int level);

	LatLng getSoutheastBound(int level);

}
