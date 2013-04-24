package com.contralabs.inmap.views;


public class TranslateHelper {
	
	private float xLast, yLast, totalMoved;
	
	private TranslateItem mTranslateItem;

	public TranslateHelper(TranslateItem item, float x, float y) {
		xLast = x;
		yLast = y;
		mTranslateItem = item;
		totalMoved = 0;
	}

	public void move(float x, float y) {
		totalMoved += Math.abs(x - xLast) + Math.abs(y - yLast);
		mTranslateItem.move(x - xLast, y - yLast);
		xLast = x;
		yLast = y;
	}
	
	public float getTotalMoved(){
		return totalMoved;
	}

	public interface TranslateItem{
		void move(float f, float g);
	}
}
