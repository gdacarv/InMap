package com.inmap.fragments;

import android.support.v4.app.Fragment;

public abstract class ExtraFragment extends Fragment {
	
	private boolean isReady = false;
	private OnReadyChangeListener mListener;

	public abstract String getTitle();

	public abstract int getIconResId();
	
	public boolean isReady() {
		return isReady;
	}
	
	public void setReady(boolean ready) {
		isReady = ready;
		if(mListener != null)
			mListener.onReadyChanged(ready);
	}
	
	public void setOnReadyChangeListener(OnReadyChangeListener listener) {
		mListener = listener;
	}
	
	public interface OnReadyChangeListener{
		void onReadyChanged(boolean ready);
	}
}
