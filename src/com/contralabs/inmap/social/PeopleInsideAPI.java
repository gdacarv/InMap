package com.contralabs.inmap.social;

public interface PeopleInsideAPI {

	void onEnteredArea(User user, boolean showToOthers);
	
	void onExitedArea(User user, boolean showToOthers);
	
	void requestPeopleInside(UsersCallback callback);

}
