package com.contralabs.inmap.fragments;

import java.security.InvalidParameterException;
import java.util.Arrays;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.contralabs.inmap.R;
import com.contralabs.inmap.activities.RecommendationActivity;
import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.recommendation.Evaluation;
import com.contralabs.inmap.recommendation.PerformanceEvaluation;
import com.contralabs.inmap.recommendation.SimilarityBuilderService;
import com.contralabs.inmap.recommendation.SimilarityBuilderService.SimilarityAlgorithm;

public class RecommendationStoreListFragment extends StoreListFragment {
	
	private Pair<Store, Double>[] mStoresWithScore;
	
	private DescriptiveStatistics mPrecision = new DescriptiveStatistics(), mRecall = new DescriptiveStatistics(), mFmeasure = new DescriptiveStatistics();
	
	@Override
	public void onResume() {
		super.onResume();
		mContext.startService(new Intent(mContext, SimilarityBuilderService.class));
	}

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
			TextView name = (TextView) view.findViewById(R.id.txt_store_name);
			name.setText(name.getText() + " (" + mStoresWithScore[position].first.getId() + ")");
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
		precision = Float.isNaN(precision) ? 0 : precision;
		float recall = ((float)matchedRecommendation)/((float)shouldRecommend.length);
		recall = Float.isNaN(recall) ? 0 : recall;
		float fmeasure = (2*precision*recall)/(precision+recall);
		fmeasure = Float.isNaN(fmeasure) ? 0 : fmeasure;
		//String message = "Recommender Evaluation Algorithm: " + SimilarityBuilderService.DEFAULT_ALGORITHM + " and model " + Evaluation.getEvaluationModel().name + " n = " + RecommendationActivity.RECOMMEND_QUANTITY + " precision = " + precision + " recall = " + recall + " f-measure = " + fmeasure;
		//Log.d("Evaluation", message);
		//Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
		mPrecision.addValue(precision);
		mRecall.addValue(recall);
		mFmeasure.addValue(fmeasure);
		
		if(Evaluation.mIndex < Evaluation.getEvaluationDataSize()-1)
			Evaluation.mIndex++;
		else {
			Log.d("Evaluation", "RESULT: Recommender Evaluation Algorithm: " + SimilarityBuilderService.DEFAULT_ALGORITHM + " n = " + RecommendationActivity.RECOMMEND_QUANTITY + " precision = " + mPrecision.getMean() + " (" + mPrecision.getStandardDeviation() + ") recall = " + mRecall.getMean() + " (" + mRecall.getStandardDeviation() + ") f-measure = " + mFmeasure.getMean() + " (" + mFmeasure.getStandardDeviation() + ")");
			Evaluation.mIndex = 0;
			mPrecision.clear();
			mRecall.clear();
			mFmeasure.clear();
			RecommendationActivity.RECOMMEND_QUANTITY -= 4;
			if(RecommendationActivity.RECOMMEND_QUANTITY == 0){
				PerformanceEvaluation.instance.logResults();
				PerformanceEvaluation.instance.clear();
				RecommendationActivity.RECOMMEND_QUANTITY = 12;
				switch(SimilarityBuilderService.DEFAULT_ALGORITHM){
				case STORE_BASED:
					SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.TAG_BASED;
					break;
				case TAG_BASED:
					SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.SIMPLE;
					break;
				case SIMPLE:
					SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.RANDOM;
					break;
				case RANDOM:
					if(Integer.parseInt(PerformanceEvaluation.instance.getQuantityStore()) <= 400){
						SimilarityBuilderService.DEFAULT_ALGORITHM = SimilarityAlgorithm.STORE_BASED;
						PerformanceEvaluation.instance.increaseStores();
					}else
						return;
				}
			}
		}
		mContext.startService(new Intent(mContext, SimilarityBuilderService.class));
	}
}
