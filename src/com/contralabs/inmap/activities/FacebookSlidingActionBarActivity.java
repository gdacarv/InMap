package com.contralabs.inmap.activities;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.Session.StatusCallback;
import com.facebook.UiLifecycleHelper;

public abstract class FacebookSlidingActionBarActivity extends SlidingActionBarActivity implements StatusCallback {

	private UiLifecycleHelper uiHelper;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    uiHelper = new UiLifecycleHelper(this, this);
	    uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
}
