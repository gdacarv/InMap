package com.inmap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.LevelInformation;
import com.inmap.salvadorshop.R;
import com.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;

public class LevelPickerFragment extends Fragment {
	
	private OnLevelSelectedListener mOnLevelSelectedListener;
	private LevelInformation mLevelInformation;
	private ImageButton[] mLevelButtons;
	private Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		ApplicationDataFacade dataFacade = SalvadorShopApplicationDataFacade.getInstance(mContext);
		mLevelInformation = dataFacade.getLevelInformation();
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(int i = 0; i < mLevelButtons.length; i++)
					if(mLevelButtons[i] == v){
						mOnLevelSelectedListener.onLevelSelected(i);
						v.setSelected(true);
					} else
						mLevelButtons[i].setSelected(false);
			}
		};
		LinearLayout root = (LinearLayout) inflater.inflate(R.layout.fragment_levelpicker, null);
		LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1);
		mLevelButtons = new ImageButton[mLevelInformation.getLevelsCount()];
		int initLevel = mLevelInformation.getInitLevel();
		for(int i = mLevelButtons.length-1; i >= 0 ; i--){
			mLevelButtons[i] = new ImageButton(mContext);
			mLevelButtons[i].setLayoutParams(layoutParamsButton);
			mLevelButtons[i].setImageResource(mLevelInformation.getMenuIconResId(i));
			mLevelButtons[i].setOnClickListener(listener);
			mLevelButtons[i].setBackgroundResource(0);
			mLevelButtons[i].setScaleType(ScaleType.FIT_CENTER);
			mLevelButtons[i].setPadding(0, 0, 0, 0);
			if(initLevel == i)
				mLevelButtons[i].setSelected(true);
			root.addView(mLevelButtons[i]);
		}
			
		return root;
	}
	
	public void setOnLevelSelectedListener(OnLevelSelectedListener listener){
		mOnLevelSelectedListener = listener;
	}
	
	public interface OnLevelSelectedListener {
		public void onLevelSelected(int level);
	}
}
