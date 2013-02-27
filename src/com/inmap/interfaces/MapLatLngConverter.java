package com.inmap.interfaces;

import com.google.android.gms.maps.model.LatLng;

public interface MapLatLngConverter {

	LatLng getLatLng(MapItem item, int level);
}
