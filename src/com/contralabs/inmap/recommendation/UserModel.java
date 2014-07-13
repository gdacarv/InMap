package com.contralabs.inmap.recommendation;

import java.util.Map;
import java.util.Map.Entry;

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
	
	public String toJavaCode(){
		StringBuilder sb = new StringBuilder(500);
		sb.append("new UserModel(\"").append(name).append("\",new HashMap<Long, String[]>() {{");
		for(Entry<Long, String[]> sdv : storeDetailsView.entrySet())
			sb.append("put(Long.valueOf(").append(sdv.getKey()).append("l), \"").append(Utils.arrayToString(sdv.getValue(), ",")).append("\".split(\",\"));");
		sb.append("}},\"").append(Utils.arrayToString(searchPerformed, ",")).append("\".split(\",\"),");
		sb.append("new int[]{");
		for(int cv : categoriesVisited)
			sb.append(cv).append(',');
		sb.deleteCharAt(sb.length()-1);
		sb.append("})");
		return sb.toString();
	}
}
