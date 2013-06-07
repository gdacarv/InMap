package com.contralabs.inmap.social;

import java.util.Arrays;

public class DummyPeopleInsideAPI implements PeopleInsideAPI { // FIXME Delete this class
	
	private static final String[] DUMMY_PEOPLE = {"100001122794655", "100004981209104", "100002191014556", "100000046614745", "100001640110199" };

	@Override
	public void onEnteredArea(String id, boolean showToOthers) {
	}

	@Override
	public void onExitedArea(String id) {
	}

	@Override
	public void requestPeopleInside(StringsCallback callback) {
		callback.onReceived(Arrays.asList(DUMMY_PEOPLE));
	}

}
