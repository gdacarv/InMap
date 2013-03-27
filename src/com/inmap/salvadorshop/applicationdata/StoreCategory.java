package com.inmap.salvadorshop.applicationdata;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.inmap.salvadorshop.R;

public enum StoreCategory {
	CLOTHING_MAN(1, R.string.title_clothing_man, 0),
	CLOTHING_WOMAN(2, R.string.title_clothing_woman, 0),
	GIFTS(3, R.string.title_gifts, 0),
	SHOES(4, R.string.title_shoes, 0),
	ELETRONICS(5, R.string.title_eletronics, 0),
	FOOD(6, R.string.title_food, 0),
	ENTERTAINMENT(7, R.string.title_entertainment, 0),
	BOOKSTORE(8, R.string.title_bookstore, 0),
	BANK(9, R.string.title_bank, 0),
	SERVICES(10, R.string.title_services, 0),
	CARRIER(11, R.string.title_carrier, 0), 
	DEPARTMENT(12, R.string.title_department, 0), 
	HEALTH(13, R.string.title_health, 0), 
	UNISSEX(14, R.string.title_unissex, 0), 
	UNDERWEAR(15, R.string.title_underwear, 0), 
	COSMETIC(16, R.string.title_cosmetic, 0), 
	SPORTS(17, R.string.title_sports, 0), 
	BABY(18, R.string.title_baby, 0), 
	STREET(19, R.string.title_street, 0), 
	OPTICS(20, R.string.title_optics, 0), 
	BEDBATHTABLE(21, R.string.title_bedbathtable, 0), 
	JEWELRY(22, R.string.title_jewelry, 0), 
	ACCESSORIES(23, R.string.title_accessories, 0), 
	HOME(24, R.string.title_home, 0),
	UNCATEGORIZED(25, R.string.title_uncategorized, 0);
	
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
		if(mBitmap == null && mapIconRes > 0)
			mBitmap = BitmapFactory.decodeResource(res, mapIconRes);
	}
}
