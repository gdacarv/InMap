package com.inmap.salvadorshop.applicationdata;

import com.google.android.gms.maps.model.LatLng;
import com.inmap.salvadorshop.R;
import com.inmap.interfaces.LevelInformation;

public class SalvadorShopLevelInformation implements LevelInformation {

	private String[] levelTitles = {"L3", "L2", "L1", "G2", "G1"};
	private int [] maps = {R.drawable.mapl3, R.drawable.mapl2, R.drawable.mapl1, R.drawable.mapg2, R.drawable.mapg1};
	private LatLng northwestBound = new LatLng(-12.976698d,-38.456984d);
	private LatLng southeastBound = new LatLng(-12.980027d,-38.452822d);
	
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
	public int getInitLevel() {
		return 2;
	}

	@Override
	public double getLevelLatitude(int i) {
		return -12.978255d;
	}

	@Override
	public double getLevelLongitude(int i) {
		return -38.454847d;
	}

	@Override
	public float getLevelWidth(int i) {
		return 187.5f;
	}

	@Override
	public LatLng getNorthwestBound(int level) {
		return northwestBound;
	}

	@Override
	public LatLng getSoutheastBound(int level) {
		return southeastBound;
	}

	
}
