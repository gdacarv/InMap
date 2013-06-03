package com.contralabs.inmap.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

public class DropDownImageView extends ImageView {

	private int[] mImages = new int[0];
	private int mIndexSelected = 0;
	private OnClickListener mOnClickListener;
	private PopupWindow mPopupWindow;
	private OnItemSelectListener mOnItemSelectListener;
	private BaseAdapter mListAdapter;

	public DropDownImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DropDownImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DropDownImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		super.setOnClickListener(onClickListener);
	}

	private View createContentView() {
		ListView view = new ListView(getContext());
		view.setAdapter(mListAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null)
					convertView = new ImageView(getContext());
				((ImageView) convertView).setImageResource(mImages[position]);
				convertView.setSelected(position == getSelectedIndex());
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return mImages[position];
			}

			@Override
			public Object getItem(int position) {
				return mImages[position];
			}

			@Override
			public int getCount() {
				return mImages.length;
			}
		});
		view.setOnItemClickListener(onItemClickListener);
		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			toggleDropDown();
			if(mOnClickListener != null)
				mOnClickListener.onClick(v);
		}
	};

	public void setImageResources(int[] images) {
		if(images == null)
			images = new int[0];
		mImages = images;
		if(mIndexSelected >= mImages.length)
			mIndexSelected = mImages.length-1;
		refresh();
	}

	private void refresh() {
		if(mListAdapter != null)
			mListAdapter.notifyDataSetChanged();
		refreshImageView();
	}

	private void refreshImageView() {
		setImageResource(mImages[mIndexSelected]);
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		mOnClickListener = l;
	}

	public void toggleDropDown() {
		PopupWindow popupWindow = getPopupWindow();
		if(!popupWindow.isShowing())
			popupWindow.showAsDropDown(this);
		else
			popupWindow.dismiss();
	}

	private PopupWindow getPopupWindow() {
		if(mPopupWindow == null) {
			
			View contentView = createContentView();
			mPopupWindow = new PopupWindow(contentView, getWidth(), LayoutParams.WRAP_CONTENT, true);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		}
		return mPopupWindow;
	}

	public interface OnItemSelectListener {
		void OnItemSelected(int index, View v);
	}

	public void setOnItemSelectListener(OnItemSelectListener listener) {
		mOnItemSelectListener = listener;
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			setSelectedIndex(position);
			if(mOnItemSelectListener != null)
				mOnItemSelectListener.OnItemSelected(mIndexSelected, view);
			getPopupWindow().dismiss();
		}
	};

	public int getSelectedIndex() {
		return mIndexSelected;
	}

	public void setSelectedIndex(int index) {
		mIndexSelected = index;
		refreshImageView();
	}

	@Override
	public Parcelable onSaveInstanceState() {

		Bundle bundle = new Bundle();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putInt("indexSelected", mIndexSelected);

		return bundle;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {

		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			setSelectedIndex(bundle.getInt("indexSelected"));
			super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
		}else
			super.onRestoreInstanceState(state);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		if(mPopupWindow.isShowing())
			mPopupWindow.dismiss();
		super.onDetachedFromWindow();
	}
}
