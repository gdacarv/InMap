package com.contralabs.inmap.fragments;

import java.security.InvalidParameterException;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.contralabs.inmap.R;
import com.contralabs.inmap.model.Store;

public class RecommendationStoreListFragment extends StoreListFragment {
	
	private Pair<Store, Double>[] mStoresWithScore;

	@Override
	public void setStores(Store[] stores) {
		throw new InvalidParameterException("You should pass a Map<Store, Double> representing the similarity score for each store.");
	}

	public void setStores(Pair<Store, Double>[] storesWithScore){
		Store[] stores = new Store[storesWithScore.length];
		for(int i = 0; i < stores.length; i++)
			stores[i] = storesWithScore[i].first;
		super.setStores(stores);
		mStoresWithScore = storesWithScore;
	}
	
	@Override
	protected StoreListAdapter getListAdapter() {
		return new RecommendationStoreListAdapter(mContext);
	}
	
	protected class RecommendationStoreListAdapter extends StoreListAdapter {

		public RecommendationStoreListAdapter(Context context) {
			super(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			TextView description = (TextView) view.findViewById(R.id.txt_store_description);
			description.setText(String.format(mContext.getString(R.string.recomendacao_score), mStoresWithScore[position].second.toString()) + "\n" + description.getText());
			return view;
		}
		
	}
}
