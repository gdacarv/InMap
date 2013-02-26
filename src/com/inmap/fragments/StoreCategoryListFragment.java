package com.inmap.fragments;

import com.inmap.salvadorshop.R;
import com.inmap.salvadorshop.applicationdata.StoreCategory;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class StoreCategoryListFragment extends Fragment {

	private View mRoot;
	private ListView mStoreCategoryList;
	private Context mContext;
	
	private OnStoreCategoryChangedListener mOnStoreCategoryChangedListener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mRoot = inflater.inflate(R.layout.fragment_listcategory, null);
		mStoreCategoryList = (ListView) mRoot.findViewById(R.id.list_category);
		mStoreCategoryList.setAdapter(new StoreCategoryListAdapter(mContext));

		mStoreCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectStoreCategory((int) id);
			}
		});
		
		/*DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		mStoreCategoryList.setLayoutParams(new LinearLayout.LayoutParams((int) (width*0.8), LinearLayout.LayoutParams.FILL_PARENT));*/

		return mRoot;
	}
	
	public void setOnStoreCategoryChangedListener(OnStoreCategoryChangedListener listener){
		mOnStoreCategoryChangedListener = listener;
	}
	
	private void selectStoreCategory(int id){
		if(mOnStoreCategoryChangedListener != null)
			mOnStoreCategoryChangedListener.onStoreCategoryChanged(id);
	}
	
	public interface OnStoreCategoryChangedListener {
		public void onStoreCategoryChanged(int id);
	}
	
	private class StoreCategoryListAdapter extends BaseAdapter{
		private Context mContext;
		private StoreCategory[] mStoreCategorys;
		
		public StoreCategoryListAdapter(Context context){
			mContext = context;
			mStoreCategorys = StoreCategory.values();
		}

		@Override
		public int getCount() {
			return mStoreCategorys.length;
		}

		@Override
		public Object getItem(int position) {
			return mStoreCategorys[position];
		}

		@Override
		public long getItemId(int position) {
			return mStoreCategorys[position].getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(mContext, android.R.layout.simple_list_item_1, null);
			
			((TextView) convertView.findViewById(android.R.id.text1))
				.setText(((StoreCategory)getItem(position)).getTitleRes());
			
			return convertView;
		}
		
	}
}
