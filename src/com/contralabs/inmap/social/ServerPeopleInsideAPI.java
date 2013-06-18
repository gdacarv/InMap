package com.contralabs.inmap.social;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.contralabs.inmap.server.LoggerInputStreamHandler;
import com.contralabs.inmap.server.WebUtils;

public class ServerPeopleInsideAPI implements PeopleInsideAPI {
	
	private static final String SERVER_URL = "http://inmap.contralabs.com/peopleinside";

	@Override
	public void onEnteredArea(User user, boolean showToOthers) {
		doPostRequestOnLocationChange(user, showToOthers, true);
	}

	@Override
	public void onExitedArea(User user, boolean showToOthers) {
		doPostRequestOnLocationChange(user, showToOthers, false);
	}

	@Override
	public void requestPeopleInside(UsersCallback callback) {
		WebUtils.loadURLAsync(SERVER_URL, new UsersInputStreamHandler(), callback);
	}

	private void doPostRequestOnLocationChange(User user, boolean showToOthers, boolean inside) {
		WebUtils.doPostRequestAsync(SERVER_URL, new LoggerInputStreamHandler("onInside " + inside), getParameters(user, showToOthers, inside), null);
	}

	private List<? extends NameValuePair> getParameters(User user, boolean showToOthers, boolean inside) {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>(6);
		parameters.add(new BasicNameValuePair("facebookId", user.getFacebookId()));
		parameters.add(new BasicNameValuePair("name", user.getName()));
		parameters.add(new BasicNameValuePair("email", user.getEmail()));
		parameters.add(new BasicNameValuePair("birthday", user.getBirthday()));
		parameters.add(new BasicNameValuePair("inside", String.valueOf(inside)));
		parameters.add(new BasicNameValuePair("showInsideToOthers", String.valueOf(showToOthers)));
		return parameters;
	}

}
