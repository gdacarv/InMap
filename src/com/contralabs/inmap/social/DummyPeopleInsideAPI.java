package com.contralabs.inmap.social;

import java.util.ArrayList;
import java.util.List;

public class DummyPeopleInsideAPI implements PeopleInsideAPI { // FIXME Delete this class

	private static final String[] DUMMY_PEOPLE_ID = {"100001122794655", "100004981209104", "100002191014556", "100000046614745", "100001640110199", "1302973456" };
	private static final String[] DUMMY_PEOPLE_NAME = {"Victor Cardozo", "Fallux Careca", "Yuri Fernandes", "Kasia Boncheska", "Gustavo Carvalho", "Isis Mesquita" };
	

	@Override
	public void onEnteredArea(String id, boolean showToOthers) {
	}

	@Override
	public void onExitedArea(String id) {
	}

	@Override
	public void requestPeopleInside(UsersCallback callback) {
		List<User> users = new ArrayList<User>(DUMMY_PEOPLE_ID.length);
		for(int i = 0; i < DUMMY_PEOPLE_ID.length; i++)
			users.add(new SimpleUser(DUMMY_PEOPLE_NAME[i], DUMMY_PEOPLE_ID[i]));
		callback.onReceived(users);
	}

}
