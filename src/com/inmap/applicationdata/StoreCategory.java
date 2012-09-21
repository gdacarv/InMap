package com.inmap.applicationdata;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.inmap.R;

public enum StoreCategory {
	CLOTHING_MAN(1, R.string.title_clothing_man, R.drawable.ic_home),
	CLOTHING_WOMAN(2, R.string.title_clothing_woman, R.drawable.ic_home),
	GIFTS(3, R.string.title_gifts, R.drawable.ic_home),
	SHOES(4, R.string.title_shoes, R.drawable.ic_home),
	ELETRONICS(5, R.string.title_eletronics, R.drawable.ic_home),
	FOOD(6, R.string.title_food, R.drawable.ic_home),
	ENTERTAINMENT(7, R.string.title_entertainment, R.drawable.ic_home),
	BOOKSTORE(8, R.string.title_bookstore, R.drawable.ic_home),
	BANK(9, R.string.title_bank, R.drawable.ic_home),
	SERVICES(10, R.string.title_services, R.drawable.ic_home),
	CARRIER(11, R.string.title_carrier, R.drawable.ic_home), 
	DEPARTMENT(12, R.string.title_department, R.drawable.ic_home), 
	HEALTH(13, R.string.title_health, R.drawable.ic_home);
	
	private int id, titleRes, mapIconRes;
	private Bitmap mBitmap;
	
	public static final int length = values().length; 

	private StoreCategory(int id, int title_res, int mapIconRes){
		this.id = id;
		this.titleRes = title_res;
		this.mapIconRes = mapIconRes;
	}
	
	public int getId() {
		return id;
	}

	public int getTitleRes() {
		return titleRes;
	}
	
	public Bitmap getMapIconBitmap() {
		return mBitmap;
	}
	
	public void loadMapIconBitmap(Resources res){
		if(mBitmap == null)
			mBitmap = BitmapFactory.decodeResource(res, mapIconRes);
	}
}
