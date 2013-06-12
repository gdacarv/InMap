package com.contralabs.inmap.social;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class FacebookHelper {

	private static final String TAG = "FacebookHelper";

	private static final String FACEBOOK_ID_KEY = "inmap.facebook_id_key";

	public static String getFacebookId(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(FACEBOOK_ID_KEY, null);
	}

	public static void setFacebookId(Context context, String fbId) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(FACEBOOK_ID_KEY, fbId);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			editor.apply();
		else
			editor.commit();
	}

	public static void requestFriends(final UsersCallback callback) {
		Session session = Session.getActiveSession();
		if(session == null || !session.isOpened()) {
			callback.onReceived(null);
			return;
		}
		Request.executeMyFriendsRequestAsync(session, new GraphUserListCallback() {

			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				if(users == null) {
					Log.e(TAG, response.toString());
					callback.onReceived(null);
					return;
				}
				List<User> myUsers = new ArrayList<User>(users.size());
				for(GraphUser user : users)
					myUsers.add(new SimpleUser(user.getName(), user.getId()));
				callback.onReceived(myUsers);
			}
		});
	}

	public static Intent getOpenFacebookIntent(Context context, String fbId) {

		try {
			context.getPackageManager().getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"+fbId)); //Trys to make intent with FB's URI
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://www.facebook.com/"+fbId)); //catches and opens a url to the desired page
		}
	}
}
