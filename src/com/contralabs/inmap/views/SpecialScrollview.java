package com.contralabs.inmap.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SpecialScrollview extends ScrollView{

	public SpecialScrollview(Context context) {
		super(context);
	}

	public SpecialScrollview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SpecialScrollview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			super.onTouchEvent(ev);
			break;

		case MotionEvent.ACTION_MOVE:
			return false; // redirect MotionEvents to ourself

		case MotionEvent.ACTION_CANCEL:
			super.onTouchEvent(ev);
			break;

		case MotionEvent.ACTION_UP:
			return false;

		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		super.onTouchEvent(ev);
		return true;
	}
}