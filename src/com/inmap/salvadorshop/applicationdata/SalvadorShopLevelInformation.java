package com.inmap.salvadorshop.applicationdata;

import com.inmap.R;
import com.inmap.interfaces.LevelInformation;

public class SalvadorShopLevelInformation implements LevelInformation {

	private String[] levelTitles = {"L3", "L2", "L1", "G2", "G1"};
	private int [] maps = {R.drawable.mapl3, R.drawable.mapl2, R.drawable.mapl1, R.drawable.mapg2, R.drawable.mapg1};
	
	@Override
	public int getLevelsCount() {
		return levelTitles.length;
	}

	@Override
	public String getTitle(int position) {
		return levelTitles[position];
	}

	@Override
	public int getMapResource(int position) {
		return maps[position];
	}

	@Override
	public int initializerLevel() {
		return 2;
	}

	
}
