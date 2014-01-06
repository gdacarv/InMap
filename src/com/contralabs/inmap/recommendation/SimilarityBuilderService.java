package com.contralabs.inmap.recommendation;

import java.util.Calendar;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateFormat;

import com.contralabs.inmap.model.DatabaseHelper;
import com.contralabs.inmap.model.DbAdapter;

public class SimilarityBuilderService extends IntentService {

	private static final float SCORE_MINIMUM = 0.1f;
	private static final String EXTRA_USER = "extraUser";

	public SimilarityBuilderService() {
		super("SimilarityBuilderService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String user = getUser(intent);
		DbAdapter db = DbAdapter.getInstance(getApplicationContext()).open();
		try{
			String userModel = buildUserModel(user, db);
			buildSimilarity(user, db, userModel);
		} finally { 
			db.close();
		}
	}

	private void buildSimilarity(String user, DbAdapter db, String userModel) {
		db.clearSimilarity(user);
		Cursor cursor = db.getStoreTags();
		if(cursor != null)
			try{
				String[] userModelTags = userModel.split(",");
				int columnId = cursor.getColumnIndex(DatabaseHelper.KEY_ID),
						columnTags = cursor.getColumnIndex(DatabaseHelper.KEY_TAGS);
				if(cursor.moveToFirst()) do{
					float similarityScore = getSimilarityScore(userModelTags, cursor.getString(columnTags).split(","));
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

	private float getSimilarityScore(String[] userModelTags, String[] storeTags) {
		int count = 0;
		for(String u : userModelTags)
			for(String s : storeTags)
				if(u.equals(s))
					count++;
		return Math.min(1f, (float)count/(float)userModelTags.length);
	}

}
