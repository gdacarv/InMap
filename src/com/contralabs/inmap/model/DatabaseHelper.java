package com.contralabs.inmap.model;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.contralabs.inmap.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private Context mCtx;

	private final static String DATABASE_NAME = "inmapdatabase";
	private final static int DATABASE_VERSION = 2; // Modified 16/12/13 08:57

	public static final String KEY_ID = "_id";

	static final String DATABASE_TABLE_STORE = "store";
	static final String KEY_NAME = "name";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_PHONE = "phone";
	static final String KEY_WEBSITE = "website";
	static final String KEY_LEVEL = "level";
	static final String KEY_STORECATEGORY = "id_storecategory";
	public static final String KEY_TAGS = "tags";
	static final String KEY_EXTRAS = "extras";
	static final String KEY_AREAR1P1X = "arear1p1x";
	static final String KEY_AREAR1P1Y = "arear1p1y";
	static final String KEY_AREAR1P2X = "arear1p2x";
	static final String KEY_AREAR1P2Y = "arear1p2y";
	static final String KEY_AREAR2P1X = "arear2p1x";
	static final String KEY_AREAR2P1Y = "arear2p1y";
	static final String KEY_AREAR2P2X = "arear2p2x";
	static final String KEY_AREAR2P2Y = "arear2p2y";


	static final String DATABASE_TABLE_INFRASTRUCTURE = "infrastructure";
	static final String KEY_INFRACATEGORY = "id_infracategory";
	static final String KEY_X = "x";
	static final String KEY_Y = "y";
	
	static final String DATABASE_TABLE_DETAIL_VIEW = "detailview";
	static final String KEY_USER = "user";
	static final String KEY_STOREID = "storeid";
	static final String KEY_WHEN = "time_viewed";
	
	static final String DATABASE_TABLE_USER_MODEL = "usermodel";
	static final String KEY_SET_DETAILSVIEW = "setdv";
	
	static final String DATABASE_TABLE_SIMILARITY = "similarity";
	static final String KEY_SCORE = "score";


	//static final String DATE_FORMAT_READ = "dd/MM/yyyy hh:mm";
	public static final String DATE_FORMAT_WRITE = "yyyy/MM/dd hh:mm";
	
	private final String DATABASE_CREATE_DETAIL_VIEW = "create table " + DATABASE_TABLE_DETAIL_VIEW + " (" 
			+ KEY_ID + " integer primary key, "
			+ KEY_USER + " text, "
			+ KEY_STOREID + " integer not null, "
			+ KEY_WHEN + " text not null, "
			+ "FOREIGN KEY ("+KEY_STOREID+") REFERENCES "+DATABASE_TABLE_STORE+" ("+KEY_ID+"));";
	
	private final String DATABASE_CREATE_USER_MODEL = "create table " + DATABASE_TABLE_USER_MODEL + " (" 
			+ KEY_ID + " integer primary key, "
			+ KEY_SET_DETAILSVIEW + " text);";
	
	private final String DATABASE_CREATE_SIMILARITY = "create table " + DATABASE_TABLE_SIMILARITY + " (" 
			+ KEY_ID + " integer primary key, "
			+ KEY_SCORE + " real not null, "
			+ KEY_USER + " text, "
			+ KEY_STOREID + " integer not null, "
			+ "FOREIGN KEY ("+KEY_STOREID+") REFERENCES "+DATABASE_TABLE_STORE+" ("+KEY_ID+"));";

	private final String DATABASE_CREATE[] = {"create table " + DATABASE_TABLE_STORE + " (" 
			+ KEY_ID + " integer primary key, "
			+ KEY_NAME + " text not null, "
			+ KEY_DESCRIPTION + " text, "
			+ KEY_PHONE + " text, "
			+ KEY_WEBSITE + " text, "
			+ KEY_LEVEL + " integer not null, "
			+ KEY_STORECATEGORY + " integer not null, "
			+ KEY_TAGS + " text, "
			+ KEY_EXTRAS + " text, "
			+ KEY_AREAR1P1X + " integer, "
			+ KEY_AREAR1P1Y + " integer, "
			+ KEY_AREAR1P2X + " integer, "
			+ KEY_AREAR1P2Y + " integer, "
			+ KEY_AREAR2P1X + " integer, "
			+ KEY_AREAR2P1Y + " integer, "
			+ KEY_AREAR2P2X + " integer, "
			+ KEY_AREAR2P2Y + " integer);",

			"create table " + DATABASE_TABLE_INFRASTRUCTURE + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_USER + " text, "
			+ KEY_INFRACATEGORY + " integer not null, "
			+ KEY_X + " integer not null, "
			+ KEY_Y + " integer not null, "
			+ KEY_LEVEL + " integer not null);",

			DATABASE_CREATE_DETAIL_VIEW,
			
			DATABASE_CREATE_USER_MODEL,
			
			DATABASE_CREATE_SIMILARITY
	};

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mCtx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		createTables(db);
		try {
			populateBase(db);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createTables(SQLiteDatabase db) {
		for(int i = 0; i < DATABASE_CREATE.length; i++)
			db.execSQL(DATABASE_CREATE[i]);
	}

	public void populateBase(SQLiteDatabase db) throws XmlPullParserException, IOException {
		Resources resources = mCtx.getResources();
		populateStores(db, resources.getXml(R.xml.stores));
		populateInfrastructures(db, resources.getXml(R.xml.infrastructures));
	}

	public void populateStores(SQLiteDatabase db, XmlPullParser xpp) throws XmlPullParserException, IOException {
		String[] pointsNames = {KEY_AREAR1P1X, KEY_AREAR1P1Y, KEY_AREAR1P2X, KEY_AREAR1P2Y, KEY_AREAR2P1X, KEY_AREAR2P1Y, KEY_AREAR2P2X, KEY_AREAR2P2Y};
		int eventType;
		ContentValues values = new ContentValues(8);
		String temp, value;
		xpp.next();
		xpp.next();
		do{
			try {
				eventType = xpp.next();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					temp = xpp.getName();
					if(temp.equals("store")){
						values.clear();
					}else{
						eventType = xpp.next();
						if(eventType != XmlPullParser.END_TAG) {
							value = xpp.getText();
							if(temp.equals("level") || temp.equals("id_storecategory") || temp.equals("id"))
								values.put(temp.equals("id") ? KEY_ID : temp, Integer.parseInt(value));
							else if(temp.equals("area")) {
								String[] points = value.split(",");
								for(int i = 0; i < points.length && i < pointsNames.length; i++)
									values.put(pointsNames[i], points[i]);
							}else {
								if(temp.equals("tags"))
									value = value.replace(", ", ",").replace(" ,", ","); // XXX Maybe useless
								values.put(temp, value);
							}
							xpp.next();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if(xpp.getName().equals("store")){
						if(values.containsKey("deleted"))
							db.delete(DATABASE_TABLE_STORE, KEY_ID + " = " + values.getAsString(KEY_ID), null);
						else
							db.insertWithOnConflict(DATABASE_TABLE_STORE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
					}
					break;
				}
			} catch (RuntimeException e) {
				Log.e("DbPopulating", "Current ContentValues: " + values);
				throw e;
			}
		}while (eventType != XmlPullParser.END_DOCUMENT);
	}

	public void populateInfrastructures(SQLiteDatabase db, XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType;
		ContentValues values = new ContentValues(4);
		String temp, value;
		xpp.next();
		xpp.next();
		do{
			eventType = xpp.next();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				temp = xpp.getName();
				if(temp.equals("infrastructure")){
					values.clear();
				}else if(temp.equals("id")){
					xpp.next();
					value = xpp.getText();
					values.put(KEY_ID, Integer.parseInt(value));
					xpp.next();
				}else{
					xpp.next();
					value = xpp.getText();
					values.put(temp, Integer.parseInt(value));
					xpp.next();
				}
				break;
			case XmlPullParser.END_TAG:
				if(xpp.getName().equals("infrastructure")){
					if(values.containsKey("deleted"))
						db.delete(DATABASE_TABLE_INFRASTRUCTURE, KEY_ID + " = " + values.getAsString(KEY_ID), null);
					else
						db.insertWithOnConflict(DATABASE_TABLE_INFRASTRUCTURE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				}
				break;
			}
		}while (eventType != XmlPullParser.END_DOCUMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL(DATABASE_CREATE_DETAIL_VIEW);
			db.execSQL(DATABASE_CREATE_USER_MODEL);
			db.execSQL(DATABASE_CREATE_SIMILARITY);
		}
	}
}