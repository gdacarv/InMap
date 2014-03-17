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

	private static final double SCORE_MINIMUM = 0.0001f;
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
			UserModel userModel = buildUserModel(user, db);
			SimilarityAlgorithm algorithm = (SimilarityAlgorithm) intent.getSerializableExtra(EXTRA_ALGORITHM);
			buildSimilarity(user, db, userModel, algorithm == null ? DEFAULT_ALGORITHM : algorithm);
		} finally { 
			db.close();
		}
	}

	private void buildSimilarity(String user, DbAdapter db, UserModel userModel, SimilarityAlgorithm similarityAlgorithm) {
		db.clearSimilarity(user);
		Cursor cursor = db.getStoreTags();
		if(cursor != null)
			try{
				String[] userModelStoreDetailsView = userModel.storeDetailsView;
				int columnId = cursor.getColumnIndex(DatabaseHelper.KEY_ID),
						columnTags = cursor.getColumnIndex(DatabaseHelper.KEY_TAGS);
				if(cursor.moveToFirst()) do{
					double similarityScore = getSimilarityScore(userModelStoreDetailsView, userModel.searchPerformed, cursor.getString(columnTags).split(","), similarityAlgorithm);
					if(similarityScore > SCORE_MINIMUM)
						db.saveSimilarity(user, cursor.getLong(columnId), similarityScore);
				} while(cursor.moveToNext());
			} finally {
				cursor.close();
			}

	}

	private UserModel buildUserModel(String user, DbAdapter db) {
		UserModel userModel = new UserModel();
		final String decayDate = DateFormat.format(DatabaseHelper.DATE_FORMAT_WRITE, getDecayDate()).toString();
		db.deleteStoreDetailViewBefore(decayDate);
		db.deleteSearchPerformedBefore(decayDate);
		String tags = db.returnAllTagsFromStoreDetailsView(user);
		String[] searchs = db.returnAllSearchPerformed(user);
		db.saveUserModel(user, tags, searchs);
		userModel.searchPerformed = searchs;
		userModel.storeDetailsView = tags.split(",");
		return userModel;
	}

	private String getUser(Intent intent) {
		return intent.getStringExtra(EXTRA_USER);
	}

	private Date getDecayDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		return cal.getTime();
	}

	private double getSimilarityScore(String[] storeDetailView, String[] searchPerformed, String[] storeTags, SimilarityAlgorithm similarityAlgorithm) {
		switch (similarityAlgorithm) {
		case COSINE:
			final Map<String, Double> storeTagsMap = convertStringArrayToMap(storeTags);
			return calculateCosineSimilarity(convertStringArrayToMap(storeDetailView), storeTagsMap) * 0.3f +
					calculateCosineSimilarity(convertStringArrayToMap(searchPerformed), storeTagsMap) * 0.7f;
		case SIMPLE: // TODO Old version, using only StoreDetailView
			return calculateSimpleSimilarity(storeDetailView, storeTags);
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
