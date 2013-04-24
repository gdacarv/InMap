package com.contralabs.inmap.fragments;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.salvadorshop.applicationdata.StoreCategory;
import com.google.analytics.tracking.android.EasyTracker;
import com.contralabs.inmap.R;

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
		EasyTracker.getTracker().sendView(String.format(getString(R.string.view_category), id));
	}
	
	public interface OnStoreCategoryChangedListener {
		public void onStoreCategoryChanged(int id);
	}
	
	private class StoreCategoryListAdapter extends BaseAdapter{
		private Context mContext;
		private StoreCategory[] mStoreCategorys;
		private int[] mStoresCount;
		private Comparator<StoreCategory> comparator = new Comparator<StoreCategory>() {
			
			@Override
			public int compare(StoreCategory lhs, StoreCategory rhs) {
				//return mStoresCount[rhs.getId()-1] - mStoresCount[lhs.getId()-1];
				return getString(lhs.getTitleRes()).compareTo(getString(rhs.getTitleRes()));
			}
		};
		
		public StoreCategoryListAdapter(Context context){
			mContext = context;
			mStoreCategorys = StoreCategory.values();
			mStoresCount = new int[mStoreCategorys.length];
			Arrays.fill(mStoresCount, -1);
			new getStoresCountAsyncTask(mStoresCount, this).execute();
		}
		
		@Override
		public void notifyDataSetChanged() {
			Arrays.sort(mStoreCategorys, comparator );
			super.notifyDataSetChanged();
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
				convertView = View.inflate(mContext, R.layout.listitem_category, null);
			
			TextView textView = (TextView) convertView.findViewById(R.id.txt_list_category_name);
			StoreCategory storeCategory = (StoreCategory)getItem(position);
			textView.setText(storeCategory.getTitleRes());
			textView.setCompoundDrawablesWithIntrinsicBounds(storeCategory.getMenuIconResId(), 0, 0, 0);
			convertView.setBackgroundColor(storeCategory.getMenuColor());
			int count = mStoresCount[storeCategory.getId()-1];
			((TextView) convertView.findViewById(R.id.txt_list_category_qt)).setText(count == -1 ? "" : String.valueOf(count));
			
			return convertView;
		}
		
		private class getStoresCountAsyncTask extends AsyncTask<Void, Void, Void>{
			
			private int[] storesCount;
			private BaseAdapter adapter;

			public getStoresCountAsyncTask(int[] storesCount, BaseAdapter adapter) {
				this.storesCount = storesCount;
				this.adapter = adapter;
			}

			@Override
			protected Void doInBackground(Void... params) {
				DbAdapter dbAdapter = DbAdapter.getInstance(mContext).open();
				dbAdapter.getStoresCountByCategory(storesCount);
				dbAdapter.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				adapter.notifyDataSetChanged();
			}
		}
	}
}
