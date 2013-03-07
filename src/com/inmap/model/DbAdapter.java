package com.inmap.model;

import com.inmap.salvadorshop.applicationdata.InfrastructureCategory;
import com.inmap.salvadorshop.applicationdata.StoreCategory;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class DbAdapter {

	private static DbAdapter mInstance;
	private int mUsers = 0;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx; 

	public static DbAdapter getInstance(Context ctx){
		if(mInstance == null)
			mInstance = new DbAdapter(ctx);
		return mInstance;
	}

	private DbAdapter(Context ctx){
		this.mCtx = ctx;
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

	public Store[] getStores(StoreParameters parameters) {
		boolean useAnd = false;
		StringBuilder selectionBuilder = new StringBuilder();
		
		String categoryString = parameters.getCategoryString();
		if(categoryString.length() > 0) {
			selectionBuilder.append(DatabaseHelper.KEY_STORECATEGORY).append(" in (").append(categoryString).append(")");
			useAnd = true;
		}
		
		int level = parameters.getLevel();
		if(level >= 0) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append(DatabaseHelper.KEY_LEVEL).append(" = ").append(level);
		}
		
		Coordinate hasPoint = parameters.getContainsPoint();
		if(hasPoint != null) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append("((")
				.append(DatabaseHelper.KEY_AREAR1P1X).append(" < ").append(hasPoint.x)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR1P1Y).append(" < ").append(hasPoint.y)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR1P2X).append(" >= ").append(hasPoint.x)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR1P2Y).append(" >= ").append(hasPoint.y)
				.append(") OR (")
				.append(DatabaseHelper.KEY_AREAR2P1X).append(" < ").append(hasPoint.x)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR2P1Y).append(" < ").append(hasPoint.y)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR2P2X).append(" >= ").append(hasPoint.x)
				.append(" AND ").append(DatabaseHelper.KEY_AREAR2P2Y).append(" >= ").append(hasPoint.y)
				.append("))");
		}
		
		String anytext = parameters.getAnytext();
		if(anytext != null) {
			if(useAnd)
				selectionBuilder.append(" AND ");
			else 
				useAnd = true;
			selectionBuilder.append("(")
				.append(DatabaseHelper.KEY_NAME).append(" LIKE '%").append(anytext).append("%' OR ")
				.append(DatabaseHelper.KEY_TAGS).append(" LIKE '%").append(anytext).append("%' OR ")
				.append(DatabaseHelper.KEY_DESCRIPTION).append(" LIKE '%").append(anytext).append("%' OR ")
				.append(DatabaseHelper.KEY_WEBSITE).append(" LIKE '%").append(anytext)
				.append("%')");
		}
		
		String selection = selectionBuilder.toString();
		Cursor cursor = mDb.query(DatabaseHelper.DATABASE_TABLE_STORE, null, selection, null, null, null, null); // TODO Right things based on Parameters
		return cursor != null ? getStoresFromCursor(cursor) : null;
	}

	private Store[] getStoresFromCursor(Cursor cursor) {
		Store[] stores;
		if(cursor.moveToFirst()){
			int[] pointsColumn = {cursor.getColumnIndex(DatabaseHelper.KEY_AREAR1P1X), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR1P1Y), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR1P2X), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR1P2Y), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR2P1X), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR2P1Y), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR2P2X), 
					cursor.getColumnIndex(DatabaseHelper.KEY_AREAR2P2Y)};
			stores = new Store[cursor.getCount()];
			int idColumn = cursor.getColumnIndex(DatabaseHelper.KEY_ID),
				nameColumn = cursor.getColumnIndex(DatabaseHelper.KEY_NAME),
				descriptionColumn = cursor.getColumnIndex(DatabaseHelper.KEY_DESCRIPTION),
				phoneColumn = cursor.getColumnIndex(DatabaseHelper.KEY_PHONE),
				websiteColumn = cursor.getColumnIndex(DatabaseHelper.KEY_WEBSITE),
				levelColumn = cursor.getColumnIndex(DatabaseHelper.KEY_LEVEL),
				categoryColumn = cursor.getColumnIndex(DatabaseHelper.KEY_STORECATEGORY),
				tagsColumn = cursor.getColumnIndex(DatabaseHelper.KEY_TAGS);
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
						areaIntArray);
				cursor.moveToNext();
			}
		}else
			stores = new Store[0];
				
		cursor.close();
		return stores;
	}

	public Infrastructure[] getInfrastructures(int category, int level) {
		Cursor cursor = mDb.query(DatabaseHelper.DATABASE_TABLE_INFRASTRUCTURE, null, 
				DatabaseHelper.KEY_INFRACATEGORY + " = ? and " + DatabaseHelper.KEY_LEVEL + " = ?", 
				new String[] {String.valueOf(category), String.valueOf(level)}, null, null, null); 
		/*Cursor cursor = mDb.query(DatabaseHelper.DATABASE_TABLE_INFRASTRUCTURE, null, null, null, null, null, null); */
		return cursor != null ? getInfrastructuresFromCursor(cursor) : null;
	}

	private Infrastructure[] getInfrastructuresFromCursor(Cursor cursor) {
		Infrastructure[] infras;
		if(cursor.moveToFirst()){
			infras = new Infrastructure[cursor.getCount()];
			int categoryColumn = cursor.getColumnIndex(DatabaseHelper.KEY_INFRACATEGORY),
				levelColumn = cursor.getColumnIndex(DatabaseHelper.KEY_LEVEL),
				xColumn = cursor.getColumnIndex(DatabaseHelper.KEY_X),
				yColumn = cursor.getColumnIndex(DatabaseHelper.KEY_Y);
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

	

}
