package com.inmap.model;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.inmap.salvadorshop.R;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

	private Context mCtx;

	private final static String DATABASE_NAME = "inmapdatabase";
	private final static int DATABASE_VERSION = 1;

	static final String KEY_ID = "_id";

	static final String DATABASE_TABLE_STORE = "store";
	static final String KEY_NAME = "name";
	static final String KEY_DESCRIPTION = "description";
	static final String KEY_PHONE = "phone";
	static final String KEY_WEBSITE = "website";
	static final String KEY_LEVEL = "level";
	static final String KEY_STORECATEGORY = "id_storecategory";
	static final String KEY_TAGS = "tags";
	static final String KEY_AREA = "area";
	

	static final String DATABASE_TABLE_INFRASTRUCTURE = "infrastructure";
	static final String KEY_INFRACATEGORY = "id_infracategory";
	static final String KEY_X = "x";
	static final String KEY_Y = "y";


	//static final String DATE_FORMAT_READ = "dd/MM/yyyy hh:mm";
	//static final String DATE_FORMAT_WRITE = "yyyy/MM/dd hh:mm";

	private final String DATABASE_CREATE[] = {"create table " + DATABASE_TABLE_STORE + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null, "
			+ KEY_DESCRIPTION + " text, "
			+ KEY_PHONE + " text, "
			+ KEY_WEBSITE + " text, "
			+ KEY_LEVEL + " integer not null, "
			+ KEY_STORECATEGORY + " integer not null, "
			+ KEY_TAGS + " text, "
			+ KEY_AREA + " text not null);",
			
			"create table " + DATABASE_TABLE_INFRASTRUCTURE + " (" 
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_INFRACATEGORY + " integer not null, "
			+ KEY_X + " integer not null, "
			+ KEY_Y + " integer not null, "
			+ KEY_LEVEL + " integer not null);"
			
			
	};

	DatabaseHelper(Context context) {
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
		populateStores(db);
		populateInfrastructures(db);
	}

	private void populateStores(SQLiteDatabase db) throws XmlPullParserException, IOException {
		XmlResourceParser xpp = mCtx.getResources().getXml(R.xml.stores);
		int eventType;
		ContentValues values = new ContentValues(8);
		String temp, value;
		xpp.next();
		xpp.next();
		do{
			eventType = xpp.next();
			switch (eventType) {
			case XmlPullParser.START_TAG:
				temp = xpp.getName();
				if(temp.equals("store")){
					values.clear();
				}else{
					xpp.next();
					value = xpp.getText();
					if(temp.equals("level") || temp.equals("id_storecategory"))
						values.put(temp, Integer.parseInt(value));
					else
						values.put(temp, value);
					xpp.next();
				}
				break;
			case XmlPullParser.END_TAG:
				if(xpp.getName().equals("store")){
					db.insert(DATABASE_TABLE_STORE, null, values);
				}
				break;
			}
		}while (eventType != XmlPullParser.END_DOCUMENT);
	}

	private void populateInfrastructures(SQLiteDatabase db) throws XmlPullParserException, IOException {
		XmlResourceParser xpp = mCtx.getResources().getXml(R.xml.infrastructures);
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
				}else{
					xpp.next();
					value = xpp.getText();
					values.put(temp, Integer.parseInt(value));
					xpp.next();
				}
				break;
			case XmlPullParser.END_TAG:
				if(xpp.getName().equals("infrastructure")){
					db.insert(DATABASE_TABLE_INFRASTRUCTURE, null, values);
				}
				break;
			}
		}while (eventType != XmlPullParser.END_DOCUMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*switch (oldVersion) {
			case 1:
				db.execSQL("ALTER TABLE " + DATABASE_TABLE_MSGS + " ADD COLUMN " + KEY_MENSAGEM + " text;");
				ContentValues args;
				int[] primsgs1 = { R.string.msg1_1_1, R.string.msg2_1_1, R.string.msg3_1_1, R.string.msg4_1_1, R.string.msg5_1_1, R.string.msg_1_end};
				for(int j = 0; j < 5; j++)
					for(int i = 0; primsgs1[j]+i < primsgs1[j+1]; i++){
						args = new ContentValues();
						args.put(KEY_MSGID, primsgs1[j]+i);
						args.put(KEY_NIVEL, j+1);
						db.insert(DATABASE_TABLE_MSGS, null, args);
					}
			}*/
	}
}