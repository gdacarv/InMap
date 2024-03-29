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

package com.contralabs.inmap.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.contralabs.inmap.InMapApplication;
import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.InMapFragment;
import com.contralabs.inmap.fragments.InfoDialogFragment;
import com.contralabs.inmap.fragments.InfrastructureBarFragment;
import com.contralabs.inmap.fragments.InfrastructureBarFragment.OnInfrastructureCategoryChangedListener;
import com.contralabs.inmap.fragments.LegalNoticesDialogFragment;
import com.contralabs.inmap.fragments.RateDialogFragment;
import com.contralabs.inmap.fragments.SplashDialogFragment;
import com.contralabs.inmap.fragments.StoreCategoryListFragment;
import com.contralabs.inmap.fragments.StoreCategoryListFragment.OnStoreCategoryChangedListener;
import com.contralabs.inmap.fragments.StoreListFragment;
import com.contralabs.inmap.fragments.StoreListFragment.OnStoreSelectedListener;
import com.contralabs.inmap.fragments.StoreListFragment.StoreListController;
import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.interfaces.InMapViewController;
import com.contralabs.inmap.interfaces.OnAnimationEnd;
import com.contralabs.inmap.interfaces.OnLevelSelectedListener;
import com.contralabs.inmap.interfaces.OnStoreBallonClickListener;
import com.contralabs.inmap.interfaces.StoreMapItem;
import com.contralabs.inmap.interfaces.StoreOnMapController;
import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.model.StoreParameters;
import com.contralabs.inmap.recommendation.SimilarityBuilderService;
import com.contralabs.inmap.server.WebUtils;
import com.contralabs.inmap.views.AnimateFrameLayout;
import com.contralabs.inmap.views.LevelPickerView;
import com.google.analytics.tracking.android.EasyTracker;
import com.helpshift.Helpshift;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.slidingmenu.lib.SlidingMode;

public class MainActivity extends SlidingActionBarActivity implements OnInfrastructureCategoryChangedListener, OnStoreCategoryChangedListener, OnStoreSelectedListener, OnLevelSelectedListener, StoreListController, OnStoreBallonClickListener {

	public static final String SHOW_STORE_INMAP = "show_store_inmap";


	public static final String SHOW_SEARCH = "com.contralabs.inmap.SHOW_SEARCH";


	private ApplicationDataFacade mApplicationDataFacade;


	private AnimateFrameLayout mLayoutStoreList, mLayoutCategoryList;
	private StoreCategoryListFragment mStoreCategoryListFragment;
	private InfrastructureBarFragment mInfrastructureBarFragment;
	private StoreListFragment mStoreListFragment;
	//private PeopleInsideFragment mPeopleInsideFragment;
	private LevelPickerView mLevelPickerView;
	private boolean isShowingStoreList = false;
	private InMapViewController mInMapViewController;
	private InMapFragment mInMapFragment;
	private OnLevelSelectedListener[] mLevelSelectedListeners;
	private OnInfrastructureCategoryChangedListener[] mInfrastructureCategoryChangedListeners;
	private StoreOnMapController mStoreOnMapController;
	private DbAdapter mDbAdapter;
	private View mClearMarkersButton;
	private boolean mShowingSplash = false;
	private Helpshift mHelpshift;

	private SlidingMenu mSlidingMenu;

	private boolean infraHasMarkers, storesHasMarkers;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setTitle("");

		getSupportActionBar().setHomeButtonEnabled(true);

		mDbAdapter = DbAdapter.getInstance(getApplicationContext());

		configureSlidingMenu();

		configureFragments();

		configureAllLayout();

		loadInformationFromApplicationDataFacade();

		retrieveSavedState(savedInstanceState);

		Intent intent = getIntent();

		boolean intentShowOnMap = verifyIntentShowStoreOnMap(intent);
		boolean intentSearch = verifyIntentSearch(intent);

		if(!intentSearch && !intentShowOnMap && savedInstanceState == null)
			showSplash();

		//ProximityCheckDialogFragment.showIfAppropriate(intent, getSupportFragmentManager());
		
		configureHelpShift();
		
