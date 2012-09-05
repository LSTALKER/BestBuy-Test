package com.bestbuy.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheDBAdapter {
	private static final String TAG = "DBAdapter";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TAG = "tag";
	public static final String KEY_URL = "url";
	public static final String KEY_DATA = "data";
	public static final String KEY_EXPIRES = "expires";

	private static final String DATABASE_NAME = "cache";
	private static final String DATABASE_TABLE = "cachedata";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, " + KEY_TAG + " text not null, " + KEY_URL + " text not null, " + KEY_DATA + " text not null, " + KEY_EXPIRES + " text not null);";

	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor that creates a table and handles upgrades
	 * 
	 * @param ctx
	 */
	public CacheDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	/**
	 * Database handler takes care of creating and destroying the table, and
	 * upgrading
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			BBYLog.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	private static String unescapeString(String string) {
		if (string == null) {
			return null;
		}
		return string.replace("'", "");
	}

	/**
	 * Open the database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public CacheDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Close the database
	 */
	public void close() {
		DBHelper.close();
	}

	public boolean deleteCacheItem(String tag, String url) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		boolean result = false;
		if (db == null || !db.isOpen()) {
			open();
			result = db.delete(DATABASE_TABLE, KEY_TAG + "=''" + tag + "'' AND " + KEY_URL + "=''" + url + "''", null) > 0;
			close();
		}
		return result;
	}
	
	public boolean deleteCacheTag(String tag) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		boolean result = false;
		if (db == null || !db.isOpen()) {
			open();
			result = db.delete(DATABASE_TABLE, KEY_TAG + "=''" + tag + "''", null) > 0;
			close();
		}
		return result;
	}

	private boolean updateCacheItem(String tag, String url, String data, String expires) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		data = DatabaseUtils.sqlEscapeString(data);
		expires = DatabaseUtils.sqlEscapeString(expires);
		boolean result = false;
		if (db == null || !db.isOpen()) {
			open();
			ContentValues args = new ContentValues();
			args.put(KEY_TAG, tag);
			args.put(KEY_URL, url);
			args.put(KEY_DATA, data);
			args.put(KEY_EXPIRES, expires);
			result = db.update(DATABASE_TABLE, args, KEY_URL + "=?",new String[]{String.valueOf(url)}) > 0;
			close();
		}
		return result;
	}

	private long insertCacheItem(String tag, String url, String data, String expires) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		data = DatabaseUtils.sqlEscapeString(data);
		expires = DatabaseUtils.sqlEscapeString(expires);
		long result = 0;
		if (db == null || !db.isOpen()) {
			open();
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TAG, tag);
			initialValues.put(KEY_URL, url);
			initialValues.put(KEY_DATA, data);
			initialValues.put(KEY_EXPIRES, expires);
			
			result = db.insert(DATABASE_TABLE, null, initialValues);
			close();
		}
		return result;
	}

	public String getCacheData(String tag, String url) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		BBYLog.d(TAG, "GETTING CACHE FOR: " + url);
		String result = null;
		if (db == null || !db.isOpen()) {
			open();
			//Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_DATA}, KEY_TAG + "=''" + tag + "'' AND " + KEY_URL + "=''" + url + "''", null, null, null, null, null);
			Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_DATA}, KEY_TAG + "=?" +" AND " + KEY_URL + "=?", new String[]{tag,url}, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() > 0) {
					result = cursor.getString(0);
				}
			}
			cursor.close();
			close();
		}
		return unescapeString(result);
	}

	public void setCacheData(String tag, String url, String data, String expires) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		data = DatabaseUtils.sqlEscapeString(data);
		expires = DatabaseUtils.sqlEscapeString(expires);
		BBYLog.d(TAG, "SETTING CACHE FOR: " + url);
		boolean didUpdate;
		didUpdate = updateCacheItem(tag, url, data, expires);
		if (!didUpdate) {
			insertCacheItem(tag, url, data, expires);
		}
	}

	public String getCacheExpiration(String tag, String url) {
		tag = DatabaseUtils.sqlEscapeString(tag);
		url = DatabaseUtils.sqlEscapeString(url);
		String result = null;
		if (db == null || !db.isOpen()) {
			open();
			//Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_EXPIRES }, KEY_TAG + "=''" + tag +"'' AND " + KEY_URL + "=''"+ url + "''",  null, null, null, null, null);
			  Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_EXPIRES}, KEY_TAG + "=?" +" AND " + KEY_URL + "=?", new String[]{tag,url}, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (cursor.getCount() > 0) {
					result = cursor.getString(0);
				}
			}
			cursor.close();
			close();
		}
		return unescapeString(result);
	}
}