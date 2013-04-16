package com.inmap.fragments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inmap.InMapApplication;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.model.DbAdapter;
import com.inmap.model.Store;
import com.inmap.model.StoreParameters;
import com.inmap.salvadorshop.R;
import com.inmap.salvadorshop.applicationdata.StoreCategory;

public class StoreListFragment extends Fragment {

	private static final int MAX_DESCRIPTION_LENGHT = 140;
	
	private View mRoot, mViewNoItemList, mViewHeader;
	private ListView mStoreList;
	private ImageButton mBackToCategoryButton, mShowOnMapButton;
	private Context mContext;
	private TextView mTitleTextView;
	
	private StoreListAdapter mStoreListAdapter;
	
	private OnStoreSelectedListener mOnStoreSelectedListener;
	private StoreListController mStoreListController;
	private ApplicationDataFacade mApplicationDataFacade;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentActivity activity = getActivity();
		mContext = activity;
		mApplicationDataFacade = ((InMapApplication) activity.getApplication()).getApplicationDataFacade();
		mRoot = inflater.inflate(R.layout.fragment_liststore, null);
		mViewNoItemList = mRoot.findViewById(R.id.view_list_store_empty);
		mStoreList = (ListView) mRoot.findViewById(R.id.list_store);
		mStoreListAdapter = new StoreListAdapter(mContext);
		mStoreList.setAdapter(mStoreListAdapter);

		mStoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectStore((Store) mStoreListAdapter.getItem(position));
			}
		});
		mTitleTextView = (TextView) mRoot.findViewById(R.id.txt_storelist_title);
		mViewHeader = mRoot.findViewById(R.id.layout_storelist_header);
		
		configureButtons();
		return mRoot;
	}
	
	private void configureButtons() {
		mBackToCategoryButton = (ImageButton) mRoot.findViewById(R.id.btn_backtocategorys);
		mBackToCategoryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStoreListController != null) {
					if(mStoreListAdapter.isSearch())
						mStoreListController.onSearchClicked();
					else
						mStoreListController.onBackToCategorysClicked();
				}
			}
		});
		mShowOnMapButton = (ImageButton) mRoot.findViewById(R.id.btn_showonmap);
		mShowOnMapButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStoreListController != null)
					mStoreListController.onShowOnMapClicked(mStoreListAdapter.getStores());
			}
		});
		
		mRoot.findViewById(R.id.btn_sugira_loja).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ProblemasDialogFragment.sendSearchQueryNotFound(mContext, mStoreListAdapter.getSearchQuery());
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
		mStoreList.smoothScrollToPosition(0);
		mStoreListAdapter.setStoreParameters(parameters);
		if(mStoreListAdapter.isSearch()) {
			mBackToCategoryButton.setImageResource(R.drawable.bt_pesquisar);
			mTitleTextView.setText(mStoreListAdapter.getSearchQuery());
			mViewHeader.setBackgroundColor(0xff0b6897);
			mShowOnMapButton.setImageResource(R.drawable.pin_cat_pesquisa_all);
		}else {
			StoreCategory category = mStoreListAdapter.getStoreCategory();
			mBackToCategoryButton.setImageResource(category.getMenuIconResId());
			mTitleTextView.setText(category.getTitleRes());
			mViewHeader.setBackgroundColor(category.getMenuColor());
			mShowOnMapButton.setImageResource(category.getBtAllIconResId());
		}
	}
	
	public interface OnStoreSelectedListener {
		void onStoreSelected(Store store);
	}
	
	public interface StoreListController {
		void onBackToCategorysClicked();
		void onShowOnMapClicked(Store[] stores);
		void onSearchClicked();
	}
	
	private class StoreListAdapter extends BaseAdapter{
		private Context mContext;
		private Store[] mStores;
		private StoreParameters mParameters;
		private boolean mIsSearch;
		
		public StoreListAdapter(Context context){
			mContext = context;
		}
		
		public StoreCategory getStoreCategory() {
			if(mIsSearch)
				return null;
			return StoreCategory.getStoreCategoryById(Integer.valueOf(mParameters.getCategoryString().split(",")[0]));
		}

		public boolean isSearch() {
			return mIsSearch;
		}

		public void setStoreParameters(StoreParameters parameters){
			mParameters = parameters;
			String categoryId = mParameters.getCategoryString().split(",")[0];
			if(categoryId.equals("")) { // Is Search
				mIsSearch = true;
			}else { // Is category
				mIsSearch = false;
			}
			refreshStores();
		}
		
		public String getSearchQuery() {
			return mParameters.getAnytext();
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
				convertView = View.inflate(mContext, R.layout.listitem_store, null);
			
			Store store = (Store)getItem(position);
			TextView nameTextView = (TextView) convertView.findViewById(R.id.txt_store_name);
			nameTextView.setText(store.getName());
			StoreCategory storeCategory = store.getCategory();
			ImageView imgCategory = (ImageView) convertView.findViewById(R.id.img_store_category);
			View layout = convertView.findViewById(R.id.layout_listitem_store_category);
			if(isSearch()) {
				imgCategory.setImageResource(storeCategory.getMenuIconResId());
				layout.setBackgroundColor(storeCategory.getMenuColor());
				layout.setVisibility(View.VISIBLE);
			} else
				layout.setVisibility(View.GONE);
			nameTextView.setTextColor(storeCategory.getMenuColor());
			
			((TextView) convertView.findViewById(R.id.txt_store_description))
				.setText(formatDescription(store.getDescription()));
			
			((TextView) convertView.findViewById(R.id.txt_store_level))
				.setText(mApplicationDataFacade.getLevelInformation().getTitle(store.getLevel()));
			
			TextView extraTextView = (TextView) convertView.findViewById(R.id.txt_store_extra);
			String extra = store.getExtra();
			if(extra != null && extra.length() > 0) {
				try {
					Class<? extends ExtraFragment> cls = (Class<? extends ExtraFragment>) Class.forName(extra);
					Method methodDescription = cls.getMethod("getDescription", Resources.class);
					Method methodIcon = cls.getMethod("getIconResIdStatic");
					extraTextView.setText(methodDescription.invoke(null, getResources()).toString());
					extraTextView.setCompoundDrawablesWithIntrinsicBounds((Integer) methodIcon.invoke(null), 0, 0, 0);
					extraTextView.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					extraTextView.setVisibility(View.GONE);
					e.printStackTrace();
				} 
			} else
				extraTextView.setVisibility(View.GONE);
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			Arrays.sort(mStores, comparator);
			super.notifyDataSetChanged();
		}
		
		private Comparator<Store> comparator = new Comparator<Store>() {

			@Override
			public int compare(Store lhs, Store rhs) {
				return lhs.getTitle().compareTo(rhs.getTitle());
			}
		};
		
		private CharSequence formatDescription(String description) {
			if(description == null || description.length() <= MAX_DESCRIPTION_LENGHT)
				return description;
			int index = description.indexOf(' ', MAX_DESCRIPTION_LENGHT);
			if(index < MAX_DESCRIPTION_LENGHT)
				return description;
			return Html.fromHtml(description.substring(0, index) + "... <font color='red'><i>(Leia Mais)</i></font>");
		}

		private class getStoresAsyncTask extends AsyncTask<Void, Void, Store[]>{
			
			private View loadingView;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				loadingView = mRoot.findViewById(R.id.loading_stores);
				loadingView.setVisibility(View.VISIBLE);
				//mStoreList.setVisibility(View.GONE);
			}

			@Override
			protected Store[] doInBackground(Void... params) {
				DbAdapter dbAdapter = DbAdapter.getInstance(mContext).open();
				Store[] stores = dbAdapter.getStores(mParameters);
				dbAdapter.close();
				return stores;
			}
			
			@Override
			protected void onPostExecute(Store[] result) {
				super.onPostExecute(result);
				mStores = result;
				notifyDataSetChanged();
				loadingView.setVisibility(View.GONE);
				mViewNoItemList.setVisibility(result.length == 0 ? View.VISIBLE : View.INVISIBLE);
				//mStoreList.setVisibility(View.VISIBLE);
			}
		}
	}

	public boolean isSearch() {
		return mStoreListAdapter.isSearch();
	}
}