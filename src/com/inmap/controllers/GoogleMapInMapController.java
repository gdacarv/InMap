package com.inmap.controllers;

import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.InMapViewController;
import com.inmap.interfaces.LevelInformation;
import com.inmap.interfaces.MapController;
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
				.position(
						new LatLng(levelInformation.getLevelLatitude(i), levelInformation.getLevelLongitude(i)), 
						levelInformation.getLevelWidth(i)
				));
		}
		mGroundOverlays[0].setVisible(true);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void refreshMapItemsListener() {
		// TODO Auto-generated method stub
		
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
}
