package com.inmap.applicationdata;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.inmap.R;

public enum InfrastructureCategory {
	EXIT(1, R.drawable.ic_action_share, R.drawable.marker),
	PARKING(2, R.drawable.ic_action_share, R.drawable.marker),
	STAIRS(3, R.drawable.ic_action_share, R.drawable.marker), 
	ELEVATOR(4, R.drawable.ic_action_share, R.drawable.marker), 
	TOILET(5, R.drawable.ic_action_share, R.drawable.marker);
	
	private int id, menuIconRes, mapIconRes;
	private Bitmap mBitmap;

	private InfrastructureCategory(int id, int menuIconRes, int mapIconRes){
		this.id = id;
		this.menuIconRes = menuIconRes;
		this.mapIconRes = mapIconRes;
	}

	public int getId() {
		return id;
	}

	public int getMenuIconRes() {
		return menuIconRes;
	}

	public Bitmap getMapIconBitmap() {
		return mBitmap;
	}
	
	public void loadMapIconBitmap(Resources res){
		if(mBitmap == null)
			mBitmap = BitmapFactory.decodeResource(res, mapIconRes);
	}

}
