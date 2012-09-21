package com.inmap.views;

import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;

public class ZoomHelper {
	
	private float lastSpacing;
	private PointF midPoint;
	private ZoomItem mZoomItem;

	public ZoomHelper(ZoomItem item, MotionEvent event) {
		lastSpacing = spacing(event);
		midPoint = midPoint(event);
		mZoomItem = item;
	}

	public void zoom(MotionEvent event) {
		float newDist = spacing(event);
		mZoomItem.zoom(midPoint.x, midPoint.y, newDist/lastSpacing);
		lastSpacing = newDist;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	private PointF midPoint(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x / 2, y / 2);
		}

	public interface ZoomItem{
		void zoom(float x, float y, float zoom);
	}
}
