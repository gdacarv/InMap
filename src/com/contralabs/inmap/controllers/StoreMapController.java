package com.contralabs.inmap.controllers;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;

import com.contralabs.inmap.interfaces.MapController;
import com.contralabs.inmap.interfaces.MapItem;
import com.contralabs.inmap.interfaces.MapItemsListener;
import com.contralabs.inmap.interfaces.OnLevelSelectedListener;
import com.contralabs.inmap.interfaces.StoreOnMapController;
import com.contralabs.inmap.model.Store;

public class StoreMapController implements MapController, StoreOnMapController, OnLevelSelectedListener {

	private Context mContext;
	private MapItem[] mItems, mShowingItems;
	private MapItemsListener mListener = new MapItemsListener() {
		
		@Override
		public void refreshMapItems() {
		}
	};
	private int level;
	
	public StoreMapController(Context context, int levelInit) {
		mContext = context;
		level = levelInit;
	}
	
	@Override
	public MapItem[] getMapItems() {
		return mShowingItems;
	}

	@Override
	public void setMapItemsListener(MapItemsListener listener) {
		mListener = listener;
	}

	@Override
	public void setStores(Store... stores) {
		if(stores != null && stores.length > 0)
			mItems = convertStoresInMapItems(stores);
		else
			mItems = new MapItem[0];
		reloadShowingItems();
		mListener.refreshMapItems();
	}

	@Override
	public void onLevelSelected(int level) {
		this.level = level;
		if(mItems != null)
			reloadShowingItems();
		mListener.refreshMapItems();
	}

	private MapItem[] convertStoresInMapItems(Store[] stores) {
		return stores;
	}

	private void reloadShowingItems() {
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		for(MapItem item : mItems)
			if(((Store)item).getLevel() == level)
				mapItems.add(item);
		mShowingItems = mapItems.toArray(new MapItem[mapItems.size()]);
	}

	@Override
	public void clearMarkers() {
		setStores();
	}
}
