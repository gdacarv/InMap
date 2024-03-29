package com.contralabs.inmap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.contralabs.inmap.interfaces.OnAnimationEnd;
import com.contralabs.inmap.salvadorshop.applicationdata.InfrastructureCategory;
import com.contralabs.inmap.views.AnimateFrameLayout;
import com.contralabs.inmap.views.OverScrollHorizontalScrollView;
import com.contralabs.inmap.views.OverScrollHorizontalScrollView.OnEdgeScrollListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.contralabs.inmap.R;

public class InfrastructureBarFragment extends Fragment implements OnGestureListener {

	private View mLayoutButtons, mLayoutControl, mViewMoreLeft, mViewMoreRight;
	private AnimateFrameLayout mRoot;
	private boolean isBarVisible = true, isPerformingAnimation = false;
	private Animation mAnimExpand, mAnimCollapse;
	private int mWidth = 0;
	private GestureDetector mGestureDetector;
	private ImageButton mInfraButtons[], mInfraControl;
	private int mInfraIds[];
	private OnInfrastructureCategoryChangedListener mOnInfrastructureChangeListener;
	private int mInfraSelected = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Context context = getActivity();
		mRoot = (AnimateFrameLayout) inflater.inflate(R.layout.fragment_infrastructure_bar, null);
		mLayoutButtons = mRoot.findViewById(R.id.layout_infra);
		mGestureDetector = new GestureDetector(context, this);

		mInfraControl = (ImageButton) mRoot.findViewById(R.id.btn_infra_control);

		mLayoutControl = mRoot.findViewById(R.id.layout_btn_infra);
		mLayoutControl.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});

		if(savedInstanceState != null){
			isBarVisible = savedInstanceState.getBoolean("isExpanded", isBarVisible);
			mInfraSelected = savedInstanceState.getInt("InfraSelected", mInfraSelected);
		}
		
		if(isBarVisible) {
			mLayoutButtons.setVisibility(View.VISIBLE);
			mInfraControl.setImageResource(R.drawable.bt_infraestrutura_out);
		}

		final InfrastructureCategory cats[] = InfrastructureCategory.values();
		mInfraButtons = new ImageButton[cats.length];
		mInfraIds = new int[cats.length];
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(v.isSelected()) {
					selectInfrastructure(0);
					v.setSelected(false);
				}else
					for(int i = 0; i < mInfraButtons.length; i++)
						if(mInfraButtons[i] == v){
							selectInfrastructure(mInfraIds[i]);
							v.setSelected(true);
						} else
							mInfraButtons[i].setSelected(false);
			}
		};
		LinearLayout layout = (LinearLayout) mRoot.findViewById(R.id.layout_infra_btns);
		for(int i = 0; i < cats.length; i++){
			mInfraButtons[i] = new ImageButton(context);
			mInfraButtons[i].setBackgroundResource(android.R.color.transparent);
			mInfraButtons[i].setImageResource(cats[i].getMenuIconResId());
			mInfraIds[i] = cats[i].getId();
			mInfraButtons[i].setOnClickListener(listener);
			if(mInfraIds[i] == mInfraSelected)
				mInfraButtons[i].setSelected(true);
			layout.addView(mInfraButtons[i]);
		}

		mRoot.setOnAnimationEnd(new OnAnimationEnd() {

			@Override
			public void onAnimationEnded() {
				if(!isBarVisible)
					mLayoutButtons.setVisibility(View.GONE);
			}
		});

		mViewMoreLeft = mRoot.findViewById(R.id.img_infra_more_left);
		mViewMoreRight = mRoot.findViewById(R.id.img_infra_more_right);
		((OverScrollHorizontalScrollView)mRoot.findViewById(R.id.scrollview_infra)).setOnEdgeScrollListener(onEdgeScrollListener);

		return mRoot;
	}

	protected void selectInfrastructure(int id) {
		mInfraSelected = id;
		if(mOnInfrastructureChangeListener != null)
			mOnInfrastructureChangeListener.onInfrastructureCategoryChanged(id);
		if(id != 0)
			EasyTracker.getTracker().sendEvent("UserAction", "InfrastructurePick", "Show Infra", (long) id);
	}

	public void setOnInfrastructureCategoryChangeListener(OnInfrastructureCategoryChangedListener listener){
		mOnInfrastructureChangeListener = listener;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("isExpanded", isBarVisible);
		outState.putInt("InfraSelected", getInfraSelected());
	}

	private int getInfraSelected() {
		return mInfraSelected;
	}

	protected void toggleBar() {
		if(!isPerformingAnimation){
			isPerformingAnimation = true;
			isBarVisible = !isBarVisible;
			if(isBarVisible){
				mLayoutButtons.setVisibility(View.VISIBLE);
				if(mAnimExpand == null){
					int width = mRoot.findViewById(R.id.layout_btn_infra).getWidth();
					int width2 = mWidth > 0 ? mWidth : width*9;
					mAnimExpand = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 1 - (float)width/(float)width2,
							Animation.RELATIVE_TO_SELF, 0f, 
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f);
					mAnimExpand.setDuration(300);
					mAnimExpand.setAnimationListener(new Animation.AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							isPerformingAnimation = false;
							mInfraControl.setImageResource(R.drawable.bt_infraestrutura_out);
						}
					});
				}
				mRoot.startAnimation(mAnimExpand);
			}else{
				if(mWidth == 0){
					mWidth = mRoot.getWidth();
					mAnimExpand = null;
				}
				if(mAnimCollapse == null){
					int width = mRoot.findViewById(R.id.layout_btn_infra).getWidth();
					mAnimCollapse = new TranslateAnimation( 
							Animation.RELATIVE_TO_SELF, 0f, 
							Animation.RELATIVE_TO_SELF, 1 - (float)width/(float)mWidth,
							Animation.RELATIVE_TO_SELF, 0f,
							Animation.RELATIVE_TO_SELF, 0f);
					mAnimCollapse.setDuration(300);
					mAnimCollapse.setAnimationListener(new Animation.AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							isPerformingAnimation = false;
							mInfraControl.setImageResource(R.drawable.bt_infraestrutura_in);
						}
					});
				}
				mRoot.startAnimation(mAnimCollapse);
			}
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float distancy = e2.getX(),
		distancyY = e2.getY();
		if(distancyY > -mLayoutControl.getHeight() && ((isBarVisible && distancy > mLayoutControl.getWidth() && distancy < mLayoutControl.getWidth()*6) || (!isBarVisible && distancy < 0  && distancy > mLayoutControl.getWidth()*-5f))){
			toggleBar();
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		toggleBar();
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	public void hideBar() {
		if(isBarVisible)
			toggleBar();
	}

	public interface OnInfrastructureCategoryChangedListener {
		void onInfrastructureCategoryChanged(int id);
	}

	public void clearSelection() {
		for(View view : mInfraButtons)
			view.setSelected(false);
	}
	
	private OnEdgeScrollListener onEdgeScrollListener = new OnEdgeScrollListener() {
		
		@Override
		public void onEdgeScrollChange(boolean leftEdge, boolean rightEdge) {
			mViewMoreLeft.setVisibility(leftEdge ? View.INVISIBLE : View.VISIBLE);
			mViewMoreRight.setVisibility(rightEdge ? View.INVISIBLE : View.VISIBLE);
		}
	};
}
