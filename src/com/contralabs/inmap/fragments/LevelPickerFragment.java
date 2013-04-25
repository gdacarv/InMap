package com.contralabs.inmap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.interfaces.LevelInformation;
import com.contralabs.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;
import com.google.analytics.tracking.android.EasyTracker;
import com.contralabs.inmap.R;

public class LevelPickerFragment extends Fragment {
	
	private static final String LEVEL_SELECTED = "LevelSelected";
	private OnLevelSelectedListener mOnLevelSelectedListener;
	private LevelInformation mLevelInformation;
	private ImageButton[] mLevelButtons;
	private Context mContext;
	private int mLevelSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		ApplicationDataFacade dataFacade = SalvadorShopApplicationDataFacade.getInstance(getActivity());
		mLevelInformation = dataFacade.getLevelInformation();
		if(savedInstanceState == null)
			mLevelSelected = mLevelInformation.getInitLevel();
		else
			mLevelSelected = savedInstanceState.getInt(LEVEL_SELECTED, mLevelInformation.getInitLevel());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectLevel(-1, v);
			}
		};
		LinearLayout root = (LinearLayout) inflater.inflate(R.layout.fragment_levelpicker, null);
		LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1);
		mLevelButtons = new ImageButton[mLevelInformation.getLevelsCount()];
		for(int i = mLevelButtons.length-1; i >= 0 ; i--){
			mLevelButtons[i] = new ImageButton(mContext);
			mLevelButtons[i].setLayoutParams(layoutParamsButton);
			mLevelButtons[i].setImageResource(mLevelInformation.getMenuIconResId(i));
			mLevelButtons[i].setOnClickListener(listener);
			mLevelButtons[i].setBackgroundResource(0);
			mLevelButtons[i].setScaleType(ScaleType.FIT_CENTER);
			mLevelButtons[i].setPadding(0, 0, 0, 0);
			if(mLevelSelected == i)
				mLevelButtons[i].setSelected(true);
			root.addView(mLevelButtons[i]);
		}
			
		return root;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(LEVEL_SELECTED, mLevelSelected);
	}
	
	public void setOnLevelSelectedListener(OnLevelSelectedListener listener){
		mOnLevelSelectedListener = listener;
	}
	
	public interface OnLevelSelectedListener {
		public void onLevelSelected(int level);
	}

	public void selectLevel(int level) {
		selectLevel(level, null);
	}

	private void selectLevel(int level, View v) {
		for(int i = 0; i < mLevelButtons.length; i++)
			if(i == level || mLevelButtons[i] == v){
				mOnLevelSelectedListener.onLevelSelected(i);
				mLevelButtons[i].setSelected(true);
				EasyTracker.getInstance().setContext(mContext.getApplicationContext());
				EasyTracker.getTracker().sendView(String.format(getString(R.string.view_level), mLevelInformation.getTitle(i)));
				mLevelSelected = i;
			} else
				mLevelButtons[i].setSelected(false);
	}
}
