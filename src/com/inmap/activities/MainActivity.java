/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inmap.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.inmap.InMapApplication;
import com.inmap.actionbar.ActionBarActivity;
import com.inmap.controllers.GoogleMapInMapController;
import com.inmap.fragments.InfrastructureBarFragment;
import com.inmap.fragments.ProblemasDialogFragment;
import com.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.inmap.fragments.LevelPickerFragment;
import com.inmap.fragments.LevelPickerFragment.OnLevelSelectedListener;
import com.inmap.fragments.StoreCategoryListFragment;
import com.inmap.fragments.StoreCategoryListFragment.OnStoreCategoryChangedListener;
import com.inmap.fragments.StoreListFragment;
import com.inmap.fragments.StoreListFragment.OnStoreSelectedListener;
import com.inmap.fragments.StoreListFragment.StoreListController;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.InMapViewController;
import com.inmap.interfaces.OnAnimationEnd;
import com.inmap.interfaces.OnStoreBallonClickListener;
import com.inmap.interfaces.StoreMapItem;
import com.inmap.interfaces.StoreOnMapController;
import com.inmap.model.DbAdapter;
import com.inmap.model.Store;
import com.inmap.model.StoreParameters;
import com.inmap.salvadorshop.R;
import com.inmap.views.AnimateFrameLayout;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMode;

public class MainActivity extends ActionBarActivity implements OnInfrastructureCategoryChangedListener, OnStoreCategoryChangedListener, OnStoreSelectedListener, OnLevelSelectedListener, StoreListController, OnStoreBallonClickListener {

	protected static final String SHOW_STORE_INMAP = "show_store_inmap";


	private ApplicationDataFacade mApplicationDataFacade;


	private AnimateFrameLayout mLayoutStoreList, mLayoutCategoryList, mLayoutLevelPicker;
	private StoreCategoryListFragment mStoreCategoryListFragment;
	private InfrastructureBarFragment mInfrastructureBarFragment;
	private StoreListFragment mStoreListFragment;
	private LevelPickerFragment mLevelPickerFragment;
	private boolean isShowingStoreList = false,
		isShowingLevelPicker = false;
	private InMapViewController mInMapViewController;
	private OnLevelSelectedListener[] mLevelSelectedListeners;
	private OnInfrastructureCategoryChangedListener[] mInfrastructureCategoryChangedListeners;
	private StoreOnMapController mStoreOnMapController;
	private DbAdapter mDbAdapter;
	private View mClearMarkersButton;

	private GoogleMap mMap;

	private SlidingMenu mSlidingMenu;


	private boolean infraHasMarkers, storesHasMarkers;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setTitle("");

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			getActionBar().setHomeButtonEnabled(true);
		
		mDbAdapter = DbAdapter.getInstance(getApplicationContext());
		
		fixZoomButtons();
		
		configureSlidingMenu();

		configureFragments();

		configureAllLayout();

		setUpMapIfNeeded();

		loadInformationFromApplicationDataFacade();

		Intent intent = getIntent();

