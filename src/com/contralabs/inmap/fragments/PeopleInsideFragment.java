package com.contralabs.inmap.fragments;

import java.util.Arrays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.contralabs.inmap.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;

public class PeopleInsideFragment extends FacebookFragment {

	private View mLoginLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_peopleinside, container, false);
		mLoginLayout = view.findViewById(R.id.peopleinside_login_layout);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("email", "user_birthday"));
		authButton.setFragment(this);
		updateLoginLayoutVisibility(Session.getActiveSession());
		return view;
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		updateLoginLayoutVisibility(session);
	}

	private void updateLoginLayoutVisibility(Session session) {
		mLoginLayout.setVisibility(session == null || !session.isOpened() ? View.VISIBLE : View.INVISIBLE);
	}
}
