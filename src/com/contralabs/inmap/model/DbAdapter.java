package com.contralabs.inmap.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.contralabs.inmap.recommendation.UserModel;
import com.contralabs.inmap.salvadorshop.applicationdata.InfrastructureCategory;
import com.contralabs.inmap.salvadorshop.applicationdata.StoreCategory;
import com.contralabs.inmap.utils.Utils;

import static com.contralabs.inmap.model.DatabaseHelper.*;


public class DbAdapter {

	private static DbAdapter mInstance;
	private int mUsers = 0;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private DateFormat mDateFormat;

	private final Context mCtx; 

	public static DbAdapter getInstance(Context ctx){
		if(mInstance == null)
			mInstance = new DbAdapter(ctx);
		return mInstance;
	}

	private DbAdapter(Context ctx){
		this.mCtx = ctx;
		mDateFormat = new SimpleDateFormat(DATE_FORMAT_WRITE, Locale.US);
	}

	public synchronized DbAdapter open() throws SQLException{
		mUsers++;
		if(isClose()){
			mDbHelper = new DatabaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
		}
		return this;
	}

	public synchronized void close(){
		mUsers--;
		if(mUsers == 0)
			mDbHelper.close();
	}
	public boolean isClose(){
		return mDb == null || !mDb.isOpen();
	}

	public Store getStore(long id) {
		return getStoreFromCursor(mDb.query(DATABASE_TABLE_STORE, null, KEY_ID + " = " + id, null, null, null, null));
	}

	public Store[] getStores(StoreParameters parameters) {
		boolean useAnd = false;
		StringBuilder selectionBuilder = new StringBuilder();

		String categoryString = parameters.getCategoryString();
		if(categoryString.length() > 0) {
			selectionBuilder.append(KEY_STORECATEGORY).append(" in (").append(categoryString).append(")");
			useAnd = true;
		}

		int level = parameters.getLevel();
		if(level >= 0) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append(KEY_LEVEL).append(" = ").append(level);
		}