		verifyIntentShowStoreOnMap(intent);
		verifyIntentSearch(intent);
		
	}

	public void fixZoomButtons() {
		View root = getChildAt(getChildAt(getChildAt(findViewById(android.R.id.content))));
		View view = root.findViewById(1);
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

	public View getChildAt(View view) {
		return getChildAt(view, 0);
	}

	public View getChildAt(View view, int index) {
		return ((ViewGroup)view).getChildAt(index);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mInMapViewController.moveMapViewToPlacePosition();
			break;

		case R.id.menu_categories:
			if(isShowingStoreList)
				returnToCategoryList();
			toggleList();
			break;

		case R.id.menu_search:
			onSearchRequested();
			break;

		case R.id.menu_levels:
			toggleLevelPicker();
			break;
			
		case R.id.menu_problemas:
			new ProblemasDialogFragment().show(getSupportFragmentManager(), "ProblemasDialogFragment");
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(mSlidingMenu.isMenuShowing()){
			if(isShowingStoreList) {
				if(mStoreListFragment.isSearch())
					onSearchClicked();
				else
					returnToCategoryList();
			}else
				toggleList();
		}
		else
			super.onBackPressed();
	}

	@Override
	public void onInfrastructureCategoryChanged(int id) {
		infraHasMarkers = id > 0;
		updateClearMarkersVisibility();
		for(OnInfrastructureCategoryChangedListener listener : mInfrastructureCategoryChangedListeners)
			listener.onInfrastructureCategoryChanged(id);
	}

	@Override
	public void onStoreCategoryChanged(int id) {
		mStoreListFragment.setStoreParameters(new StoreParameters().addCategory(id));
		showStoreList();
	}

	private void showStoreList() {
		isShowingStoreList = true;
		mLayoutStoreList.setVisibility(View.VISIBLE);
		mLayoutCategoryList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_100));
		mLayoutStoreList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_from_100));
	}


	@Override
	public void onStoreSelected(Store store) {
		if(store != null) {
			Intent i = new Intent(this, StoreDetailsActivity.class);
			i.putExtra(StoreDetailsActivity.STORE, store);
			startActivity(i);
		}
	}

	@Override
	public void onBackToCategorysClicked() {
		returnToCategoryList();
	}

	@Override
	public void onShowOnMapClicked(Store[] stores) {
		toggleList();
		mStoreOnMapController.setStores(stores);
		storesHasMarkers = stores != null && stores.length > 0;
		updateClearMarkersVisibility();
	}

	@Override
	public void onSearchClicked() {
		returnToCategoryList();
		toggleList();
		onSearchRequested();
	}

	@Override
	public void onLevelSelected(int level) {
		mInMapViewController.setLevel(level);
		for(OnLevelSelectedListener listener : mLevelSelectedListeners)
			listener.onLevelSelected(level);
	}
	
	private void configureSlidingMenu() {
		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMode.LEFT_RIGHT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset_left, SlidingMode.LEFT);
		mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_width_right, SlidingMode.RIGHT);
		mSlidingMenu.setFadeDegree(0.00f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mSlidingMenu.setMenu(R.layout.layout_lists, SlidingMode.LEFT);
		mSlidingMenu.setMenu(R.layout.layout_levelpicker, SlidingMode.RIGHT);
	}

	private void configureFragments() {
		FragmentManager fm = getSupportFragmentManager();

		mStoreCategoryListFragment = (StoreCategoryListFragment) fm.findFragmentById(R.id.fragment_categorylist);
		mStoreCategoryListFragment.setOnStoreCategoryChangedListener(this);

		mInfrastructureBarFragment = (InfrastructureBarFragment) fm.findFragmentById(R.id.fragment_infrabar);
		mInfrastructureBarFragment.setOnInfrastructureCategoryChangeListener(this);

		mStoreListFragment = (StoreListFragment) fm.findFragmentById(R.id.fragment_storelist);
		mStoreListFragment.setOnStoreSelectedListener(this);
		mStoreListFragment.setStoreListController(this);

		mLevelPickerFragment = (LevelPickerFragment) fm.findFragmentById(R.id.fragment_levelpicker);
		mLevelPickerFragment.setOnLevelSelectedListener(this);
	}

	private void configureAllLayout() {
		configureLayoutCategoryList();
		configureLayoutStoreList();
		mClearMarkersButton = findViewById(R.id.btn_clear_markers);
		configureLayoutLevelPicker();
	}

	

	private void configureLayoutCategoryList() {
		mLayoutCategoryList = (AnimateFrameLayout) findViewById(R.id.layout_categorylist);
		mLayoutCategoryList.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				if(isShowingStoreList) mLayoutCategoryList.setVisibility(View.GONE);
			}
		});
	}

	private void configureLayoutStoreList() {
		mLayoutStoreList = (AnimateFrameLayout) findViewById(R.id.layout_storelist);
		mLayoutStoreList.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				if(!isShowingStoreList) mLayoutStoreList.setVisibility(View.GONE);
			}
		});
	}

	private void configureLayoutLevelPicker() {
		mLayoutLevelPicker = (AnimateFrameLayout) findViewById(R.id.layout_levels);
		mLayoutLevelPicker.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				if(!isShowingLevelPicker) mLayoutLevelPicker.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void loadInformationFromApplicationDataFacade() {
		mApplicationDataFacade = ((InMapApplication)getApplication()).getApplicationDataFacade();
		if(mMap == null)
			throw new IllegalStateException("GoogleMap should not be null.");
		mInMapViewController = new GoogleMapInMapController(/*Context*/ this, mMap, mApplicationDataFacade);
		mInMapViewController.setOnStoreBallonClickListener(this);
		mLevelSelectedListeners = mApplicationDataFacade.getOnLevelSelectedListeners();
		mInfrastructureCategoryChangedListeners = mApplicationDataFacade.getOnInfrastructureCategoryChangedListeners();
		mStoreOnMapController = mApplicationDataFacade.getStoreOnMapController();
		mInMapViewController.setMapController(mApplicationDataFacade.getMapController());
	}

	private void toggleLevelPicker() {
		/*isShowingLevelPicker = !isShowingLevelPicker;
		if(isShowingLevelPicker){
			mLayoutLevelPicker.setVisibility(View.VISIBLE);
			mLayoutLevelPicker.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_down_from__100_to_0));*/
		if(mSlidingMenu.isSecondaryMenuShowing())
			mSlidingMenu.showContent();
		else 
			mSlidingMenu.showSecondaryMenu();
		/*}else {
			mLayoutLevelPicker.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_100));
		}*/
	}

	protected void toggleList() {
		if(mSlidingMenu.isMenuShowing())
			mSlidingMenu.showContent();
		else 
			mSlidingMenu.showMenu();
	}

	private void returnToCategoryList() {
		isShowingStoreList = false;
		mLayoutCategoryList.setVisibility(View.VISIBLE);
		mLayoutCategoryList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_down_from__100_to_0));
		mLayoutStoreList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_down_100));
	}

	@Override
	public void onStoreBallonClicked(StoreMapItem store) {
		onStoreSelected(store.getStore());
	}

	private boolean verifyIntentShowStoreOnMap(Intent i) {
		if(i != null) {
			Store store = (Store) i.getSerializableExtra(SHOW_STORE_INMAP);
			if(store != null) {
				if(mSlidingMenu.isMenuShowing())
					toggleList();
				showStoreOnMap(store);
				return true;
			}
		}
		return false;
	}

	private void showStoreOnMap(Store store) {
		onLevelSelected(store.getLevel());
		mStoreOnMapController.setStores(store);
		mInMapViewController.openStoreBallon(store);
		storesHasMarkers = store != null;
		updateClearMarkersVisibility();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map)).getMap();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		verifyIntentShowStoreOnMap(intent);
		verifyIntentSearch(intent);
	}

	private void verifyIntentSearch(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			if(query.length() > 2 && query.endsWith("s")) // To search plurals as singular (pt)
				query = query.substring(0, query.length()-1);
			mStoreListFragment.setStoreParameters(new StoreParameters().setText(query));
			if(!mSlidingMenu.isMenuShowing())
				toggleList();
			if(!isShowingStoreList)
				showStoreList();
		}else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
		    // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
		    long id = Long.parseLong(intent.getDataString());
		    mDbAdapter.open();
		    Store store;
		    try {
		    	store = mDbAdapter.getStore(id);
		    }finally {
		    	mDbAdapter.close();
		    }
		    onStoreSelected(store);
		}
	}
	
	public void onClearMarkersButtonClick(View v) {
		mStoreOnMapController.clearMarkers();
		storesHasMarkers = false;
		onInfrastructureCategoryChanged(0);
	}

	public void updateClearMarkersVisibility() {
		mClearMarkersButton.setVisibility(storesHasMarkers || infraHasMarkers ? View.VISIBLE : View.INVISIBLE);
	}

	public void updateClearMarkersVisibility(int visible) {
		mClearMarkersButton.setVisibility(visible);
	}
}
