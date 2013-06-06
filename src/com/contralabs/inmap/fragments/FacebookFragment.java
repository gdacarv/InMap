package com.contralabs.inmap.fragments;

import com.facebook.Session.StatusCallback;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class FacebookFragment extends Fragment implements StatusCallback {
	
	private UiLifecycleHelper uiHelper;
	private SessionState mLastSessionState;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(getActivity(), this);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		Session activeSession = Session.getActiveSession();
		SessionState sessionState = activeSession != null ? activeSession.getState() : null;
		if(sessionState != mLastSessionState)
			call(activeSession, sessionState, null);
	}

	@Override
	public void onPause() {
		super.onPause();
		Session activeSession = Session.getActiveSession();
		mLastSessionState = activeSession != null ? activeSession.getState() : null;
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
}
