package com.contralabs.inmap.views;

import com.contralabs.inmap.interfaces.LevelInformation;
import com.contralabs.inmap.interfaces.OnLevelSelectedListener;
import com.contralabs.inmap.salvadorshop.applicationdata.SalvadorShopApplicationDataFacade;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class LevelPickerView extends DropDownImageView {

	private OnLevelSelectedListener mOnLevelSelectedListener;

	public LevelPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LevelPickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LevelPickerView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOnItemSelectListener(onItemSelectListener);
		LevelInformation levelInfo = SalvadorShopApplicationDataFacade.getInstance(getContext()).getLevelInformation();
		int[] resId = new int[levelInfo.getLevelsCount()];
		for(int i = 0; i < resId.length; i++)
			resId[i] = levelInfo.getMenuIconResId(i);
		setImageResources(resId);
		setSelectedIndex(levelInfo.getInitLevel());
	}
	
	public void setOnLevelSelectedListener(OnLevelSelectedListener listener){
		mOnLevelSelectedListener = listener;
	}
	
	public void selectLevel(int level) {
		setSelectedIndex(level);
		if(mOnLevelSelectedListener != null)
			mOnLevelSelectedListener.onLevelSelected(level);
		
	}
	
	private OnItemSelectListener onItemSelectListener = new OnItemSelectListener() {
		
		@Override
		public void OnItemSelected(int index, View v) {
			if(mOnLevelSelectedListener != null)
				mOnLevelSelectedListener.onLevelSelected(index);
		}
	};
}
