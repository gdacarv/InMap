package com.inmap.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.inmap.model.Coordinate;

public interface MapLatLngConverter {

	LatLng getLatLng(MapItem item, int level);

	Coordinate getMapCoordinate(LatLng latlng, int level);
}
