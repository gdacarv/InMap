package com.inmap.salvadorshop.applicationdata;

import com.inmap.salvadorshop.R;

public enum InfrastructureCategory {
	EXIT(1, R.drawable.ic_action_share, 0),
	PARKING(2, R.drawable.ic_action_share, 0),
	STAIRS(3, R.drawable.ic_action_share, 0), 
	ELEVATOR(4, R.drawable.ic_action_share, 0), 
	TOILET(5, R.drawable.ic_action_share, 0), 
	ESCALATOR(6, R.drawable.ic_action_share, 0), 
	PHONES(7, R.drawable.ic_action_share, 0), 
	BABY_FACILITIES(8, R.drawable.ic_action_share, 0);
	
	private int id, menuIconRes, mapIconRes;

	private InfrastructureCategory(int id, int menuIconRes, int mapIconRes){
		this.id = id;
		this.menuIconRes = menuIconRes;
		this.mapIconRes = mapIconRes;
	}

	public int getId() {
		return id;
	}

	public int getMenuIconResId() {
		return menuIconRes;
	}

	public int getMapIconResId() {
		return mapIconRes;
	}

}
