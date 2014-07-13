package com.contralabs.inmap.recommendation;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
import android.util.Log;

import com.contralabs.inmap.model.DatabaseHelper;
import com.contralabs.inmap.model.DbAdapter;

public class SimilarityBuilderService extends IntentService {

	public static final String ACTION_SIMILIRARITY_BUILD_FINISHED = "SIMILIRARITY_BUILD_FINISHED";
	private static final double SCORE_MINIMUM = 0.15d;
	private static final String EXTRA_USER = "extraUser";
	private static final String EXTRA_ALGORITHM = "extraUser";
	public static SimilarityAlgorithm DEFAULT_ALGORITHM = SimilarityAlgorithm.STORE_BASED;
	private Map<Integer, Integer> mFrequencyMap;

	public SimilarityBuilderService() {
		super("SimilarityBuilderService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String user = getUser(intent);
		DbAdapter db = DbAdapter.getInstance(getApplicationContext()).open();
		try{
			UserModel userModel = buildUserModel(user, db);
			Log.d("Evaluation", userModel.toJavaCode());
			Log.d("Evaluation", "Do not recommend: " + userModel.storeDetailsView.keySet().toString());
			UserModel evalModel = Evaluation.getEvaluationModel();
			if(evalModel != null)
				userModel = evalModel;
			SimilarityAlgorithm algorithm = (SimilarityAlgorithm) intent.getSerializableExtra(EXTRA_ALGORITHM);
			buildSimilarity(user, db, userModel, algorithm == null ? DEFAULT_ALGORITHM : algorithm);
			//Log.d("Evaluation", "Finished build similarity. Algorithm: " + SimilarityBuilderService.DEFAULT_ALGORITHM + " and model " + Evaluation.getEvalatuationModel().name + " n = " + RecommendationActivity.RECOMMEND_QUANTITY);
			LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_SIMILIRARITY_BUILD_FINISHED));
		} finally { 
			db.close();
		}
	}

	private UserModel buildUserModel(String user, DbAdapter db) {
		final String decayDate = DateFormat.format(DatabaseHelper.DATE_FORMAT_WRITE, getDecayDate()).toString();
		db.deleteStoreDetailViewBefore(decayDate);
		db.deleteSearchPerformedBefore(decayDate);
		db.deleteCategoryVisitedBefore(decayDate);
		Map<Long, String[]> tags = db.returnAllTagsFromStoreDetailsView(user);
		String[] searchs = db.returnAllSearchPerformed(user);
		int[] categoriesVisited = db.returnAllCategoriesVisited(user);
		UserModel userModel = new UserModel(user, tags, searchs, categoriesVisited);
		db.saveUserModel(userModel);
		return userModel;
	}

	private void buildSimilarity(String user, DbAdapter db, UserModel userModel, SimilarityAlgorithm similarityAlgorithm) {
		db.clearSimilarity(user);
		Cursor cursor = db.getStoreBasicInfo(userModel.storeDetailsView.keySet());
		if(cursor != null)
			try{
				int columnId = cursor.getColumnIndex(DatabaseHelper.KEY_ID),
					columnTags = cursor.getColumnIndex(DatabaseHelper.KEY_TAGS),
					columnStoreCategory = cursor.getColumnIndex(DatabaseHelper.KEY_STORECATEGORY);
				if(cursor.moveToFirst()) do{
					double similarityScore = getSimilarityScore(userModel, cursor.getString(columnTags).split(","), cursor.getInt(columnStoreCategory), similarityAlgorithm);
					if(similarityScore > SCORE_MINIMUM)
						db.saveSimilarity(user, cursor.getLong(columnId), similarityScore);
				} while(cursor.moveToNext());
			} finally {
				cursor.close();
			}

	}

	private String getUser(Intent intent) {
		return intent.getStringExtra(EXTRA_USER);
	}

	private Date getDecayDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	private double getSimilarityScore(UserModel userModel, String[] storeTags, int storeCategory, SimilarityAlgorithm similarityAlgorithm) {
		String[] storeDetailView = userModel.getAllStoreDetailViewTags().split(",");
		String[] searchPerformed = userModel.searchPerformed;
		int[] categoriesVisited = userModel.categoriesVisited;
		switch (similarityAlgorithm) {
		case STORE_BASED:
			final Map<String, Double> storeTagsMap = convertStringArrayToMap(storeTags);
			final double similarityStoreDetailView = (storeDetailView == null || storeDetailView.length == 0 ? 0 : calculateCosineSimilarity(convertStringArrayToMap(storeDetailView), storeTagsMap));
			final double similaritySearchPerformed = (searchPerformed == null || searchPerformed.length == 0 ? 0 : calculateCosineSimilarity(convertStringArrayToMap(searchPerformed), storeTagsMap));
			final double similarityCategoriesVisited = (categoriesVisited == null || categoriesVisited.length == 0 ? 0 : getFrequencyMap(categoriesVisited).get(storeCategory).doubleValue()/(double)categoriesVisited.length);
			return similarityStoreDetailView * 0.15d +
					similaritySearchPerformed * 0.6d +
					similarityCategoriesVisited * 0.25d;
		case SIMPLE:
			return calculateSimpleSimilarity(storeDetailView, storeTags);
		case RANDOM:
			return Math.random();
		case TAG_BASED:
			final Map<String, Double> storeTagsMap2 = convertStringArrayToMap(storeTags);
			return (storeDetailView == null || storeDetailView.length == 0 ? 0 : calculateCosineSimilarity(convertStringArrayToMap(storeDetailView), storeTagsMap2));
		}
		throw new InvalidParameterException("Similarity Algorithm " + similarityAlgorithm.name() + " not know.");
	}

	private double calculateSimpleSimilarity(String[] v1, String[] v2) {
		int count = 0;
		for(String u : v1)
			for(String s : v2)
				if(u.equalsIgnoreCase(s))
					count++;
		return Math.min(1f, (double)count/(double)v1.length);
	}

	private double calculateCosineSimilarity(Map<String, Double> v1, Map<String, Double> v2) {
		Set<String> both = new HashSet<String>(v1.keySet());
		both.retainAll(v2.keySet());
		double sclar = 0, norm1 = 0, norm2 = 0;
		if(!(v1 instanceof NonNullDoubleMap))
			v1 = new NonNullDoubleMap<String>(v1);
		if(!(v2 instanceof NonNullDoubleMap))
			v2 = new NonNullDoubleMap<String>(v2);
		for (String k : both) sclar += v1.get(k) * v2.get(k);
		for (String k : v1.keySet()) norm1 += v1.get(k) * v1.get(k);
		for (String k : v2.keySet()) norm2 += v2.get(k) * v2.get(k);
		return sclar / Math.sqrt(norm1 * norm2);
	}

	private Map<String, Double> convertStringArrayToMap(String[] array){
		Map<String, Double> map = new HashMap<String, Double>(array.length);
		final Double one = Double.valueOf(1);
		for(String str : array) {
			map.put(str, one);
		}
		return map;
	}

	private Map<Integer, Integer> getFrequencyMap(int[] categoriesVisited) {
		if(mFrequencyMap == null) {
			mFrequencyMap = new NonNullIntegerMap<Integer>(new HashMap<Integer, Integer>());
			for(int i : categoriesVisited)
				mFrequencyMap.put(i, mFrequencyMap.get(i)+1);
		}
		return mFrequencyMap;
	}
	
	public enum SimilarityAlgorithm {
		SIMPLE, STORE_BASED, RANDOM, TAG_BASED;
	}

	
}
