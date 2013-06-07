package com.contralabs.inmap.social;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;

public class FacebookHelper {

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
}
