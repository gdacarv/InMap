package com.contralabs.inmap.model;

import java.io.Serializable;

import com.contralabs.inmap.salvadorshop.applicationdata.StoreCategory;

public class StoreParameters implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3156944549918749802L;
	
	private String name, description, phone, website, tags[], anytext;
	private int[] mCategorys = new int[StoreCategory.length];
	private int categoryCounter = 0;
	private int level = -1;
	private int area[];
	private Coordinate hasPoint;

	public StoreParameters(){
	}

	public StoreParameters addCategory(int id){
		if(categoryCounter < mCategorys.length){
			mCategorys[categoryCounter] = id;
			categoryCounter++;
		}
		return this;
	}

	public StoreParameters setText(String text){
		anytext = text;
		return this;
	}

	public StoreParameters setName(String text) {
		name = text;
		return this;
	}

	public StoreParameters setDescription(String text) {
		description = text;
		return this;
	}

	public StoreParameters setPhone(String text) {
		phone = text;
		return this;
	}

	public StoreParameters setWebsite(String text) {
		website = text;
		return this;
	}

	public StoreParameters setTags(String text) {
		tags = text.split("[ ,]");
		return this;
	}

	public StoreParameters setLevel(int level){
		this.level = level;
		return this;
	}
	
	public StoreParameters hasPoint(Coordinate coord) {
		hasPoint = coord;
		return this;
	}

	public String getCategoryString(){
		if(categoryCounter == 0)
			return "";
		String result = "" + mCategorys[0];
		for(int i = 1; i < categoryCounter; i++)
			result += "," + mCategorys[i];
		return result;
	}
	
	public int getLevel() {
		return level;
	}
	
	public Coordinate getContainsPoint() {
		return hasPoint;
	}

	public String getAnytext() {
		return anytext;
	}

	/*public StoreParameters(String name, String description, String phone, String website, StoreCategory mCategory, int level, String[] tags, int[] area) {
		this.name = name;
		this.description = description;
		this.phone = phone;
		this.website = website;
		this.tags = tags;
		this.mCategory = mCategory;
		this.level = level;
		this.area = area;
	}*/
}
