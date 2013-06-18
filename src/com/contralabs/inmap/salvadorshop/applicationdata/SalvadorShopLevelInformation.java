package com.contralabs.inmap.salvadorshop.applicationdata;

import com.contralabs.inmap.interfaces.LevelInformation;
import com.google.android.gms.maps.model.LatLng;
import com.contralabs.inmap.R;

public class SalvadorShopLevelInformation implements LevelInformation {

	private String[] levelTitles = {"G1", "G2", "L1", "L2", "L3"};
	private int [] maps = {R.drawable.mapg1, R.drawable.mapg2, R.drawable.mapl1, R.drawable.mapl2, R.drawable.mapl3};
	private int [] menuIcons = {R.drawable.img_nivel_g1, R.drawable.img_nivel_g2, R.drawable.img_nivel_l1, R.drawable.img_nivel_l2, R.drawable.img_nivel_l3};
	
	private LatLng[][] levelsBounds = {
			{new LatLng(-12.977679918582552d,-38.455763384699820d), new LatLng(-12.979128883754134d,-38.45436695963144d)}, // G1
			{new LatLng(-12.977589092525022d,-38.455971255898476d), new LatLng(-12.979127250201760d,-38.45429621636867d)}, // G2
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}, // L1
			{new LatLng(-12.976610913385269d,-38.456781953573230d), new LatLng(-12.979877703030981d,-38.45279149711132d)}, // L2
			{new LatLng(-12.977214353008742d,-38.455904871225360d), new LatLng(-12.979239311869557d,-38.45373999327421d)}  // L3 
	};
	
	private double[][] levelsPosition = {
			{-12.978403585445427d, -38.455065675079820d},  // G1             
			{-12.978357845841520d, -38.455133736133575d},  // G2 
			{-12.978243170083285d, -38.454786825342274d},  // L1                  
			{-12.978243170083285d, -38.454786825342274d},  // L2
			{-12.978225854365903d, -38.454822599887850d}   // L3 
	};
	
	private float[] levelsWidth = { // In meters
			130f,  // G1       
			110f,  // G2    
			170f,  // L1            
			170f,  // L2
			150f,  // L3 
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
