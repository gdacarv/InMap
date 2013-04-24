package com.contralabs.inmap.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class OverScrollHorizontalScrollView extends HorizontalScrollView {

	private boolean lastClampedX = true;
	private OnEdgeScrollListener mListener;

	public OverScrollHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public OverScrollHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OverScrollHorizontalScrollView(Context context) {
		super(context);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		if(lastClampedX != clampedX) {
			if(mListener != null)
				mListener.onEdgeScrollChange(clampedX && scrollX <=0, clampedX && scrollX > 0);
			lastClampedX = clampedX;
		}
	}
	
	public void setOnEdgeScrollListener(OnEdgeScrollListener listener) {
		mListener = listener;
	}
	
	public interface OnEdgeScrollListener{
		void onEdgeScrollChange(boolean leftEdge, boolean rightEdge);
	}
}
