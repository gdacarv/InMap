package com.contralabs.inmap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.contralabs.inmap.R;
import com.contralabs.inmap.fragments.RecommendationStoreListFragment;
import com.contralabs.inmap.fragments.StoreListFragment.OnStoreSelectedListener;
import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.model.Store;
import com.google.analytics.tracking.android.EasyTracker;

public class RecommendationActivity extends SherlockFragmentActivity implements OnStoreSelectedListener{
	
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
			storeListFragment.setStores(db.getStoresFromSimilarityScore(null, 15)); // TODO Fix user
		} finally {
			db.close();
		}
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
