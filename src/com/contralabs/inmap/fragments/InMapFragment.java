package com.contralabs.inmap.fragments;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.contralabs.inmap.R;
import com.contralabs.inmap.activities.MainActivity;
import com.contralabs.inmap.controllers.PreSettedMapLatLngConverter;
import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.interfaces.InMapViewController;
import com.contralabs.inmap.interfaces.LevelInformation;
import com.contralabs.inmap.interfaces.MapController;
import com.contralabs.inmap.interfaces.MapItem;
import com.contralabs.inmap.interfaces.MapItemsListener;
import com.contralabs.inmap.interfaces.MapLatLngConverter;
import com.contralabs.inmap.interfaces.OnStoreBallonClickListener;
import com.contralabs.inmap.interfaces.StoreMapItem;
import com.contralabs.inmap.location.SensorHelper;
import com.contralabs.inmap.location.SensorHelper.OnSensorChangeListener;
import com.contralabs.inmap.model.Coordinate;
import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.model.StoreParameters;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InMapFragment extends FixedSupportMapFragment implements InMapViewController, MapItemsListener, OnSensorChangeListener {

	
	private static final String TAG = "InMapFragment";
	private GoogleMap mMap;
	private ApplicationDataFacade mApplicationDataFacade;
	private GroundOverlay mLevelGroundOverlay;
	private int mCurrentLevel;
	private MapController mMapController;
	private OnStoreBallonClickListener mOnStoreBallonClickListener;
	private Map<Marker, MapItem> mMapItemsMarkers = new HashMap<Marker, MapItem>();
	private MapLatLngConverter mMapLatLngConverter;
	private Context mContext;
	private Marker mMarkerLatLng1, mMarkerLatLng2, mMarkerLatLngLevel;
	private float mMapRotation;
	private LevelInformation mLevelInformation;
	private SensorHelper mSensorHelper;

	public void setApplicationDataFacade(ApplicationDataFacade applicationDataFacade) {
		mApplicationDataFacade = applicationDataFacade;
		mLevelInformation = mApplicationDataFacade.getLevelInformation();
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRetainInstance(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		mContext = getActivity();
		if(mMap == null) {
			mMap = getMap();
			if(mMap != null) {
				initialize();
				if(!(mContext instanceof MainActivity) || !((MainActivity)mContext).isShowingSplash())
					moveMapViewToPlacePosition(false, true);
			}
			else {
				Toast.makeText(mContext, R.string.msg_nomaps, Toast.LENGTH_LONG).show();
			}
		}
		configureMapType();
		mSensorHelper.beginListening();
	}
	
	@Override
	public void onPause() {
		mSensorHelper.stopListening();
		super.onPause();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mSensorHelper = new SensorHelper(activity);
		mSensorHelper.setOnSensorChangeListener(this);
	}

	private void initialize() {
		moveMapViewToInitialPosition();
		configureMapType();
		mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
		mMap.setMyLocationEnabled(true);

		configureLevels(getResources());

		//configureMarkerLatLng();

		mMap.setOnMapClickListener(onMapClickListener);
	}

	private void configureMapType() {
		if(mMap != null) {
			String mapTypes[] = getResources().getStringArray(R.array.pref_values_maps_type);
			String mapType = PreferenceManager.getDefaultSharedPreferences(mContext).getString(getString(R.string.pref_key_maps_type), mapTypes[0]);
			mMap.setMapType(mapType.equals(mapTypes[0]) ? GoogleMap.MAP_TYPE_NORMAL : mapType.equals(mapTypes[1]) ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_HYBRID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		fixZoomButtons(view);
		return view;
	}

	private void configureMarkerLatLng() {
		mMarkerLatLng1 = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(mLevelInformation.getLevelLatitude(0), mLevelInformation.getLevelLongitude(0))).title("Click to see position"));
		mMarkerLatLng2 = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(mLevelInformation.getLevelLatitude(0), mLevelInformation.getLevelLongitude(0))).title("Click to see position"));
		mMarkerLatLngLevel = mMap.addMarker(new MarkerOptions().draggable(true).position(new LatLng(mLevelInformation.getLevelLatitude(mCurrentLevel), mLevelInformation.getLevelLongitude(mCurrentLevel))).title("Click to see position"));
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
		if(level != mCurrentLevel && mMap != null) {
			mCurrentLevel = level;
			mLevelGroundOverlay.remove();
			mLevelGroundOverlay = addLevelGroundOverlay(level);
			setMarkerToLevelPosition(level);
		}
	}

	private void setMarkerToLevelPosition(int level) {
		if(mMarkerLatLngLevel != null)
			mMarkerLatLngLevel.setPosition(new LatLng(mLevelInformation.getLevelLatitude(mCurrentLevel), mLevelInformation.getLevelLongitude(mCurrentLevel)));
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
		if(mMap == null)
			return;
		if(mMapItemsMarkers.containsValue(storeMapItem)) {
			for(Marker marker : mMapItemsMarkers.keySet())
				if(mMapItemsMarkers.get(marker) == storeMapItem)
					marker.showInfoWindow();
		}else
			createMarker(storeMapItem).showInfoWindow();
	}

	@Override
	public void refreshMapItems() {
		if(mMap == null)
			return;
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
		int mapIconResId = item.getMapIconResId();
		if(mapIconResId != 0)
			markerOptions.icon(BitmapDescriptorFactory.fromResource(mapIconResId));
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
		if(mMap == null)
			return;
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude()))
		.zoom(mApplicationDataFacade.getMapZoom())
		.bearing(mApplicationDataFacade.getMapRotation())
		.build()));
	}

	public void moveMapViewToPlacePosition(boolean instant, boolean fromInitial) {
		if(mMap == null)
			return;
		if(fromInitial) {
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
			.target(new LatLng(mApplicationDataFacade.getInitialLatitude(), mApplicationDataFacade.getInitialLongitude()))
			.zoom(mApplicationDataFacade.getInitialMapZoom())
			.build()));
		}
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
		.target(new LatLng(mApplicationDataFacade.getLatitude(), mApplicationDataFacade.getLongitude()))
		.zoom(mApplicationDataFacade.getMapZoom())
		.bearing(mApplicationDataFacade.getMapRotation())
		.build()), instant ? 1 : 2000, null);
	}

	private OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {

		@Override
		public void onInfoWindowClick(Marker marker) {
			if((mMarkerLatLng1 != null && mMarkerLatLng1.equals(marker)) || mMarkerLatLng2 != null && mMarkerLatLng2.equals(marker) || mMarkerLatLngLevel != null && mMarkerLatLngLevel.equals(marker)) {
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
			boolean forceShowClearButton = false;
			if(coor.x >= 0 && coor.y >= 0) {
				Store[] stores = null;
				DbAdapter dbAdapter = DbAdapter.getInstance(mContext).open();
				try {
					stores = dbAdapter.getStores(new StoreParameters().setLevel(mCurrentLevel).hasPoint(coor));
				} finally {
					dbAdapter.close();
				}
				if(stores != null && stores.length >0){
					mTempMarker = createMarker(stores[0]);
					mTempMarker.showInfoWindow();
					forceShowClearButton = true;
				}
			}
			if(mContext instanceof MainActivity) {
				if(forceShowClearButton)
					((MainActivity)mContext).updateClearMarkersVisibility(View.VISIBLE);
				else
					((MainActivity)mContext).updateClearMarkersVisibility();
			}
		}
	};

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

	@Override
	public int getLevel() {
		return mCurrentLevel;
	}

	private void fixZoomButtons(View v) {
		View view = v.findViewById(1);
		if (view != null){
			int actionBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics());

			// Sets the margin of the button
			ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
			marginParams.setMargins(0, 0, actionBarHeight, actionBarHeight*3);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			view.setLayoutParams(layoutParams);
		}
	}

	@Override
	public void onSensorChanged(float azimuth, float pitch, float roll) {
		// TODO Auto-generated method stub
		float angle = (float) (-azimuth*180/Math.PI);
		Log.i(TAG, "Sensor changed: azimuth: " + azimuth + " pitch: " + pitch + " roll: " + roll + " angle: " + angle);
	}

}
