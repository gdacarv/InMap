package com.contralabs.inmap.recommendation;

import java.util.Map;

import com.contralabs.inmap.utils.Utils;

public class UserModel {
	
	public Map<Long, String[]> storeDetailsView;
	public String[] searchPerformed;
	public int[] categoriesVisited;
	public String name;
	
	public UserModel(String name, Map<Long, String[]> storeDetailsView, String[] searchPerformed, int[] categoriesVisited){
		this.name = name;
		this.storeDetailsView = storeDetailsView;
		this.searchPerformed = searchPerformed;
		this.categoriesVisited = categoriesVisited;
	}

	public String getAllStoreDetailViewTags() {
		StringBuilder allTags = new StringBuilder();
		for(String[] tags : storeDetailsView.values()){
			allTags.append(Utils.arrayToString(tags, ",")).append(',');
		}
		if(allTags.length() > 0) allTags.deleteCharAt(allTags.length()-1);
		return allTags.toString();
	}
}
