package com.contralabs.inmap.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;

import com.contralabs.inmap.interfaces.OnAnimationEnd;
import com.contralabs.inmap.views.SplashImageView;
import com.contralabs.inmap.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

public class SplashDialogFragment extends DialogFragment {

	private OnAnimationEnd mOnAnimationEndListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_FRAME, R.style.TransparentDialogTheme);
		setCancelable(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Display display = getActivity().getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		FragmentActivity activity = getActivity();
		FrameLayout layout = new FrameLayout(activity);
		layout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		layout.setBackgroundColor(Color.WHITE);
		SplashImageView splash = new SplashImageView(activity);
		splash.setScaleType(ScaleType.CENTER);
		layout.addView(splash, new ViewGroup.LayoutParams(width, height));
		splash.start(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				if(isVisible())
					dismissAllowingStateLoss();
				if(mOnAnimationEndListener != null)
					mOnAnimationEndListener.onAnimationEnded();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		}, R.drawable.img_splashscreen_01, R.drawable.img_splashscreen_02);
		return layout;
	}
	
	public void setOnAnimationEnd(OnAnimationEnd listener) {
		mOnAnimationEndListener = listener;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		Activity activity = getActivity();
		if(activity != null && !activity.isFinishing())
			activity.finish();
		super.onCancel(dialog);
	}
}
