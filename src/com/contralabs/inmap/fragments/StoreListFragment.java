package com.contralabs.inmap.fragments;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.contralabs.inmap.InMapApplication;
import com.contralabs.inmap.R;
import com.contralabs.inmap.activities.MainActivity;
import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.model.DbAdapter;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.model.StoreParameters;
import com.contralabs.inmap.salvadorshop.applicationdata.StoreCategory;
import com.google.analytics.tracking.android.EasyTracker;
import com.helpshift.Helpshift;

public class StoreListFragment extends Fragment {

	private static final String LIST_STATE = "ListState";

	private static final String STORE_PARAMETERS = "StoreParameters";

	private static final int MAX_DESCRIPTION_LENGHT = 100;
	
	protected View mRoot, mViewNoItemList, mViewHeader;
	protected ListView mStoreList;
	private ImageButton mBackToCategoryButton, mShowOnMapButton;
	protected Context mContext;
	protected TextView mTitleTextView;
	
	protected StoreListAdapter mStoreListAdapter;
	
	protected OnStoreSelectedListener mOnStoreSelectedListener;
	protected StoreListController mStoreListController;
	protected ApplicationDataFacade mApplicationDataFacade;

	private Bundle mSavedIntanceState;

	private Helpshift mHelpshift;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		FragmentActivity activity = getActivity();
		mContext = activity;
		mApplicationDataFacade = ((InMapApplication) activity.getApplication()).getApplicationDataFacade();
		mRoot = inflater.inflate(R.layout.fragment_liststore, null);
		mViewNoItemList = mRoot.findViewById(R.id.view_list_store_empty);
		mStoreList = (ListView) mRoot.findViewById(R.id.list_store);
		mStoreListAdapter = getListAdapter();
		mStoreList.setAdapter(mStoreListAdapter);
		mTitleTextView = (TextView) mRoot.findViewById(R.id.txt_storelist_title);
		mViewHeader = mRoot.findViewById(R.id.layout_storelist_header);
		
		configureButtons();
		
		if(savedInstanceState == null) {
			savedInstanceState = mSavedIntanceState;
			mSavedIntanceState = null;
		}
		
