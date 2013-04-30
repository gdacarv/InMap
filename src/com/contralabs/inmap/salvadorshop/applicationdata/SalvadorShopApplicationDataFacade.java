package com.contralabs.inmap.salvadorshop.applicationdata;

import android.content.Context;

import com.contralabs.inmap.controllers.InfrastructureMapController;
import com.contralabs.inmap.controllers.StoreMapController;
import com.contralabs.inmap.controllers.TwoMapController;
import com.contralabs.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.contralabs.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;
import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.interfaces.LevelInformation;
import com.contralabs.inmap.interfaces.MapController;
import com.contralabs.inmap.interfaces.StoreOnMapController;

public class SalvadorShopApplicationDataFacade implements ApplicationDataFacade {
	
	private Context mContext;
	private static SalvadorShopApplicationDataFacade instance;
	private LevelInformation mLevelInformation;
	private MapController mMapController;
	private InfrastructureMapController mInfrastructureMapController;
	private StoreMapController mStoreMapController;
	private OnLevelSelectedListener[] mOnLevelSelectedListeners;
	private OnInfrastructureCategoryChangedListener[] mOnInfrastructureCategoryChangedListeners;
	
	public static SalvadorShopApplicationDataFacade getInstance(Context context){
		if(instance == null)
			instance = new SalvadorShopApplicationDataFacade(context);
		return instance;
	}

	private SalvadorShopApplicationDataFacade(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public LevelInformation getLevelInformation() {
		if(mLevelInformation == null)
			mLevelInformation = new SalvadorShopLevelInformation();
		return mLevelInformation;
	}

	@Override
	public MapController getMapController() {
		if(mMapController == null)
			mMapController = TwoMapController.createInstance(getInfrastructureMapController(), getStoreMapController());
		return mMapController;
	}

	@Override
	public OnLevelSelectedListener[] getOnLevelSelectedListeners() {
		if(mOnLevelSelectedListeners == null)
			mOnLevelSelectedListeners = new OnLevelSelectedListener[] {getInfrastructureMapController(), getStoreMapController()};
		return mOnLevelSelectedListeners;
	}

	@Override
	public OnInfrastructureCategoryChangedListener[] getOnInfrastructureCategoryChangedListeners() {
		if(mOnInfrastructureCategoryChangedListeners == null)
			mOnInfrastructureCategoryChangedListeners = new OnInfrastructureCategoryChangedListener[] {getInfrastructureMapController()};
		return mOnInfrastructureCategoryChangedListeners;
	}

	@Override
	public StoreOnMapController getStoreOnMapController() {
		return getStoreMapController();
	}



	private InfrastructureMapController getInfrastructureMapController() {
		if(mInfrastructureMapController == null)
			mInfrastructureMapController = new InfrastructureMapController(mContext, mLevelInformation.getInitLevel());
		return mInfrastructureMapController;
	}

	private StoreMapController getStoreMapController() {
		if(mStoreMapController == null)
			mStoreMapController = new StoreMapController(mContext, mLevelInformation.getInitLevel());
		return mStoreMapController;
	}

	@Override
	public double getLatitude() {
		return -12.97837;
	}

	@Override
	public double getLongitude() {
		return -38.454875;
	}

	@Override
	public float getMapRotation() {
		return -57f;
	}

	@Override
	public float getMapZoom() {
		return 17f;
	}

	@Override
	public double getInitialLatitude() {
		return -12.979802;
	}

	@Override
	public double getInitialLongitude() {
		return -38.479031;
	}

	@Override
	public float getInitialMapZoom() {
		return 12f;
	}

	@Override
	public String getPlaceName() {
		return "Salvador Shopping";
	}
}
