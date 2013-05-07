package com.contralabs.inmap.model;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class StoreContentProvider extends ContentProvider {

	private DatabaseHelper mDatabaseHelper;

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String query = uri.getLastPathSegment().toLowerCase();
		Cursor cursor = mDatabaseHelper.getWritableDatabase().query(DatabaseHelper.DATABASE_TABLE_STORE, new String[] {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1, DatabaseHelper.KEY_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA}, DatabaseHelper.KEY_NAME + " LIKE '%" + query + "%'", null, null, null, DatabaseHelper.KEY_NAME);
		//cursor.moveToFirst();
		//Log.i("StoreContentProvider", "query result data 1: " + cursor.getLong(cursor.getColumnIndex("SUGGEST_COLUMN_INTENT_DATA")));
		//cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return ContentResolver.CURSOR_DIR_BASE_TYPE + "/com.inmap.model.StoreContentProvider";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	
}
