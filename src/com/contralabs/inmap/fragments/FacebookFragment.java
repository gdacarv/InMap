package com.contralabs.inmap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.contralabs.inmap.social.FacebookHelper;
import com.contralabs.inmap.social.SimpleUser;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

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
	
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if(session.isOpened()) {
			Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {


				@Override
				public void onCompleted(GraphUser user, Response response) {
					FragmentActivity activity = getActivity();
					if (user != null && activity != null) {
						FacebookHelper.setUser(activity, new SimpleUser(user.getName(), user.getId(), (String) response.getGraphObject().getProperty("email"), user.getBirthday()));
					}
				}
			});
		}else {
			FragmentActivity activity = getActivity();
			if(activity != null)
				FacebookHelper.setUser(activity, null);
		}
	}
}
