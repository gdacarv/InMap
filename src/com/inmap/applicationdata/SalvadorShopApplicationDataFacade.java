package com.inmap.applicationdata;

import android.content.Context;
import com.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.LevelInformation;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.StoreOnMapController;
import com.inmap.views.InfrastructureMapController;
import com.inmap.views.StoreMapController;
import com.inmap.views.TwoMapController;

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
		/*final Resources res = mContext.getResources();
		MapController controller = new MapController() {
			
			private MapItem[] mMapItem = { new MapItem(){
				
				private Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.marker);

				@Override
				public int getX() {
					return 150;
				}

				@Override
				public int getY() {
					return 250;
				}

				@Override
				public Bitmap getBitmap() {
					return bitmap;
				}
				
			}
			};
			
			@Override
			public void setMapItemsListener(MapItemsListener listener) {
			}
			
			@Override
			public MapItem[] getMapItems() {
				return mMapItem;
			}
		};*/
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
			mInfrastructureMapController = new InfrastructureMapController(mContext);
		return mInfrastructureMapController;
	}

	private StoreMapController getStoreMapController() {
		if(mStoreMapController == null)
			mStoreMapController = new StoreMapController(mContext);
		return mStoreMapController;
	}
}
