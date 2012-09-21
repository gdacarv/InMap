package com.inmap.views;
/* Essa classe vai carregar as Infrastructures do BD e transforma-la em MapItem. */

import android.content.Context;

import com.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.MapItem;
import com.inmap.interfaces.MapItemsListener;
import com.inmap.model.DbAdapter;
import com.inmap.model.Infrastructure;

public class InfrastructureMapController implements MapController,
		OnInfrastructureCategoryChangedListener, OnLevelSelectedListener {

	private Context mContext;
	private MapItem[] mItens;
	private MapItemsListener mListener;
	private int level, infrastructureCategoryId;
	
	
	public InfrastructureMapController(Context context) {
		this.mContext = context;
	}

	@Override
	public void onLevelSelected(int level, int mapResource) {
		this.level = level;
		reloadMapItems();
	}

	@Override
	public void onInfrastructureCategoryChanged(int id) {
		this.infrastructureCategoryId = id;
		reloadMapItems();
	}

	@Override
	public MapItem[] getMapItems() {
		return mItens;
	}

	@Override
	public void setMapItemsListener(MapItemsListener listener) {
		mListener = listener;
	}

	private void reloadMapItems() {
		if(infrastructureCategoryId > 0){
			Infrastructure[] infras = getInfrastructuresFromDB();
			mItens = convertInfrastructuresInMapItems(infras);
		}else
			mItens = new MapItem[0];
		mListener.refreshMapItemsListener();
	}

	private Infrastructure[] getInfrastructuresFromDB() {
		DbAdapter db = DbAdapter.getInstance(mContext).open();
		Infrastructure[] result = db.getInfrastructures(infrastructureCategoryId, level);
		db.close();
		return result;
	}

	private MapItem[] convertInfrastructuresInMapItems(Infrastructure[] infras) {
		if(infras != null && infras.length > 0)
			infras[0].getCategory().loadMapIconBitmap(mContext.getResources());
		return infras;
	}

}
