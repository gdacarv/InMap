package com.inmap.salvadorshop.applicationdata;

import com.google.android.gms.maps.model.LatLng;
import com.inmap.salvadorshop.R;
import com.inmap.interfaces.LevelInformation;

public class SalvadorShopLevelInformation implements LevelInformation {

	private String[] levelTitles = {"G1", "G2", "L1", "L2", "L3"};
	private int [] maps = {R.drawable.mapg1, R.drawable.mapg2, R.drawable.mapl1, R.drawable.mapl2, R.drawable.mapl3};
	
	private LatLng[][] levelsBounds = {
			{new LatLng(-12.978040608785639d,-38.455534391105175d), new LatLng(-12.979154367169741d,-38.45446050167084d)}, // G1
			{new LatLng(-12.977770417894650d,-38.455814346671104d), new LatLng(-12.979030870592828d,-38.45444340258837d)}, // G2
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}, // L1
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}, // L2 TODO Right bounds
			{new LatLng(-12.976628229215127d,-38.456756472587585d), new LatLng(-12.979861040846481d,-38.45281630754471d)}  // L3 TODO Right bounds
	};
	
	private double[][] levelsPosition = {
			{-12.978595704875309d, -38.454998320435420d},  // G1             
			{-12.978399991619712d, -38.455129712820005d},  // G2 
			{-12.978243170083285d, -38.454786825342274d},  // L1                  
			{-12.978243170083285d, -38.454786825342274d},  // L2 TODO Right position
			{-12.978243170083285d, -38.454786825342274d}   // L3 TODO Right position
	};
	
	private float[] levelsWidth = { // In meters
			100f,  // G1       
			90f,   // G2    
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

}
