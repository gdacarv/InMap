package com.contralabs.inmap.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.contralabs.inmap.R;
import com.contralabs.inmap.social.FacebookHelper;
import com.contralabs.inmap.social.PeopleInsideAPI;
import com.contralabs.inmap.social.ServerPeopleInsideAPI;
import com.contralabs.inmap.social.User;
import com.contralabs.inmap.social.UsersCallback;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class PeopleInsideFragment extends FacebookFragment {

	private static final int TIMERATE_REFRESH_PEOPLE_INSIDE = 40; // In Seconds
	private static final String TAG = "PeopleInsideFragment";
	private View mLoginLayout;
	private ExpandableListView mListView;
	private PeopleInsideExpandableListAdapter mAdapter;
	private PeopleInsideAPI mPeopleInsideAPI;
	private List<User> mUsersInside;
	private List<User> mAllFriends;
	private ScheduledExecutorService mScheduler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPeopleInsideAPI = new ServerPeopleInsideAPI();
		setRetainInstance(true);
		if(mAdapter == null)
			mAdapter = new PeopleInsideExpandableListAdapter(getActivity());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mScheduler = Executors.newSingleThreadScheduledExecutor();

		mScheduler.scheduleAtFixedRate
		      (new Runnable() {
		         public void run() {
		     		requestPeopleInside();
		     		Log.i(TAG, "People Inside refreshing...");
		         }
		      }, 0, TIMERATE_REFRESH_PEOPLE_INSIDE, TimeUnit.SECONDS);
	}
	
	@Override
	public void onPause() {
		mScheduler.shutdownNow();
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_peopleinside, container, false);
		mLoginLayout = view.findViewById(R.id.peopleinside_login_layout);
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("email", "user_birthday"));
		authButton.setFragment(this);
		mListView = (ExpandableListView) view.findViewById(R.id.peopleinside_list);
		mListView.setGroupIndicator(null);
		mListView.setOnChildClickListener(onChildClickListener);
		mListView.setAdapter(mAdapter);
		mListView.expandGroup(0);
		mListView.expandGroup(1);
		updateLayoutVisibility(isLoggedIn(Session.getActiveSession()));
		return view;
	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		super.call(session, state, exception);
		boolean loggedIn = isLoggedIn(session);
		updateLayoutVisibility(loggedIn);
		if(loggedIn)
			FacebookHelper.requestFriends(friendsCallback);
	}

	private UsersCallback friendsCallback = new UsersCallback() {
		

		@Override
		public void onReceived(List<User> users) {
			mAllFriends = users;
			refreshAdapterData();
		}
	};

	private boolean isLoggedIn(Session session) {
		return session != null && session.isOpened();
	}

	private void updateLayoutVisibility(boolean loggedIn) {
		mLoginLayout.setVisibility(loggedIn ? View.INVISIBLE : View.VISIBLE);
		mListView.setVisibility(loggedIn ? View.VISIBLE : View.INVISIBLE);
	}
	
	private void requestPeopleInside() {
		mPeopleInsideAPI.requestPeopleInside(peopleInsideCallback);
	}
	
	private UsersCallback peopleInsideCallback = new UsersCallback() {
		
		@Override
		public void onReceived(List<User> users) {
			mUsersInside = users;
			refreshAdapterData();
		}
	};

	private void refreshAdapterData() {
		if(mUsersInside == null || mAllFriends == null)
			return;
		List<User> friends, unknown;
		User userMe = FacebookHelper.getUser(getActivity());
		if(mAllFriends != null && mAllFriends.size() > 0) {
			friends = new ArrayList<User>();
			unknown = new ArrayList<User>(mUsersInside.size());
			for(User user : mUsersInside) {
				if(user.getFacebookId().equals(userMe.getFacebookId()))
					continue;
				if(isFriend(user))
					friends.add(user);
				else
					unknown.add(user);
			}
		} else {
			friends = null;
			unknown = mUsersInside;
		}
		
		mAdapter.setData(friends, unknown);
	}
	
	private boolean isFriend(User user) {
		if(mAllFriends != null) 
			for(User friend : mAllFriends)
				if(friend.getFacebookId().equals(user.getFacebookId()))
					return true;
		return false;
	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {
		
		@Override
		public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
			if(mAdapter.getChildType(groupPosition, childPosition) == mAdapter.getChildTypeCount()-1) { // Invite friends
				FacebookHelper.inviteFriends(getActivity());
				return true;
			}
			
			User user = (User) mAdapter.getChild(groupPosition, childPosition);
			startActivity(FacebookHelper.getOpenFacebookIntent(getActivity(), user.getFacebookId()));
			return true;
		}
	};

	private class PeopleInsideExpandableListAdapter extends BaseExpandableListAdapter{

		private Context mContext;
		private List<User> mFriends = new ArrayList<User>(0), mUnknown = new ArrayList<User>(0);

		public PeopleInsideExpandableListAdapter(Context context) {
			mContext = context;
		}

		public void setData(List<User> friends, List<User> unknown) {
			mFriends = friends;
			mUnknown = unknown;
			notifyDataSetChanged();
		}

		@Override
		public int getGroupCount() {
			return mUnknown.size() > 0 ? 2 : 1;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupPosition == 0 ? mFriends.size()+1 : mUnknown.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupPosition == 0 ? mFriends : mUnknown;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groupPosition == 0 ? childPosition < getChildrenCount(groupPosition)-1 ? mFriends.get(childPosition) : "Invite Friends" : mUnknown.get(childPosition);
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
			if(convertView == null) {
				convertView = View.inflate(mContext, R.layout.listitem_user_category, null);
			}
			((TextView)convertView.findViewById(R.id.cat_name)).setText(groupPosition == 0 ? R.string.amigos : R.string.desconhecido);
			((ImageView)convertView.findViewById(R.id.cat_img_state)).setImageResource(isExpanded ? R.drawable.bt_ic_dropdown_active : R.drawable.bt_ic_dropdown_default);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			if(getChildType(groupPosition, childPosition) == getChildTypeCount()-1) { // Is invite your friends
				TextView textView = (TextView) View.inflate(mContext, android.R.layout.simple_list_item_1, null);
				textView.setText(R.string.invite_label);
				textView.setGravity(Gravity.CENTER);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
			
			if(convertView == null)
				convertView = View.inflate(mContext, R.layout.listitem_people, null);
			User user = (User) getChild(groupPosition, childPosition);
			((ProfilePictureView) convertView.findViewById(R.id.people_pic)).setProfileId(user.getFacebookId());
			((TextView) convertView.findViewById(R.id.people_name)).setText(user.getName());
			return convertView;
		}
		
		@Override
		public int getChildTypeCount() {
			return super.getChildTypeCount()+1;
		}
		
		@Override
		public int getChildType(int groupPosition, int childPosition) {
			return groupPosition == 0 && childPosition < getChildrenCount(groupPosition)-1 ? super.getChildType(groupPosition, childPosition) : getChildTypeCount()-1;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
