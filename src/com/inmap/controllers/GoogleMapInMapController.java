package com.inmap.controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.InMapViewController;
import com.inmap.interfaces.LevelInformation;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.MapItem;
import com.inmap.interfaces.MapItemsListener;
import com.inmap.interfaces.MapLatLngConverter;
import com.inmap.interfaces.OnStoreBallonClickListener;
import com.inmap.interfaces.StoreMapItem;
import com.inmap.model.Coordinate;
import com.inmap.model.DbAdapter;
import com.inmap.model.Store;
import com.inmap.model.StoreParameters;

public class GoogleMapInMapController implements InMapViewController, MapItemsListener {


	private GoogleMap mMap;
	private ApplicationDataFacade mApplicationDataFacade;
	private GroundOverlay mLevelGroundOverlay;
	private int mCurrentLevel;
	private MapController mMapController;
	private OnStoreBallonClickListener mOnStoreBallonClickListener;
	private Map<Marker, MapItem> mMapItemsMarkers = new HashMap<Marker, MapItem>();
	private MapLatLngConverter mMapLatLngConverter;
	private Context mContext;
	private Marker mMarkerLatLng;

	public GoogleMapInMapController(Context context, GoogleMap map, ApplicationDataFacade applicationDataFacade) {
		mContext = context;
		mMap = map;
		mApplicationDataFacade = applicationDataFacade;
		mLevelInformation = mApplicationDataFacade.getLevelInformation();
		moveMapViewToInitialPosition();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				moveMapViewToPlacePosition();
			}
		}, 1500);
		//mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
		mMap.setMyLocationEnabled(true);

		configureLevels(mContext.getResources());
		
		mMap.setOnMapClickListener(onMapClickListener);
		
		configureMarkerLatLng();
		configureSimpleMarker();
	}

	private void configureMarkerLatLng() {
		LevelInformation levelInfo = mApplicationDataFacade.getLevelInformation();
		mMarkerLatLng = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(levelInfo.getLevelLatitude(0), levelInfo.getLevelLongitude(0))).title("Click to see position"));
	}

	private void configureSimpleMarker() {
		LevelInformation levelInfo = mApplicationDataFacade.getLevelInformation();
		LatLng latLng = new LatLng(levelInfo.getLevelLatitude(mCurrentLevel), levelInfo.getLevelLongitude(mCurrentLevel));
		mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
		mMap.addMarker(new MarkerOptions().draggable(true).position(latLng));
	}

	public void configureLevels(Resources resources) {
		mMapRotation = mApplicationDataFacade.getMapRotation();
		mMapLatLngConverter = new PreSettedMapLatLngConverter(resources, mLevelInformation, mMapRotation);
		mCurrentLevel = mLevelInformation.getInitLevel();
		mLevelGroundOverlay = addLevelGroundOverlay(mCurrentLevel);
		
		/*LatLngBounds bounds = mGroundOverlays[2].getBounds(); Probably useless
		mMap.addMarker(new MarkerOptions().position(bounds.northeast));
		mMap.addMarker(new MarkerOptions().position(bounds.southwest));

		mMap.addMarker(new MarkerOptions().position(mLevelInformation.getNorthwestBound(mCurrentLevel)));
		mMap.addMarker(new MarkerOptions().position(mLevelInformation.getSoutheastBound(mCurrentLevel)));*/
	}

	@Override
	public void setLevel(int level) {
		if(level != mCurrentLevel) {
			mCurrentLevel = level;
			mLevelGroundOverlay.remove();
			mLevelGroundOverlay = addLevelGroundOverlay(level);
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
			createMarker(storeMapItem).showInfoWindow();
	}

	@Override
	public void refreshMapItems() {
		MapItem[] mapItems = mMapController.getMapItems();
		Iterator<Marker> it = mMapItemsMarkers.keySet().iterator();
		while(it.hasNext()) {
			it.next().remove();
			it.remove();
		}
		for(MapItem item : mapItems) {
			createMarker(item); 
		} // XXX Might use optimizations
	}

	public Marker createMarker(MapItem item) {
		LatLng latLng = mMapLatLngConverter.getLatLng(item, mCurrentLevel);
		MarkerOptions markerOptions = new MarkerOptions()
			.position(latLng); 
		Bitmap mapIconBitmap = item.getMapIconBitmap();
		if(mapIconBitmap != null)
			markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mapIconBitmap));
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

	public void moveMapViewToPlacePosition() {
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude()))
		.zoom(mApplicationDataFacade.getMapZoom())
		.bearing(mApplicationDataFacade.getMapRotation())
		.build()), 2000, null);
	}

	private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {
		
		@Override
		public void onInfoWindowClick(Marker marker) {
			if(mMarkerLatLng.equals(marker)) {
				LatLng pos = marker.getPosition();
				String text = "onInfoWindowClick " + "lat: " + pos.latitude + " long: " + pos.longitude;
				Log.i("OnInfoWindowClickListener", text);
				Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();
			} else
				
			if(mOnStoreBallonClickListener != null) {
				MapItem mapItem = mMapItemsMarkers.get(marker);
				if(mapItem != null && mapItem instanceof StoreMapItem)
					mOnStoreBallonClickListener.onStoreBallonClicked((StoreMapItem) mapItem);
			}
		}
	};

	private OnMapClickListener onMapClickListener = new OnMapClickListener() {
		
		private Marker mTempMarker;

		@Override
		public void onMapClick(LatLng latlng) { // TODO Do it async
			if(mTempMarker != null)
				mTempMarker.remove();
			Coordinate coor = mMapLatLngConverter.getMapCoordinate(latlng, mCurrentLevel);
			Log.i("GoogleMapInMapController.onMapClickListener.new OnMapClickListener() {...}",
					"onMapClick " +coor.toString());
			if(coor.x < 0 || coor.y < 0)
				return;
			Store[] stores = null;
			DbAdapter dbAdapter = DbAdapter.getInstance(mContext).open();
			try {
				stores = dbAdapter.getStores(new StoreParameters().setLevel(mCurrentLevel).hasPoint(coor));
			} finally {
				dbAdapter.close();
			}
			if(stores == null || stores.length < 1)
				return;
			mTempMarker = createMarker(stores[0]);
			mTempMarker.showInfoWindow();
		}
	};
	private float mMapRotation;
	private LevelInformation mLevelInformation;
	
	public void onMyLocationChange (Location location) {
		
	}
	
	private GroundOverlay addLevelGroundOverlay(int level) {
		return mMap.addGroundOverlay(new GroundOverlayOptions()
		.image(BitmapDescriptorFactory.fromResource(mLevelInformation.getMapResource(level)))
		.bearing(mMapRotation)
		.position(
				new LatLng(mLevelInformation.getLevelLatitude(level), mLevelInformation.getLevelLongitude(level)), 
				mLevelInformation.getLevelWidth(level))
		);
	}
}
