package com.contralabs.inmap.social;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.contralabs.inmap.server.JSONInputStreamHandler;

public class UsersInputStreamHandler extends JSONInputStreamHandler {

	@Override
	protected Object handleJSONObject(JSONObject jsonObject) {
		if(jsonObject == null || !jsonObject.optBoolean("success"))
			return null;
		try {
			JSONArray array = jsonObject.getJSONArray("data");
			List<User> users = new ArrayList<User>(array.length());
			for(int i = 0, n = array.length(); i < n; i++) {
				JSONObject user = array.getJSONObject(i);
				users.add(
						new SimpleUser(
								user.getString("name"), 
								user.getString("facebookId"), 
								user.getString("email"), 
								user.getString("birthday")));
			}
			return users;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
