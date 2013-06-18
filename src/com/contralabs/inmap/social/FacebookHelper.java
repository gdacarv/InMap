package com.contralabs.inmap.social;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.contralabs.inmap.R;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;

public class FacebookHelper {

	private static final String TAG = "FacebookHelper";

	private static final String FACEBOOK_ID_KEY = "inmap.facebook_id_key";
	private static final String NAME_KEY = "inmap.name_key";
	private static final String EMAIL_KEY = "inmap.email_key";
	private static final String BIRTHDAY_KEY = "inmap.birthday_key";

	private static User mUser;

	public static User getUser(Context context) {
		/*Session session = Session.getActiveSession();
		if(session == null || !session.isOpened()) // Maybe this cause a bug when user go inside the place because the application will not attempt to login on facebook
			return null;*/
		if(mUser == null) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String fbId = prefs.getString(FACEBOOK_ID_KEY, null);
			if(fbId != null)
				mUser = new SimpleUser(
						prefs.getString(NAME_KEY, null), 
						fbId, 
						prefs.getString(EMAIL_KEY, null), 
						prefs.getString(BIRTHDAY_KEY, null));
		}
		return mUser;
	}

	public static void setUser(Context context, User user) {
		mUser = user;
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		if(mUser != null) {
			editor.putString(FACEBOOK_ID_KEY, mUser.getFacebookId());
			editor.putString(NAME_KEY, mUser.getName());
			editor.putString(EMAIL_KEY, mUser.getEmail());
			editor.putString(BIRTHDAY_KEY, mUser.getBirthday());
		} else {
			editor.putString(FACEBOOK_ID_KEY, null);
			editor.putString(NAME_KEY, null);
			editor.putString(EMAIL_KEY, null);
			editor.putString(BIRTHDAY_KEY, null);
		}
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
					myUsers.add(new SimpleUser(user.getName(), user.getId(), null, user.getBirthday()));
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

	public static void inviteFriends(Context context) {
		Bundle params = new Bundle();
		params.putString("message", context.getString(R.string.invite_msg));
		showDialogWithoutNotificationBar(context, "apprequests", params);
	}

	private static void showDialogWithoutNotificationBar(Context context, String action, Bundle params) {
		WebDialog dialog = new WebDialog.Builder(context, Session.getActiveSession(), action, params).
		/*setOnCompleteListener(new WebDialog.OnCompleteListener() {
			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error != null && !(error instanceof FacebookOperationCanceledException)) {
					((HomeActivity)getActivity()).
					showError(getResources().getString(R.string.network_error), false);
				}
				dialog = null;
				dialogAction = null;
				dialogParams = null;
			}
		}).*/build();

		/*Window dialog_window = dialog.getWindow(); // Remove notification bar
		dialog_window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

		dialog.show();
	}
}
