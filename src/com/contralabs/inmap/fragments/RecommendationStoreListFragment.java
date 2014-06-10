package com.contralabs.inmap.fragments;

import java.security.InvalidParameterException;
import java.util.Arrays;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.contralabs.inmap.R;
import com.contralabs.inmap.activities.RecommendationActivity;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.recommendation.Evaluation;
import com.contralabs.inmap.recommendation.Evaluation.Model;
import com.contralabs.inmap.recommendation.SimilarityBuilderService;
import com.contralabs.inmap.recommendation.SimilarityBuilderService.SimilarityAlgorithm;

public class RecommendationStoreListFragment extends StoreListFragment {
	
	private Pair<Store, Double>[] mStoresWithScore;

	@Override
	public void setStores(Store[] stores) {
		throw new InvalidParameterException("You should pass a Pair<Store, Double>[] representing the similarity score for each store.");
	}

	public void setStores(Pair<Store, Double>[] storesWithScore){
		Store[] stores = new Store[storesWithScore.length];
		for(int i = 0; i < stores.length; i++)
			stores[i] = storesWithScore[i].first;
		super.setStores(stores);
		mStoresWithScore = storesWithScore;
		
		analyzeEvaluation();
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
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void analyzeEvaluation() {
		long[] shouldRecommend = Evaluation.getEvaluationShouldRecommendStores();
		if(shouldRecommend == null)
			return;
		if(shouldRecommend.length > RecommendationActivity.RECOMMEND_QUANTITY){
			shouldRecommend = Arrays.copyOf(shouldRecommend, RecommendationActivity.RECOMMEND_QUANTITY);
		}
		Arrays.sort(shouldRecommend);
		int matchedRecommendation = 0;
		for(Pair<Store, Double> storeAndScore : mStoresWithScore){
			if(Arrays.binarySearch(shouldRecommend, storeAndScore.first.getId()) >= 0)
				matchedRecommendation++;
		}
		float precision = ((float)matchedRecommendation)/((float)mStoresWithScore.length);
		float recall = ((float)matchedRecommendation)/((float)shouldRecommend.length);
		float fmeasure = (2*precision*recall)/(precision+recall);
		String message = "Recommender Evaluation Algorithm: " + SimilarityBuilderService.DEFAULT_ALGORITHM + " and model " + Evaluation.getEvalatuationModel().name + " n = " + RecommendationActivity.RECOMMEND_QUANTITY + " precision = " + precision + " recall = " + recall + " f-measure = " + fmeasure;
		Log.d("Evaluation", message);
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
		
		if(RecommendationActivity.RECOMMEND_QUANTITY == 4){
			RecommendationActivity.RECOMMEND_QUANTITY = 12;
			switch(SimilarityBuilderService.DEFAULT_ALGORITHM){
			case STORE_BASED:
				SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.SIMPLE;
				break;
			case SIMPLE:
				SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.RANDOM;
				break;
			case RANDOM:
				SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.STORE_BASED;
				switch (Evaluation.USE_EVALUATION_MODEL) {
				case ESPORTISTA:
					Evaluation.USE_EVALUATION_MODEL = Model.ESPORTISTA_NATUREBA;
					break;
				case ESPORTISTA_NATUREBA:
					Evaluation.USE_EVALUATION_MODEL = Model.PATRICINHA;
					break;
				case PATRICINHA:
					return;
				}
				break;
			}
		} else
			RecommendationActivity.RECOMMEND_QUANTITY -= 4;
		mContext.startService(new Intent(mContext, SimilarityBuilderService.class));
	}
}
