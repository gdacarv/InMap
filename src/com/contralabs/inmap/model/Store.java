package com.contralabs.inmap.model;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.contralabs.inmap.interfaces.StoreMapItem;
import com.contralabs.inmap.salvadorshop.applicationdata.StoreCategory;

public class Store implements StoreMapItem, Serializable{
	
	private static final long serialVersionUID = 2516026149726961993L; 
	
	private String name, description, phone, website, tags[], extra;
	private StoreCategory mCategory;
	private int level;
	private int area[];
	private long id;
	
	public Store(long id, String name, String description, String phone, String website, StoreCategory mCategory, int level, String[] tags, int[] area, String extra) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.phone = phone;
		this.website = website;
		this.tags = tags;
		this.mCategory = mCategory;
		this.level = level;
		this.area = area;
		this.extra = extra;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description; 
	}

	public String getPhone() {
		return phone;
	}

	public String getWebsite() {
		return website;
	}

	public String[] getTags() {
		return tags;
	}

	public StoreCategory getCategory() {
		return mCategory;
	}

	public int getLevel() {
		return level;
	}

	public int[] getArea() {
		return area;
	}

	public long getId() {
		return id;
	}

	@Override
	public int getX() {
		return (area[0] + area[2])/2;
	}

	@Override
	public int getY() {
		return (area[1] + area[3])/2;
	}

	@Override
	public int getMapIconResId() {
		return mCategory.getMapIconResId();
	}

	/*@Override // Useful only when using InMapImageView to determinate clickable area of marker
	public float getCircularArea() {
		Bitmap bmp = getMapIconBitmap();
		if(bmp != null){
			return (bmp.getHeight()+bmp.getWidth())/3;
		}
		return 0;
	}*/

	@Override
	public String getTitle() {
		return name;
	}

	@Override
	public Store getStore() {
		return this;
	}

	@Override
	public String getSubtext() {
		return description;// XXX Not the best choice...
	}
	
	public String getExtra() {
		return extra;
	}
}
