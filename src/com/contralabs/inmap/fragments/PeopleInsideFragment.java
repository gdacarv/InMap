package com.contralabs.inmap.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.contralabs.inmap.R;
import com.contralabs.inmap.social.DummyPeopleInsideAPI;
import com.contralabs.inmap.social.StringsCallback;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class PeopleInsideFragment extends FacebookFragment {

	private View mLoginLayout;
	private ExpandableListView mListView;
	private ExpandableListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		if(mAdapter == null)
			mAdapter = new PeopleInsideExpandableListAdapter(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_peopleinside, container, false);
		mLoginLayout = view.findViewById(R.id.peopleinside_login_layout);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("email", "user_birthday"));
		authButton.setFragment(this);
		updateLoginLayoutVisibility(Session.getActiveSession());
		mListView = (ExpandableListView) view.findViewById(R.id.peopleinside_list);
		mListView.setAdapter(mAdapter);
		return view;
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		super.call(session, state, exception);
		updateLoginLayoutVisibility(session);
	}

	private void updateLoginLayoutVisibility(Session session) {
		mLoginLayout.setVisibility(session == null || !session.isOpened() ? View.VISIBLE : View.INVISIBLE);
	}
	
	private class PeopleInsideExpandableListAdapter extends BaseExpandableListAdapter{

		private Context mContext;
		private List<String> mFriends = new ArrayList<String>(5), mUnknown = new ArrayList<String>();

		public PeopleInsideExpandableListAdapter(Context context) {
			mContext = context;
			
			new DummyPeopleInsideAPI().requestPeopleInside(new StringsCallback() {
				
				@Override
				public void onReceived(List<String> strings) {
					mFriends.addAll(strings);
				}
			});
		}

		@Override
		public int getGroupCount() {
			return 2;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupPosition == 0 ? mFriends.size() : mUnknown.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupPosition == 0 ? mFriends : mUnknown;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groupPosition == 0 ? mFriends.get(childPosition) : mUnknown.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = new TextView(mContext);
			((TextView)convertView).setText(groupPosition == 0 ? R.string.amigos : R.string.desconhecido);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(mContext, R.layout.listitem_people, null);
			Object child = getChild(groupPosition, childPosition);
			((ProfilePictureView) convertView.findViewById(R.id.people_pic)).setProfileId((String) child);
			((TextView) convertView.findViewById(R.id.people_name)).setText((CharSequence) child); // FIXME show real name
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
