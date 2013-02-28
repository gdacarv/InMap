package com.inmap.model;

public class Coordinate {
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x,y;
	
	@Override
	public String toString() {
		return "x = " + x + " y = " + y;
	}
}
