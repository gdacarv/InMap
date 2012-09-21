package com.inmap.fragments;

import com.inmap.R;
import com.inmap.StoreParameters;
import com.inmap.model.DbAdapter;
import com.inmap.model.Store;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class StoreListFragment extends Fragment {

	private View mRoot;
	private ListView mStoreList;
	private Button mBackToCategoryButton, mShowOnMapButton;
	private Context mContext;
	
	private StoreListAdapter mStoreListAdapter;
	
	private OnStoreSelectedListener mOnStoreSelectedListener;
	private StoreListController mStoreListController;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mRoot = inflater.inflate(R.layout.fragment_liststore, null);
		mStoreList = (ListView) mRoot.findViewById(R.id.list_store);
		mStoreListAdapter = new StoreListAdapter(mContext);
		mStoreList.setAdapter(mStoreListAdapter);

		mStoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectStore((Store) mStoreListAdapter.getItem(position));
			}
		});
		
		configureButtons();
		return mRoot;
	}
	
	private void configureButtons() {
		mBackToCategoryButton = (Button) mRoot.findViewById(R.id.btn_backtocategorys);
		mBackToCategoryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStoreListController != null)
					mStoreListController.onBackToCategorysClicked();
			}
		});
		mShowOnMapButton = (Button) mRoot.findViewById(R.id.btn_showonmap);
		mShowOnMapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStoreListController != null)
					mStoreListController.onShowOnMapClicked(mStoreListAdapter.getStores());
			}
		});
	}

	public void setOnStoreSelectedListener(OnStoreSelectedListener listener){
		mOnStoreSelectedListener = listener;
	}
	
	public void setStoreListController(StoreListController controller){
		mStoreListController = controller;
	}
	
	private void selectStore(Store store){
		if(mOnStoreSelectedListener != null)
			mOnStoreSelectedListener.onStoreSelected(store);
	}
	
	public void setStoreParameters(StoreParameters parameters){
		mStoreListAdapter.setStoreParameters(parameters);
	}
	
	public interface OnStoreSelectedListener {
		void onStoreSelected(Store store);
	}
	
	public interface StoreListController {
		void onBackToCategorysClicked();
		void onShowOnMapClicked(Store[] stores);
	}
	
	private class StoreListAdapter extends BaseAdapter{
		private Context mContext;
		private Store[] mStores;
		private StoreParameters mParameters;
		
		public StoreListAdapter(Context context){
			mContext = context;
		}
		
		public void setStoreParameters(StoreParameters parameters){
			mParameters = parameters;
			refreshStores();
		}

		private void refreshStores() {
			new getStoresAsyncTask().execute();			
		}
		
		private Store[] getStores(){
			return mStores;
		}

		@Override
		public int getCount() {
			return mStores == null ? 0 : mStores.length;
		}

		@Override
		public Object getItem(int position) {
			return mStores == null ? null : mStores[position];
		}

		@Override
		public long getItemId(int position) {
			return mStores == null ? -1 : mStores[position].getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(mContext, android.R.layout.simple_list_item_2, null);
			
			((TextView) convertView.findViewById(android.R.id.text1))
				.setText(((Store)getItem(position)).getName());
			((TextView) convertView.findViewById(android.R.id.text2))
				.setText(((Store)getItem(position)).getDescription());
			
			return convertView;
		}
		
		private class getStoresAsyncTask extends AsyncTask<Void, Void, Void>{
			
			private View loadingView;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loadingView = mRoot.findViewById(R.id.loading_stores);
				loadingView.setVisibility(View.VISIBLE);
				//mStoreList.setVisibility(View.GONE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				DbAdapter dbAdapter = DbAdapter.getInstance(mContext).open();
				mStores = dbAdapter.getStores(mParameters);
				dbAdapter.close();
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				notifyDataSetChanged();
				loadingView.setVisibility(View.GONE);
				//mStoreList.setVisibility(View.VISIBLE);
			}
		}
		
	}
}