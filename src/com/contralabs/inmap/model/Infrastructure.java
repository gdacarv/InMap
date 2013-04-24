package com.contralabs.inmap.model;

import com.contralabs.inmap.interfaces.MapItem;
import com.contralabs.inmap.salvadorshop.applicationdata.InfrastructureCategory;

public class Infrastructure implements MapItem{
	private InfrastructureCategory category;
	private int level, x, y;
	
	public Infrastructure(InfrastructureCategory category, int level, int x, int y) {
		this.category = category;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public InfrastructureCategory getCategory() {
		return category;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getMapIconResId() {
		return category.getMapIconResId();
	}

}