		startSimilarityBuilder();
	}

	private void configureHelpShift() {
		mHelpshift = new Helpshift(this);
		mHelpshift.install(this, "c29cdd5f753ae99da471940d7065b4a4", "contralabs.helpshift.com", "contralabs_platform_20130501143517257-eab3ee3ac6fa113");
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
		/*User user = FacebookHelper.getUser(this);
		if(user != null)
			new ServerPeopleInsideAPI().onEnteredArea(user, true); */
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
		/*User user = FacebookHelper.getUser(this);
		if(user != null)
			new ServerPeopleInsideAPI().onExitedArea(user, true);*/ 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mInMapViewController.moveMapViewToPlacePosition(false, false);
			break;

		case R.id.menu_categories:
			if(isShowingStoreList)
				returnToCategoryList();
			toggleList();
			break;

		case R.id.menu_search:
			onSearchRequested();
			break;

		case R.id.menu_social:
			toggleRightMenu();
			EasyTracker.getTracker().sendView("SocialFragment");
			break;

		case R.id.menu_problemas:
			//new ProblemasDialogFragment().show(getSupportFragmentManager(), "ProblemasDialogFragment");
			mHelpshift.showSupport(this);
			break;

		case R.id.menu_sobre:
			new InfoDialogFragment().show(getSupportFragmentManager(), "InfoDialogFragment");
			break;

		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		case R.id.menu_legal_notices:
			new LegalNoticesDialogFragment().show(getSupportFragmentManager(), "LegalNoticesDialogFragment");
			break;
			
		case R.id.menu_recomendacoes:
			startActivity(new Intent(this, RecommendationActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if(isLeftMenuShowing()){
			if(isShowingStoreList) {
				if(mStoreListFragment.isSearch())
					onSearchClicked();
				else
					returnToCategoryList();
			}else
				toggleList();
		}
		else
			if(!RateDialogFragment.showIfAppropriate(this, getSupportFragmentManager()))
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

	@Override
	public boolean onSearchRequested() {
		EasyTracker.getTracker().sendView(getString(R.string.view_search));
		return super.onSearchRequested();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isShowingStoreList", isShowingStoreList);
	}

	private void retrieveSavedState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			if(savedInstanceState.getBoolean("isShowingStoreList", false))
				showStoreList();
		}
	}

	private void configureSlidingMenu() {
		mSlidingMenu = getSlidingMenu();//new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMode.LEFT_RIGHT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset_left, SlidingMode.LEFT);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset_left, SlidingMode.RIGHT);
		mSlidingMenu.setFadeDegree(0.00f);
		//mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		//mSlidingMenu.setMenu(R.layout.layout_lists, SlidingMode.LEFT);
		setBehindContentView(R.layout.layout_lists);
		mSlidingMenu.setMenu(R.layout.layout_peopleinside, SlidingMode.RIGHT);
		mSlidingMenu.setOnOpenedListener(onOpenedListener);
	}

	private OnOpenedListener onOpenedListener = new OnOpenedListener() {

		@Override
		public void onOpened() {
			EasyTracker.getTracker().sendView(getString(isLeftMenuShowing() ? isShowingStoreList ? R.string.view_storelist : R.string.view_storecategories : isRightMenuShowing() ? R.string.view_levelpicker : R.string.view_error));
		}
	};

	private void configureFragments() {
		FragmentManager fm = getSupportFragmentManager();

		mStoreCategoryListFragment = (StoreCategoryListFragment) fm.findFragmentById(R.id.fragment_categorylist);
		mStoreCategoryListFragment.setOnStoreCategoryChangedListener(this);

		mInfrastructureBarFragment = (InfrastructureBarFragment) fm.findFragmentById(R.id.fragment_infrabar);
		mInfrastructureBarFragment.setOnInfrastructureCategoryChangeListener(this);

		mStoreListFragment = (StoreListFragment) fm.findFragmentById(R.id.fragment_storelist);
		mStoreListFragment.setOnStoreSelectedListener(this);
		mStoreListFragment.setStoreListController(this);

		//mPeopleInsideFragment = (PeopleInsideFragment) fm.findFragmentById(R.id.fragment_peopleinside);
	}

	private void configureAllLayout() {
		configureLayoutCategoryList();
		configureLayoutStoreList();
		mClearMarkersButton = findViewById(R.id.btn_clear_markers);
		mLevelPickerView = (LevelPickerView) findViewById(R.id.level_picker);
		mLevelPickerView.setOnLevelSelectedListener(this);
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

	private void loadInformationFromApplicationDataFacade() {
		mApplicationDataFacade = ((InMapApplication)getApplication()).getApplicationDataFacade();
		mLevelSelectedListeners = mApplicationDataFacade.getOnLevelSelectedListeners();
		mInfrastructureCategoryChangedListeners = mApplicationDataFacade.getOnInfrastructureCategoryChangedListeners();
		mStoreOnMapController = mApplicationDataFacade.getStoreOnMapController();
		mInMapFragment = (InMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
		mInMapFragment.setApplicationDataFacade(mApplicationDataFacade);
		mInMapViewController = mInMapFragment;
		mInMapViewController.setOnStoreBallonClickListener(this);
		mInMapViewController.setMapController(mApplicationDataFacade.getMapController());
	}

	private void toggleRightMenu() {
		if(isRightMenuShowing())
			mSlidingMenu.showContent();
		else 
			mSlidingMenu.showSecondaryMenu();
	}

	protected void toggleList() {
		if(isLeftMenuShowing())
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
				if(isLeftMenuShowing())
					toggleList();
				showStoreOnMap(store);
				return true;
			}
		}
		return false;
	}

	private void showStoreOnMap(Store store) {
		//mLevelPickerFragment.selectLevel(store.getLevel());
		mLevelPickerView.selectLevel(store.getLevel());
		mStoreOnMapController.setStores(store);
		mInMapViewController.openStoreBallon(store);
		storesHasMarkers = store != null;
		updateClearMarkersVisibility();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		verifyIntentShowStoreOnMap(intent);
		verifyIntentSearch(intent);
	}

	private boolean verifyIntentSearch(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			EasyTracker.getTracker().sendEvent("UserAction", "Search", query, 0l);
			if(query.length() > 2 && query.endsWith("s")) // To search plurals as singular (pt)
				query = query.substring(0, query.length()-1);
			mStoreListFragment.setStoreParameters(new StoreParameters().setText(query));
			if(!isLeftMenuShowing())
				toggleList();
			if(!isShowingStoreList)
				showStoreList();
			mDbAdapter.open();
			try {
				mDbAdapter.saveSearchPerformed(null, query); // FIXME Get facebook id if logged in
			}finally {
				mDbAdapter.close();
			}
			return true;
		}else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// Handle a suggestions click (because the suggestions all use ACTION_VIEW)
			long id = Long.parseLong(intent.getDataString());
			Store store;
			mDbAdapter.open();
			try {
				store = mDbAdapter.getStore(id);
			}finally {
				mDbAdapter.close();
			}
			onStoreSelected(store);
			return true;
		}else if(intent.getBooleanExtra(SHOW_SEARCH, false)) {
			onSearchClicked();
			return true;
		}
		return false;
	}

	public void onClearMarkersButtonClick(View v) {
		mStoreOnMapController.clearMarkers();
		storesHasMarkers = false;
		onInfrastructureCategoryChanged(0);
		mInfrastructureBarFragment.clearSelection();
	}

	public void updateClearMarkersVisibility() {
		mClearMarkersButton.setVisibility(storesHasMarkers || infraHasMarkers ? View.VISIBLE : View.INVISIBLE);
	}

	public void updateClearMarkersVisibility(int visible) {
		mClearMarkersButton.setVisibility(visible);
	}

	private void showSplash() {
		SplashDialogFragment splashDialogFragment = new SplashDialogFragment();
		splashDialogFragment.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				mInMapViewController.moveMapViewToPlacePosition(false, true);
				mShowingSplash = false;
				if(!WebUtils.isOnline(MainActivity.this))
					Toast.makeText(MainActivity.this, R.string.msg_no_internet_maps, Toast.LENGTH_LONG).show();
			}
		});
		splashDialogFragment.show(getSupportFragmentManager(), "SplashDialogFragment");
		mShowingSplash = true;
	}

	public boolean isShowingSplash() {
		return mShowingSplash;
	}

	private boolean isLeftMenuShowing() {
		return mSlidingMenu.isMenuShowing();
	}

	private boolean isRightMenuShowing() {
		return mSlidingMenu.isSecondaryMenuShowing();
	}

	private void startSimilarityBuilder() {
		mDbAdapter.open();
		try{
			mDbAdapter.clearRecommendation();
		} finally {
			mDbAdapter.close();
		}
		startService(new Intent(this, SimilarityBuilderService.class));
	}

}
