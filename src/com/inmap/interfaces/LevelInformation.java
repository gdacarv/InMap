package com.inmap.interfaces;

public interface LevelInformation {

	int getLevelsCount();

	String getTitle(int position);
	
	int getMapResource(int position);
	
	int initializerLevel();

	/** Latitude from center of map */
	double getLevelLatitude(int i);

	/** Longitude from center of map */
	double getLevelLongitude(int i);

	/** Width, in meters, of map */
	float getLevelWidth(int i);

}
