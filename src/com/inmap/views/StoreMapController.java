package com.inmap.views;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;

import com.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.MapItem;
import com.inmap.interfaces.MapItemsListener;
import com.inmap.interfaces.StoreOnMapController;
import com.inmap.model.Store;

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
		Resources res = mContext.getResources();
		for(Store store : stores)
			store.getCategory().loadMapIconBitmap(res);
		return stores;
	}

	private void reloadShowingItems() {
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		for(MapItem item : mItems)
			if(((Store)item).getLevel() == level)
				mapItems.add(item);
		mShowingItems = mapItems.toArray(new MapItem[mapItems.size()]);
	}
}
