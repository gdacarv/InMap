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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.inmap.R;
import com.inmap.StoreParameters;
import com.inmap.actionbar.ActionBarActivity;
import com.inmap.applicationdata.SalvadorShopApplicationDataFacade;
import com.inmap.fragments.InfrastructureBarFragment;
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
import com.inmap.interfaces.LevelInformation;
import com.inmap.interfaces.OnAnimationEnd;
import com.inmap.interfaces.StoreMapItem;
import com.inmap.interfaces.StoreOnMapController;
import com.inmap.model.Store;
import com.inmap.views.AnimateFrameLayout;
import com.inmap.views.InMapImageView.OnStoreBallonClickListener;

public class MainActivity extends ActionBarActivity implements OnInfrastructureCategoryChangedListener, OnStoreCategoryChangedListener, OnStoreSelectedListener, OnLevelSelectedListener, StoreListController, OnStoreBallonClickListener {

	protected static final String SHOW_STORE_INMAP = "show_store_inmap";


	private ApplicationDataFacade mApplicationDataFacade = SalvadorShopApplicationDataFacade.getInstance(this);


	private AnimateFrameLayout mLayoutMap, mLayoutStoreList, mLayoutCategoryList, mLayoutLevelPicker;
	private StoreCategoryListFragment mStoreCategoryListFragment;
	private InfrastructureBarFragment mInfrastructureBarFragment;
	private StoreListFragment mStoreListFragment;
	private LevelPickerFragment mLevelPickerFragment;
	private boolean isShowingList = false,
	isShowingStoreList = false,
	isShowingLevelPicker = false;
	private FrameLayout mLayoutLists;
	private InMapViewController mInMapViewController;
	private ZoomControls mZoom;
	private OnLevelSelectedListener[] mLevelSelectedListeners;
	private OnInfrastructureCategoryChangedListener[] mInfrastructureCategoryChangedListeners;
	private StoreOnMapController mStoreOnMapController;


	private LevelInformation mLevelInformation;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;

		configureFragments();

		configureAllLayout(width);

		loadInformationFromApplicationDataFacade();

		
		if(!verifyIntentShowStoreOnMap())
			setInitialLevel();

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
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:
			toggleList();
			break;

		case R.id.menu_search:
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_levels:
			toggleLevelPicker();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(isShowingList){
			if(isShowingStoreList)
				returnToCategoryList();
			else
				toggleList();
		}
		else
			super.onBackPressed();
	}

	@Override
	public void onInfrastructureCategoryChanged(int id) {
		for(OnInfrastructureCategoryChangedListener listener : mInfrastructureCategoryChangedListeners)
			listener.onInfrastructureCategoryChanged(id);
	}


	@Override
	public void onStoreCategoryChanged(int id) {
		isShowingStoreList = true;
		mStoreListFragment.setStoreParameters(new StoreParameters().addCategory(id));
		mLayoutStoreList.setVisibility(View.VISIBLE);
		mLayoutCategoryList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_100));
		mLayoutStoreList.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_from_100));
	}


	@Override
	public void onStoreSelected(Store store) {
		Intent i = new Intent(this, StoreDetailsActivity.class);
		i.putExtra(StoreDetailsActivity.STORE, store);
		startActivity(i);
	}

	@Override
	public void onBackToCategorysClicked() {
		returnToCategoryList();
	}

	@Override
	public void onShowOnMapClicked(Store[] stores) {
		toggleList();
		mStoreOnMapController.setStores(stores);
	}

	@Override
	public void onLevelSelected(int level) {
		int mapResource = mLevelInformation.getMapResource(level);
		for(OnLevelSelectedListener listener : mLevelSelectedListeners)
			listener.onLevelSelected(level);
		mInMapViewController.setLevel(level, mapResource);
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

	private void configureAllLayout(int width) {
		configureLayoutMap(width);
		configureLayoutCategoryList();
		configureLayoutStoreList();
		configureLayoutLists(width);
		configureLayoutLevelPicker();
		configureZoomControls();
		mInMapViewController = (InMapViewController) findViewById(R.id.map);
		mInMapViewController.setOnStoreBallonClickListener(this);
	}

	private void configureLayoutMap(int width) {
		mLayoutMap = (AnimateFrameLayout) findViewById(R.id.layout_map);
		mLayoutMap.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.FILL_PARENT));
		mLayoutMap.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(isShowingList)
					toggleList();
				return false;
			}
		});
		mLayoutMap.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				if(!isShowingList)
					mLayoutLists.setVisibility(View.GONE);
			}
		});
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

	private void configureLayoutLists(int width) {
		mLayoutLists = (FrameLayout) findViewById(R.id.layout_lists);
		mLayoutLists.setLayoutParams(new LinearLayout.LayoutParams((int) (width*0.8), LinearLayout.LayoutParams.FILL_PARENT));
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

	private void configureZoomControls() {
		mZoom = (ZoomControls) findViewById(R.id.zoomControls);
		mZoom.setOnZoomInClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mInMapViewController.zoomIn();
			}
		});
		mZoom.setOnZoomOutClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mInMapViewController.zoomOut();
			}
		});
	}

	private void loadInformationFromApplicationDataFacade() {
		mInMapViewController.setMapController(mApplicationDataFacade.getMapController());
		mLevelSelectedListeners = mApplicationDataFacade.getOnLevelSelectedListeners();
		mInfrastructureCategoryChangedListeners = mApplicationDataFacade.getOnInfrastructureCategoryChangedListeners();
		mStoreOnMapController = mApplicationDataFacade.getStoreOnMapController();
		mLevelInformation = mApplicationDataFacade.getLevelInformation();
	}

	private void setInitialLevel() {
		onLevelSelected(mLevelInformation.initializerLevel());
	}

	private void toggleLevelPicker() {
		isShowingLevelPicker = !isShowingLevelPicker;
		if(isShowingLevelPicker){
			mLayoutLevelPicker.setVisibility(View.VISIBLE);
			mLayoutLevelPicker.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_down_from__100_to_0));
		}else {
			mLayoutLevelPicker.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_up_100));
		}
	}

	protected void toggleList() {
		isShowingList = !isShowingList;
		if(isShowingList){
			mLayoutLists.setVisibility(View.VISIBLE);
			mLayoutLists.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_right_100));
			mLayoutMap.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_right_80));
			mInfrastructureBarFragment.hideBar(); 
		}else{
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.to_left_100);
			mLayoutLists.startAnimation(anim);
			mLayoutMap.startAnimation(AnimationUtils.loadAnimation(this, R.anim.to_left_80));
		}
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

	private boolean verifyIntentShowStoreOnMap() {
		Intent i = getIntent();
		if(i != null) {
			Store store = (Store) i.getSerializableExtra(SHOW_STORE_INMAP);
			if(store != null) {
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
	}
}
