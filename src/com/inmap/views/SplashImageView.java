package com.inmap.views;

import android.content.Context;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

public class SplashImageView extends ImageView {
	
	private static final long DELAY = 800;
	private static final long STAND_DELAY = 1000;
	private int[] mImgResIds;
	private int index;
	private AnimatorListener mAnimatorListener;

	public SplashImageView(Context context) {
		super(context);
	}

	public void start(AnimatorListener listener, int... imgIds) {
		mImgResIds = imgIds;
		index = 0;
		mAnimatorListener = listener;
		nextAnimation();
	}

	private void nextAnimation() {
		setImageResource(mImgResIds[index/2]);
		int showing = index % 2;
		ObjectAnimator animation = ObjectAnimator.ofFloat(this, "alpha", showing, 1-showing);
		animation.setDuration(DELAY).addListener(animatorListener);
		if(showing == 1)
			animation.setStartDelay(STAND_DELAY);
		animation.start();
	}
	

	private AnimatorListener animatorListener = new AnimatorListener() {
		
		@Override
		public void onAnimationStart(Animator animation) {
		}
		
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		
		@Override
		public void onAnimationEnd(Animator animation) {
			index++;
			if(index/2 == mImgResIds.length) {
				if(mAnimatorListener != null)
					mAnimatorListener.onAnimationEnd(animation);
			}else {
				nextAnimation();
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animation) {
			if(mAnimatorListener != null)
				mAnimatorListener.onAnimationCancel(animation);
		}
	};
}
