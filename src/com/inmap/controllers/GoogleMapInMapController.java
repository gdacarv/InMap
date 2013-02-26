package com.inmap.controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.InMapViewController;
import com.inmap.interfaces.LevelInformation;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.MapItem;
import com.inmap.interfaces.MapItemsListener;
import com.inmap.interfaces.OnStoreBallonClickListener;
import com.inmap.interfaces.StoreMapItem;

public class GoogleMapInMapController implements InMapViewController, MapItemsListener {


	private GoogleMap mMap;
	private ApplicationDataFacade mApplicationDataFacade;
	private GroundOverlay[] mGroundOverlays;
	private int mCurrentLevel;
	private MapController mMapController;
	private OnStoreBallonClickListener mOnStoreBallonClickListener;
	private Map<Marker, MapItem> mMapItemsMarkers = new HashMap<Marker, MapItem>();

	public GoogleMapInMapController(GoogleMap map, ApplicationDataFacade applicationDataFacade) {
		mMap = map;
		mApplicationDataFacade = applicationDataFacade;
		moveMapViewToInitialPosition();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				moveMapViewToPosition();
			}
		}, 1500);
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
		mMap.setMyLocationEnabled(true);

		configureLevels();
		
	}

	public void configureLevels() {
		LevelInformation levelInformation = mApplicationDataFacade.getLevelInformation();
		mGroundOverlays = new GroundOverlay[levelInformation.getLevelsCount()];
		mCurrentLevel = levelInformation.initializerLevel();

		float mapRotation = mApplicationDataFacade.getMapRotation();
		for(int i = 0; i < mGroundOverlays.length; i++) {
			mGroundOverlays[i] = mMap.addGroundOverlay(new GroundOverlayOptions()
				.image(BitmapDescriptorFactory.fromResource(levelInformation.getMapResource(i)))
				.bearing(mapRotation)
				.visible(i == mCurrentLevel)
				.transparency(0.2f)
				.position(
						new LatLng(levelInformation.getLevelLatitude(i), levelInformation.getLevelLongitude(i)), 
						levelInformation.getLevelWidth(i)
				));
		}
	}

	@Override
	public void setLevel(int level) {
		if(level != mCurrentLevel) {
			mGroundOverlays[mCurrentLevel].setVisible(false);
			mGroundOverlays[mCurrentLevel = level].setVisible(true);
		}
	}

	@Override
	public void setMapController(MapController controller) {
		mMapController = controller;
		mMapController.setMapItemsListener(this);
	}

	@Override
	public void setOnStoreBallonClickListener(OnStoreBallonClickListener listener) {
		mOnStoreBallonClickListener = listener;
	}

	@Override
	public void openStoreBallon(StoreMapItem storeMapItem) {
		if(mMapItemsMarkers.containsValue(storeMapItem)) {
			for(Marker marker : mMapItemsMarkers.keySet())
				if(mMapItemsMarkers.get(marker) == storeMapItem)
					marker.showInfoWindow();
		}else
			createMarker(mApplicationDataFacade.getLevelInformation(), storeMapItem).showInfoWindow();
	}

	@Override
	public void refreshMapItems() {
		MapItem[] mapItems = mMapController.getMapItems();
		Iterator<Marker> it = mMapItemsMarkers.keySet().iterator();
		while(it.hasNext()) {
			it.next().remove();
			it.remove();
		}
		LevelInformation levelInformation = mApplicationDataFacade.getLevelInformation();
		for(MapItem item : mapItems) {
			createMarker(levelInformation, item); 
		} // XXX Might use optimizations
	}

	public Marker createMarker(LevelInformation levelInformation, MapItem item) {
		MarkerOptions markerOptions = new MarkerOptions()
			.icon(BitmapDescriptorFactory.fromBitmap(item.getMapIconBitmap()))
			.position(getLatLng(levelInformation, item)); 
		if(item instanceof StoreMapItem) {
			StoreMapItem storeMapItem = (StoreMapItem) item;
			markerOptions
				.snippet(storeMapItem.getSubtext())
				.title(storeMapItem.getTitle());
		}
		Marker marker = mMap.addMarker(markerOptions);
		mMapItemsMarkers.put(marker, item);
		return marker;
	}

	private void moveMapViewToInitialPosition() {
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(mApplicationDataFacade.getInitialLatitude(), mApplicationDataFacade.getInitialLongitude()))
		.zoom(mApplicationDataFacade.getInitialMapZoom())
		.build()));
	}

	private void moveMapViewToPosition() {
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude()))
		.zoom(mApplicationDataFacade.getMapZoom())
		.bearing(mApplicationDataFacade.getMapRotation())
		.build()), 2000, null);
	}

	private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
		
		@Override
		public void onInfoWindowClick(Marker marker) {
			if(mOnStoreBallonClickListener != null) {
				MapItem mapItem = mMapItemsMarkers.get(marker);
				if(mapItem != null && mapItem instanceof StoreMapItem)
					mOnStoreBallonClickListener.onStoreBallonClicked((StoreMapItem) mapItem);
			}
		}
	};

	public LatLng getLatLng(LevelInformation levelInformation, MapItem item) {// FIXME get correct position
		return new LatLng(levelInformation.getLevelLatitude(mCurrentLevel), levelInformation.getLevelLongitude(mCurrentLevel));
	}
}
