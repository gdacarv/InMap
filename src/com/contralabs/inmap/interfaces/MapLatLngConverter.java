package com.contralabs.inmap.interfaces;

import com.contralabs.inmap.model.Coordinate;
import com.google.android.gms.maps.model.LatLng;

public interface MapLatLngConverter {

	LatLng getLatLng(MapItem item, int level);

	Coordinate getMapCoordinate(LatLng latlng, int level);
}
