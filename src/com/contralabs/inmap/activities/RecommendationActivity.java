package com.contralabs.inmap.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.RecommendationStoreListFragment;
import com.contralabs.inmap.fragments.StoreListFragment.OnStoreSelectedListener;
import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.recommendation.SimilarityBuilderService;
import com.google.analytics.tracking.android.EasyTracker;

public class RecommendationActivity extends SherlockFragmentActivity implements OnStoreSelectedListener{
	
	public static int RECOMMEND_QUANTITY = 12;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_recommendation);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		RecommendationStoreListFragment storeListFragment = (RecommendationStoreListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_storelist);
		storeListFragment.setOnStoreSelectedListener(this);
		storeListFragment.setHeaderVisibility(View.GONE);
		storeListFragment.setAutoSortByTitle(false);
		DbAdapter db = DbAdapter.getInstance(this).open();
		try{
			storeListFragment.setStores(db.getStoresFromSimilarityScore(null, RECOMMEND_QUANTITY)); // TODO Fix user
		} finally {
			db.close();
		}
	}
	
	private BroadcastReceiver onSimilarityUpdated = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
        	RecommendationStoreListFragment storeListFragment = (RecommendationStoreListFragment) getSupportFragmentManager().findFragmentById(R.id.frag_storelist);
    		DbAdapter db = DbAdapter.getInstance(RecommendationActivity.this).open();
    		try{
    			storeListFragment.setStores(db.getStoresFromSimilarityScore(null, RECOMMEND_QUANTITY)); // TODO Fix user
    		} finally {
    			db.close();
    		}
         
        }
    };
	
	@Override
	public void onPause() {
        super.onPause();
         
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onSimilarityUpdated);
    }
	
    @Override
	public void onResume() {
        super.onResume();
         
        IntentFilter iff= new IntentFilter(SimilarityBuilderService.ACTION_SIMILIRARITY_BUILD_FINISHED);
        LocalBroadcastManager.getInstance(this).registerReceiver(onSimilarityUpdated, iff);
    }

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onStoreSelected(Store store) {
		if(store != null) {
			Intent i = new Intent(this, StoreDetailsActivity.class);
			i.putExtra(StoreDetailsActivity.STORE, store);
			startActivity(i);
		}
	}
}
