package com.inmap.fragments;

import com.inmap.R;
import com.inmap.applicationdata.SalvadorShopApplicationDataFacade;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.interfaces.LevelInformation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class LevelPickerFragment extends Fragment {
	
	private OnLevelSelectedListener mOnLevelSelectedListener;
	private LevelInformation mLevelInformation;
	private Button[] mLevelButtons;
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
						mOnLevelSelectedListener.onLevelSelected(i, mLevelInformation.getMapResource(i));
						break;
					}
			}
		};
		LinearLayout root = (LinearLayout) inflater.inflate(R.layout.fragment_levelpicker, null);
		LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1);
		mLevelButtons = new Button[mLevelInformation.getLevelsCount()];
		for(int i = 0; i < mLevelButtons.length; i++){
			mLevelButtons[i] = new Button(mContext);
			mLevelButtons[i].setLayoutParams(layoutParamsButton);
			mLevelButtons[i].setText(mLevelInformation.getTitle(i));
			mLevelButtons[i].setOnClickListener(listener);
			root.addView(mLevelButtons[i]);
		}
			
		return root;
	}
	
	public void setOnLevelSelectedListener(OnLevelSelectedListener listener){
		mOnLevelSelectedListener = listener;
	}
	
	public interface OnLevelSelectedListener {
		public void onLevelSelected(int level, int mapResource);
	}
}
