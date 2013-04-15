package com.inmap.salvadorshop.applicationdata;

import com.google.android.gms.maps.model.LatLng;
import com.inmap.salvadorshop.R;
import com.inmap.interfaces.LevelInformation;

public class SalvadorShopLevelInformation implements LevelInformation {

	private String[] levelTitles = {"G1", "G2", "L1", "L2", "L3"};
	private int [] maps = {R.drawable.mapg1, R.drawable.mapg2, R.drawable.mapl1, R.drawable.mapl2, R.drawable.mapl3};
	private int [] menuIcons = {R.drawable.bt_level_g1, R.drawable.bt_level_g2, R.drawable.bt_level_l1, R.drawable.bt_level_l2, R.drawable.bt_level_l3};
	
	private LatLng[][] levelsBounds = {
			{new LatLng(-12.977679918582552d,-38.455763384699820d), new LatLng(-12.979128883754134d,-38.45436695963144d)}, // G1
			{new LatLng(-12.977589092525022d,-38.455971255898476d), new LatLng(-12.979127250201760d,-38.45429621636867d)}, // G2
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}, // L1
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}, // L2 TODO Right bounds
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}  // L3 TODO Right bounds
	};
	
	private double[][] levelsPosition = {
			{-12.978403585445427d, -38.455065675079820},  // G1             
			{-12.978357845841520d, -38.455133736133575d},  // G2 
			{-12.978243170083285d, -38.454786825342274d},  // L1                  
			{-12.978243170083285d, -38.454786825342274d},  // L2 TODO Right position
			{-12.978243170083285d, -38.454786825342274d}   // L3 TODO Right position
	};
	
	private float[] levelsWidth = { // In meters
			130f,  // G1       
			110f,   // G2    
			170f,  // L1            
			170f,  // L2 TODO Right position
			170f,  // L3 TODO Right position
	};
	
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
		return levelsPosition[i][0];
	}

	@Override
	public double getLevelLongitude(int i) {
		return levelsPosition[i][1];
	}

	@Override
	public float getLevelWidth(int i) {
		return levelsWidth[i];
	}

	@Override
	public LatLng getNorthwestBound(int level) {
		return levelsBounds[level][0];
	}

	@Override
	public LatLng getSoutheastBound(int level) {
		return levelsBounds[level][1];
	}

	@Override
	public int getMenuIconResId(int level) {
		return menuIcons[level];
	}

}