		Coordinate hasPoint = parameters.getContainsPoint();
		if(hasPoint != null) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append("((")
			.append(KEY_AREAR1P1X).append(" < ").append(hasPoint.x)
			.append(" AND ").append(KEY_AREAR1P1Y).append(" < ").append(hasPoint.y)
			.append(" AND ").append(KEY_AREAR1P2X).append(" >= ").append(hasPoint.x)
			.append(" AND ").append(KEY_AREAR1P2Y).append(" >= ").append(hasPoint.y)
			.append(") OR (")
			.append(KEY_AREAR2P1X).append(" < ").append(hasPoint.x)
			.append(" AND ").append(KEY_AREAR2P1Y).append(" < ").append(hasPoint.y)
			.append(" AND ").append(KEY_AREAR2P2X).append(" >= ").append(hasPoint.x)
			.append(" AND ").append(KEY_AREAR2P2Y).append(" >= ").append(hasPoint.y)
			.append("))");
		}

		String anytext = parameters.getAnytext();
		if(anytext != null) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append("(")
			.append(KEY_NAME).append(" LIKE '%").append(anytext).append("%' OR ")
			.append(KEY_TAGS).append(" LIKE '%").append(anytext).append("%' OR ")
			.append(KEY_DESCRIPTION).append(" LIKE '%").append(anytext).append("%' OR ")
			.append(KEY_WEBSITE).append(" LIKE '%").append(anytext)
			.append("%')");
		}

		String selection = selectionBuilder.toString();
		Cursor cursor = mDb.query(DATABASE_TABLE_STORE, null, selection, null, null, null, null); // TODO Right things based on Parameters
		return cursor != null ? getStoresFromCursor(cursor) : null;
	}

	public void getStoresCountByCategory(int[] storesCount) {
		Cursor cursor = mDb.query(DATABASE_TABLE_STORE, new String[]{ KEY_STORECATEGORY, "COUNT(*) as storecount" } , null, null, KEY_STORECATEGORY, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			int countColumn = cursor.getColumnIndex("storecount"),
				idColumn = cursor.getColumnIndex(KEY_STORECATEGORY);
			do {
				storesCount[cursor.getInt(idColumn)-1] = cursor.getInt(countColumn);
			} while(cursor.moveToNext());
		}
	}

	private Store[] getStoresFromCursor(Cursor cursor) {
		Store[] stores;
		if(cursor.moveToFirst()){
			int[] pointsColumn = {cursor.getColumnIndex(KEY_AREAR1P1X), 
					cursor.getColumnIndex(KEY_AREAR1P1Y), 
					cursor.getColumnIndex(KEY_AREAR1P2X), 
					cursor.getColumnIndex(KEY_AREAR1P2Y), 
					cursor.getColumnIndex(KEY_AREAR2P1X), 
					cursor.getColumnIndex(KEY_AREAR2P1Y), 
					cursor.getColumnIndex(KEY_AREAR2P2X), 
					cursor.getColumnIndex(KEY_AREAR2P2Y)};
			stores = new Store[cursor.getCount()];
			int idColumn = cursor.getColumnIndex(KEY_ID),
			nameColumn = cursor.getColumnIndex(KEY_NAME),
			descriptionColumn = cursor.getColumnIndex(KEY_DESCRIPTION),
			phoneColumn = cursor.getColumnIndex(KEY_PHONE),
			websiteColumn = cursor.getColumnIndex(KEY_WEBSITE),
			levelColumn = cursor.getColumnIndex(KEY_LEVEL),
			categoryColumn = cursor.getColumnIndex(KEY_STORECATEGORY),
			tagsColumn = cursor.getColumnIndex(KEY_TAGS),
			extrasColumn = cursor.getColumnIndex(KEY_EXTRAS);
			StoreCategory[] categorys = StoreCategory.values();
			for(int i = 0; i < stores.length; i++){
				int[] areaIntArray = new int[pointsColumn.length];
				for(int l = 0; l < areaIntArray.length; l++)
					areaIntArray[l] = cursor.getInt(pointsColumn[l]);
				String tagsString = cursor.getString(tagsColumn);
				stores[i] = new Store(cursor.getLong(idColumn), cursor.getString(nameColumn),
						cursor.getString(descriptionColumn), cursor.getString(phoneColumn), 
						cursor.getString(websiteColumn), categorys[cursor.getInt(categoryColumn)-1], 
						cursor.getInt(levelColumn), tagsString == null ? new String[0] : tagsString.split(","),
						areaIntArray,
						cursor.getString(extrasColumn)
						);
				cursor.moveToNext();
			}
		}else
			stores = new Store[0];

		cursor.close();
		return stores;
	}

	private Store getStoreFromCursor(Cursor cursor) {
		if(!cursor.moveToFirst())
			return null;
		int[] pointsColumn = {cursor.getColumnIndex(KEY_AREAR1P1X), 
				cursor.getColumnIndex(KEY_AREAR1P1Y), 
				cursor.getColumnIndex(KEY_AREAR1P2X), 
				cursor.getColumnIndex(KEY_AREAR1P2Y), 
				cursor.getColumnIndex(KEY_AREAR2P1X), 
				cursor.getColumnIndex(KEY_AREAR2P1Y), 
				cursor.getColumnIndex(KEY_AREAR2P2X), 
				cursor.getColumnIndex(KEY_AREAR2P2Y)};
		int idColumn = cursor.getColumnIndex(KEY_ID),
		nameColumn = cursor.getColumnIndex(KEY_NAME),
		descriptionColumn = cursor.getColumnIndex(KEY_DESCRIPTION),
		phoneColumn = cursor.getColumnIndex(KEY_PHONE),
		websiteColumn = cursor.getColumnIndex(KEY_WEBSITE),
		levelColumn = cursor.getColumnIndex(KEY_LEVEL),
		categoryColumn = cursor.getColumnIndex(KEY_STORECATEGORY),
		tagsColumn = cursor.getColumnIndex(KEY_TAGS),
		extrasColumn = cursor.getColumnIndex(KEY_EXTRAS);
		StoreCategory[] categorys = StoreCategory.values();
		int[] areaIntArray = new int[pointsColumn.length];
		for(int l = 0; l < areaIntArray.length; l++)
			areaIntArray[l] = cursor.getInt(pointsColumn[l]);
		String tagsString = cursor.getString(tagsColumn);
		Store store = new Store(cursor.getLong(idColumn), cursor.getString(nameColumn),
				cursor.getString(descriptionColumn), cursor.getString(phoneColumn), 
				cursor.getString(websiteColumn), categorys[cursor.getInt(categoryColumn)-1], 
				cursor.getInt(levelColumn), tagsString == null ? new String[0] : tagsString.split(","),
				areaIntArray,
				cursor.getString(extrasColumn));
		cursor.close();
		return store;
	}

	public Infrastructure[] getInfrastructures(int category, int level) {
		Cursor cursor = mDb.query(DATABASE_TABLE_INFRASTRUCTURE, null, 
				KEY_INFRACATEGORY + " = ? and " + KEY_LEVEL + " = ?", 
				new String[] {String.valueOf(category), String.valueOf(level)}, null, null, null); 
		/*Cursor cursor = mDb.query(DATABASE_TABLE_INFRASTRUCTURE, null, null, null, null, null, null); */
		return cursor != null ? getInfrastructuresFromCursor(cursor) : null;
	}

	private Infrastructure[] getInfrastructuresFromCursor(Cursor cursor) {
		Infrastructure[] infras;
		if(cursor.moveToFirst()){
			infras = new Infrastructure[cursor.getCount()];
			int categoryColumn = cursor.getColumnIndex(KEY_INFRACATEGORY),
			levelColumn = cursor.getColumnIndex(KEY_LEVEL),
			xColumn = cursor.getColumnIndex(KEY_X),
			yColumn = cursor.getColumnIndex(KEY_Y);
			InfrastructureCategory[] categorys = InfrastructureCategory.values();
			for(int i = 0; i < infras.length; i++){
				infras[i] = new Infrastructure(categorys[cursor.getInt(categoryColumn)-1], 
						cursor.getInt(levelColumn), cursor.getInt(xColumn), cursor.getInt(yColumn));
				cursor.moveToNext();
			}
		}else
			infras = new Infrastructure[0];

		cursor.close();
		return infras;
	}

	public void populateInfrastructures(XmlPullParser xpp) throws XmlPullParserException, IOException {
		mDbHelper.populateInfrastructures(mDb, xpp);
	}

	public void populateStores(XmlPullParser xpp) throws XmlPullParserException, IOException {
		mDbHelper.populateStores(mDb, xpp);
	}
	
	public void saveStoreDetailView(String user, Store store){
		String when = mDateFormat.format(new Date());
		ContentValues values = new ContentValues(user != null && user.length() > 0 ? 3 : 2);
		if(user != null && user.length() > 0)
			values.put(KEY_USER, user);
		values.put(KEY_STOREID, store.getId());
		values.put(KEY_WHEN, when);
		mDb.insert(DATABASE_TABLE_DETAIL_VIEW, null, values);
	}
	
	public void saveSearchPerformed(String user, String query){
		String when = mDateFormat.format(new Date());
		ContentValues values = new ContentValues(user != null && user.length() > 0 ? 3 : 2);
		if(user != null && user.length() > 0)
			values.put(KEY_USER, user);
		values.put(KEY_QUERY, query);
		values.put(KEY_WHEN, when);
		mDb.insert(DATABASE_TABLE_SEARCH_PERFORMED, null, values);
	}

	public void saveCategoryVisited(String user, int id) {
		String when = mDateFormat.format(new Date());
		ContentValues values = new ContentValues(user != null && user.length() > 0 ? 3 : 2);
		if(user != null && user.length() > 0)
			values.put(KEY_USER, user);
		values.put(KEY_STORECATEGORY, id);
		values.put(KEY_WHEN, when);
		mDb.insert(DATABASE_TABLE_CATEGORY_VISITED, null, values);
	}
	
	public void saveUserModel(UserModel userModel){
		ContentValues values = new ContentValues(userModel.name != null && userModel.name.length() > 0 ? 4 : 3);
		if(userModel.name != null && userModel.name.length() > 0)
			values.put(KEY_USER, userModel.name);
		values.put(KEY_SET_DETAILSVIEW, userModel.getAllStoreDetailViewTags());
		values.put(KEY_SET_SEARCHPERFORMED, Utils.arrayToString(userModel.searchPerformed, ","));
		values.put(KEY_SET_CATEGORYVISITED, Utils.arrayToString(userModel.categoriesVisited, ","));
		if(mDb.update(DATABASE_TABLE_USER_MODEL, values, KEY_USER + (userModel.name != null && userModel.name.length() > 0 ? " = " + userModel.name : " IS NULL"), null) == 0)
			mDb.insert(DATABASE_TABLE_USER_MODEL, null, values);
	}
	
	public int deleteBefore(String table, String when){
		return mDb.delete(table, KEY_WHEN + " < ?", new String[]{when});
	}
	
	public int deleteStoreDetailViewBefore(String when){
		return deleteBefore(DATABASE_TABLE_DETAIL_VIEW, when);
	}
	
	public int deleteSearchPerformedBefore(String when){
		return deleteBefore(DATABASE_TABLE_SEARCH_PERFORMED, when);
	}
	
	public int deleteCategoryVisitedBefore(String when){
		return deleteBefore(DATABASE_TABLE_CATEGORY_VISITED, when);
	}
	
	public Map<Long, String[]> returnAllTagsFromStoreDetailsView(String user){
		Cursor cursor = mDb.rawQuery(
				"SELECT " + DATABASE_TABLE_STORE + "." + KEY_TAGS + ", " + DATABASE_TABLE_DETAIL_VIEW + "." + KEY_STOREID + " FROM " + DATABASE_TABLE_DETAIL_VIEW + " INNER JOIN " + DATABASE_TABLE_STORE + " ON " + DATABASE_TABLE_DETAIL_VIEW + "." + KEY_STOREID + "=" + DATABASE_TABLE_STORE + "." + KEY_ID + " WHERE " + DATABASE_TABLE_DETAIL_VIEW + "." + KEY_USER + (user == null ? " IS NULL" : " = ?")
				, (user == null ? null : new String[]{user}));
		if(cursor == null)
			throw new RuntimeException("Could not get Store Details View info.");
		try{
			int columnTags = cursor.getColumnIndex(KEY_TAGS),
					columnId = cursor.getColumnIndex(KEY_STOREID);
			Map<Long, String[]> map = null;
			if(cursor.moveToFirst()){
				map = new HashMap<Long, String[]>(cursor.getCount());
				do {
					map.put(Long.valueOf(cursor.getLong(columnId)), cursor.getString(columnTags).split(","));
				} while(cursor.moveToNext());
			}
			return map;
		} finally {
			cursor.close();
		}
	}
	
	public String[] returnAllSearchPerformed(String user){
		Cursor cursor = mDb.rawQuery(
				"SELECT " + KEY_QUERY + " FROM " + DATABASE_TABLE_SEARCH_PERFORMED + " WHERE " + KEY_USER + (user == null ? " IS NULL" : " = ?")
				, (user == null ? null : new String[]{user}));
		if(cursor == null)
			return new String[0];
		try{
			int columnQuery = cursor.getColumnIndex(KEY_QUERY);
			if(cursor.moveToFirst()){
				String[] queries = new String[cursor.getCount()];
				int i = 0;
				do{
					queries[i] = cursor.getString(columnQuery);
					i++;
				}while(cursor.moveToNext());
				return queries;
			}
			return new String[0];
		} finally {
			cursor.close();
		}
	}
	
	public int[] returnAllCategoriesVisited(String user){
		Cursor cursor = mDb.rawQuery(
				"SELECT " + KEY_STORECATEGORY + " FROM " + DATABASE_TABLE_CATEGORY_VISITED + " WHERE " + KEY_USER + (user == null ? " IS NULL" : " = ?")
				, (user == null ? null : new String[]{user}));
		if(cursor == null)
			return new int[0];
		try{
			int columnId = cursor.getColumnIndex(KEY_STORECATEGORY);
			if(cursor.moveToFirst()){
				int[] ids = new int[cursor.getCount()];
				int i = 0;
				do{
					ids[i] = cursor.getInt(columnId);
					i++;
				}while(cursor.moveToNext());
				return ids;
			}
			return new int[0];
		} finally {
			cursor.close();
		}
	}
	
	public void clearSimilarity(String user){
		mDb.delete(DATABASE_TABLE_SIMILARITY, KEY_USER + (user == null ? " IS NULL" : " = ?"), (user == null ? null : new String[] {user}));
	}
	
	public Cursor getStoreBasicInfo(Collection<Long> set){
		String exceptIds = "";
		if(set != null && !set.isEmpty()){
			StringBuilder ids = new StringBuilder();
			for(Long id : set){
				ids.append('\'').append(id).append('\'').append(',');
			}
			exceptIds = ids.substring(0, ids.length()-1);
		}
		return mDb.query(DATABASE_TABLE_STORE, new String[]{KEY_ID, KEY_TAGS, KEY_STORECATEGORY}, KEY_ID + " NOT IN (" + exceptIds + ")", null, null, null, null);
	}

	public void saveSimilarity(String user, long storeId, double similarityScore) {
		ContentValues values = new ContentValues(user != null && user.length() > 0 ? 3 : 2);
		if(user != null && user.length() > 0)
			values.put(KEY_USER, user);
		values.put(KEY_STOREID, storeId);
		values.put(KEY_SCORE, similarityScore);
		mDb.insert(DATABASE_TABLE_SIMILARITY, null, values);
	}
	
	public Pair<Store, Double>[] getStoresFromSimilarityScore(String user, int results) {
		final String query = "SELECT " + DATABASE_TABLE_SIMILARITY + "." + KEY_SCORE + ", " + DATABASE_TABLE_STORE + ".* FROM " + DATABASE_TABLE_STORE + " INNER JOIN " + DATABASE_TABLE_SIMILARITY + " ON " + DATABASE_TABLE_SIMILARITY + "." + KEY_STOREID + "=" + DATABASE_TABLE_STORE + "." + KEY_ID + " WHERE " + DATABASE_TABLE_SIMILARITY + "." + KEY_USER + (user == null ? " IS NULL" : " = ?") + " ORDER BY " + DATABASE_TABLE_SIMILARITY + "." + KEY_SCORE + " DESC LIMIT ?";
		return getStoresAndScoreFromCursor(mDb.rawQuery(
				query
				, (user == null ? new String[] {String.valueOf(results)} : new String[] {user, String.valueOf(results)})));
	}

	private Pair<Store, Double>[] getStoresAndScoreFromCursor(Cursor cursor) {
		Pair<Store, Double>[] stores;
		if(cursor.moveToFirst()){
			int[] pointsColumn = {cursor.getColumnIndex(KEY_AREAR1P1X), 
					cursor.getColumnIndex(KEY_AREAR1P1Y), 
					cursor.getColumnIndex(KEY_AREAR1P2X), 
					cursor.getColumnIndex(KEY_AREAR1P2Y), 
					cursor.getColumnIndex(KEY_AREAR2P1X), 
					cursor.getColumnIndex(KEY_AREAR2P1Y), 
					cursor.getColumnIndex(KEY_AREAR2P2X), 
					cursor.getColumnIndex(KEY_AREAR2P2Y)};
			stores = new Pair[cursor.getCount()];
			int idColumn = cursor.getColumnIndex(KEY_ID),
			nameColumn = cursor.getColumnIndex(KEY_NAME),
			descriptionColumn = cursor.getColumnIndex(KEY_DESCRIPTION),
			phoneColumn = cursor.getColumnIndex(KEY_PHONE),
			websiteColumn = cursor.getColumnIndex(KEY_WEBSITE),
			levelColumn = cursor.getColumnIndex(KEY_LEVEL),
			categoryColumn = cursor.getColumnIndex(KEY_STORECATEGORY),
			tagsColumn = cursor.getColumnIndex(KEY_TAGS),
			extrasColumn = cursor.getColumnIndex(KEY_EXTRAS),
			scoreColumn = cursor.getColumnIndex(KEY_SCORE);
			StoreCategory[] categorys = StoreCategory.values();
			for(int i = 0; i < stores.length; i++){
				int[] areaIntArray = new int[pointsColumn.length];
				for(int l = 0; l < areaIntArray.length; l++)
					areaIntArray[l] = cursor.getInt(pointsColumn[l]);
				String tagsString = cursor.getString(tagsColumn);
				Store store = new Store(cursor.getLong(idColumn), cursor.getString(nameColumn),
						cursor.getString(descriptionColumn), cursor.getString(phoneColumn), 
						cursor.getString(websiteColumn), categorys[cursor.getInt(categoryColumn)-1], 
						cursor.getInt(levelColumn), tagsString == null ? new String[0] : tagsString.split(","),
						areaIntArray,
						cursor.getString(extrasColumn)
						);
				stores[i] = new Pair<Store, Double>(store, Double.valueOf(cursor.getDouble(scoreColumn)));
				cursor.moveToNext();
			}
		}else
			stores = new Pair[0];

		cursor.close();
		return stores;
	}
}