		if(savedInstanceState != null) {
			setStoreParameters((StoreParameters) savedInstanceState.getSerializable(STORE_PARAMETERS), savedInstanceState.getParcelable(LIST_STATE));
		}
		return mRoot;
	}
	
	protected StoreListAdapter getListAdapter() {
		return new StoreListAdapter(mContext);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STORE_PARAMETERS, mStoreListAdapter.getStoreParameters());
		outState.putParcelable(LIST_STATE, mStoreList.onSaveInstanceState());
		mSavedIntanceState = outState;
	}
	
	public void setStores(Store[] stores){
		mStoreListAdapter.setStores(stores);
	}
	
	public void setHeaderVisibility(int visibility){
		mViewHeader.setVisibility(visibility);
	}
	
	public void setAutoSortByTitle(boolean sort){
		mStoreListAdapter.mAutoSortByTitle = sort;
	}
	
	private void configureButtons() {
		mBackToCategoryButton = (ImageButton) mRoot.findViewById(R.id.btn_backtocategorys);
		View.OnClickListener backClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mStoreListController != null) {
					if(mStoreListAdapter.isSearch())
						mStoreListController.onSearchClicked();
					else
						mStoreListController.onBackToCategorysClicked();
				}
			}
		};
		mBackToCategoryButton.setOnClickListener(backClickListener);
		mRoot.findViewById(R.id.btn_backtocategorys_arrow).setOnClickListener(backClickListener);
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
				//ProblemasDialogFragment.sendSearchQueryNotFound(mContext, mStoreListAdapter.getSearchQuery());
				if(mHelpshift == null) mHelpshift = new Helpshift(mContext);
				mHelpshift.leaveBreadCrumb("Search query: " + mStoreListAdapter.getSearchQuery());
				mHelpshift.showReportIssue(getActivity());
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
	
	public void setStoreParameters(StoreParameters parameters) {
		setStoreParameters(parameters, null);
	}
	
	protected void setStoreParameters(StoreParameters parameters, Parcelable listState){
		if(parameters == null)
			return;
		mStoreList.smoothScrollToPosition(0);
		mStoreListAdapter.setStoreParameters(parameters, listState);
		if(mStoreListAdapter.isSearch()) {
			mBackToCategoryButton.setImageResource(R.drawable.bt_pesquisar);
			mTitleTextView.setText(mStoreListAdapter.getSearchQuery());
			mViewHeader.setBackgroundColor(0xff0b6897);
			mShowOnMapButton.setImageResource(R.drawable.bt_pin_loja_mapa);
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
	
	protected class StoreListAdapter extends BaseAdapter{
		private Context mContext;
		private Store[] mStores;
		private StoreParameters mParameters;
		private boolean mIsSearch;
		private Parcelable mListState;
		private boolean mAutoSortByTitle = true;
		
		public StoreListAdapter(Context context){
			mContext = context;
		}
		
		public StoreCategory getStoreCategory() {
			if(mIsSearch || mParameters == null)
				return null;
			return StoreCategory.getStoreCategoryById(Integer.valueOf(mParameters.getCategoryString().split(",")[0]));
		}

		public boolean isSearch() {
			return mIsSearch;
		}

		public void setStoreParameters(StoreParameters parameters, Parcelable listState){
			mListState = listState;
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
			if(mParameters == null)
				return "";
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
			if(convertView == null) {
				convertView = View.inflate(mContext, R.layout.listitem_store, null);
				convertView.setOnClickListener(onItemClickListener);
				convertView.findViewById(R.id.layout_store_showonmap).setOnClickListener(onItemShowOnMapClickListener);
			}
			
			Store store = (Store)getItem(position);
			TextView nameTextView = (TextView) convertView.findViewById(R.id.txt_store_name);
			nameTextView.setText(store.getName());// + " - " + store.getId()); // Convenience to show store id. Should not be used in live app.
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
			
			View showOnMap = convertView.findViewById(R.id.layout_store_showonmap);
			showOnMap.setBackgroundColor(storeCategory.getMenuColor());
			showOnMap.setTag(store);
			
			convertView.setTag(store);
			
			return convertView;
		}
		
		@Override
		public void notifyDataSetChanged() {
			if(mAutoSortByTitle)
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
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mRoot.findViewById(R.id.loading_stores).setVisibility(View.VISIBLE);
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
				setStores(result);
				if(result.length == 0)
					EasyTracker.getTracker().sendEvent("UserAction", "SearchWithoutResults", mParameters.getAnytext(), 0l);
			}
		}
		
		public StoreParameters getStoreParameters() {
			return mParameters;
		}

		private void setStores(Store[] result) {
			mStores = result;
			notifyDataSetChanged();
			configureEmptyView(result.length == 0);
			mRoot.findViewById(R.id.loading_stores).setVisibility(View.GONE);
			//mStoreList.setVisibility(View.VISIBLE);
			if(mListState != null)
				mStoreList.onRestoreInstanceState(mListState);
		}
	}

	public boolean isSearch() {
		return mStoreListAdapter.isSearch();
	}
	private void configureEmptyView(boolean empty) {
		mViewNoItemList.setVisibility(empty ? View.VISIBLE : View.INVISIBLE);
		if(!mStoreListAdapter.isSearch() && mStoreListAdapter.getStoreCategory() == StoreCategory.EVENTS) {
			((TextView) mViewNoItemList.findViewById(R.id.txt_list_store_empty)).setText(R.string.events_empty);
			mViewNoItemList.findViewById(R.id.btn_sugira_loja).setVisibility(View.GONE);
		}else {
			((TextView) mViewNoItemList.findViewById(R.id.txt_list_store_empty)).setText(R.string.message_noitems_list_store);
			mViewNoItemList.findViewById(R.id.btn_sugira_loja).setVisibility(View.VISIBLE);
		}
	}
	
	private OnClickListener onItemClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectStore((Store) v.getTag());
		}
	};
	
	private OnClickListener onItemShowOnMapClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(getActivity(), MainActivity.class);
			i.putExtra(MainActivity.SHOW_STORE_INMAP, (Serializable) v.getTag());
			startActivity(i);
		}
	};
}