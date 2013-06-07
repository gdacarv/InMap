package com.contralabs.inmap.social;

public interface PeopleInsideAPI {

	void onEnteredArea(String id, boolean showToOthers);
	
	void onExitedArea(String id);
	
	void requestPeopleInside(StringsCallback callback);

}
