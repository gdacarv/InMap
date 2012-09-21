package com.inmap.views;

import com.inmap.interfaces.OnAnimationEnd;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class AnimateFrameLayout extends FrameLayout {
	
	private OnAnimationEnd mOnAnimationEnd;

	public AnimateFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		if(mOnAnimationEnd != null)
			mOnAnimationEnd.onAnimationEnded();
	}
	
	public void setOnAnimationEnd(OnAnimationEnd listener){
		mOnAnimationEnd = listener;
	}
}
