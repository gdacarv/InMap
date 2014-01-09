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
import android.text.format.DateFormat;

import com.contralabs.inmap.model.DatabaseHelper;
import com.contralabs.inmap.model.DbAdapter;

public class SimilarityBuilderService extends IntentService {

	private static final double SCORE_MINIMUM = 0.1f;
	private static final String EXTRA_USER = "extraUser";
	private static final String EXTRA_ALGORITHM = "extraUser";
	private static final SimilarityAlgorithm DEFAULT_ALGORITHM = SimilarityAlgorithm.COSINE;

	public SimilarityBuilderService() {
		super("SimilarityBuilderService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String user = getUser(intent);
		DbAdapter db = DbAdapter.getInstance(getApplicationContext()).open();
		try{
			String userModel = buildUserModel(user, db);
			SimilarityAlgorithm algorithm = (SimilarityAlgorithm) intent.getSerializableExtra(EXTRA_ALGORITHM);
			buildSimilarity(user, db, userModel, algorithm == null ? DEFAULT_ALGORITHM : algorithm);
		} finally { 
			db.close();
		}
	}

	private void buildSimilarity(String user, DbAdapter db, String userModel, SimilarityAlgorithm similarityAlgorithm) {
		db.clearSimilarity(user);
		Cursor cursor = db.getStoreTags();
		if(cursor != null)
			try{
				String[] userModelTags = userModel.split(",");
				int columnId = cursor.getColumnIndex(DatabaseHelper.KEY_ID),
						columnTags = cursor.getColumnIndex(DatabaseHelper.KEY_TAGS);
				if(cursor.moveToFirst()) do{
					double similarityScore = getSimilarityScore(userModelTags, cursor.getString(columnTags).split(","), similarityAlgorithm);
					if(similarityScore > SCORE_MINIMUM)
						db.saveSimilarity(user, cursor.getLong(columnId), similarityScore);
				} while(cursor.moveToNext());
			} finally {
				cursor.close();
			}

	}

	private String buildUserModel(String user, DbAdapter db) {
		db.deleteStoreDetailViewBefore(DateFormat.format(DatabaseHelper.DATE_FORMAT_WRITE, getDecayDate()).toString());
		String tags = db.returnAllTagsFromStoreDetailsView(user);
		db.saveUserModel(user, tags);
		return tags;
	}

	private String getUser(Intent intent) {
		return intent.getStringExtra(EXTRA_USER);
	}

	private Date getDecayDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	private double getSimilarityScore(String[] userModelTags, String[] storeTags, SimilarityAlgorithm similarityAlgorithm) {
		switch (similarityAlgorithm) {
		case COSINE:
			return calculateCosineSimilarity(convertStringArrayToMap(userModelTags), convertStringArrayToMap(storeTags));
		case SIMPLE:
			return calculateSimpleSimilarity(userModelTags, storeTags);
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
	
	public enum SimilarityAlgorithm {
		SIMPLE, COSINE;
	}
}
