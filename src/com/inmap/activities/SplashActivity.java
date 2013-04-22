package com.inmap.activities;

import com.inmap.salvadorshop.R;
import com.inmap.views.SplashImageView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout.LayoutParams;

public class SplashActivity extends Activity implements AnimatorListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		FrameLayout layout = new FrameLayout(this);
		//layout.setBackgroundResource(R.drawable.img_background_levels);
		SplashImageView splash = new SplashImageView(this);
		splash.setScaleType(ScaleType.CENTER);
		layout.addView(splash);
		setContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		splash.start(this, R.drawable.img_splashscreen_01, R.drawable.img_splashscreen_02);
	}

	@Override
	public void onAnimationStart(Animator animation) {
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}

	@Override
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}

}
