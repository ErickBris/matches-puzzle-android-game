package com.nileworx.matchespuzzle;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DAO {

	// All Static variables

	private SQLiteDatabase database;
	private DataBaseHandler dbHandler;

	private static final String TABLE_LEVELS = "levels";
	private static final String TABLE_COINS = "coins";
	private static final String TABLE_HELPS = "helps";

	// Levels Table Columns names

	private static final String LE_ID = "_leid";
	private static final String LE_NUMBER = "le_number";
	private static final String LE_X_VALUE = "le_x_value";
	private static final String LE_Y_VALUE = "le_y_value";
	private static final String LE_R_VALUE = "le_r_value";
	private static final String LE_OPERATOR = "le_operator";
	private static final String LE_MOVES = "le_moves";
	private static final String LE_SOLUTION = "le_solution";
	private static final String LE_OPEN = "le_open";
	private static final String LE_COMPLETED = "le_completed";
	private static final String LE_STATUS = "le_status";
	private static final String LE_WEB_ID = "le_web_id";

	private static final String COINS_ID = "_coid";
	private static final String TOTAL_COINS = "total_coins";
	private static final String USED_COINS = "used_coins";

	// private static final String HE_ID = "_heid";
	private static final String HE_LEVEL_ID = "he_level_id";

	// ==============================================================================

	public DAO(Context context) {
		dbHandler = new DataBaseHandler(context);
		try {

			dbHandler.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}
		try {

			dbHandler.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;

		}

	}

	// ==============================================================================

	// Getting All Levels
	public Cursor getLevels() {

		String query = "SELECT * FROM " + TABLE_LEVELS + " ORDER BY " + LE_NUMBER + " ASC";
		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor;

	}

	// ==============================================================================

	// Getting One Level
	public Cursor getOneLevel(String LevelID) {
		// Select All Query

		String query = "SELECT * FROM " + TABLE_LEVELS + " WHERE " + LE_ID + " = '" + LevelID + "'";
		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor;

	}

	// ==============================================================================

	public void setLevelCompleted(String LevelID) {
		open();
		ContentValues values = new ContentValues();
		values.put(LE_COMPLETED, "1");

		// Update Row
		database.update(TABLE_LEVELS, values, LE_ID + "=?", new String[] { LevelID });

	}

	// ==============================================================================

	public void setLevelOpened(String LevelID) {
		open();
		ContentValues values = new ContentValues();
		values.put(LE_OPEN, "1");

		// Update Row
		database.update(TABLE_LEVELS, values, LE_ID + "=?", new String[] { LevelID });

	}

	// ==============================================================================

	public Integer getNextLevel(String LevelID) {
		open();

		String orderQuery = "SELECT " + LE_NUMBER + " FROM " + TABLE_LEVELS + " WHERE " + LE_ID + " = " + LevelID;
		String query = "SELECT " + LE_ID + " FROM " + TABLE_LEVELS + " WHERE " + LE_NUMBER + " > (" + orderQuery + ") LIMIT 1";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		
		Integer nextLevel = 0;
		if (cursor.getCount() > 0) {
			nextLevel = cursor.getInt(cursor.getColumnIndex(LE_ID));
		}		

		return nextLevel;

	}

	// ==============================================================================

	public void resetGame() {
		open();

		ContentValues levelsValues = new ContentValues();
		levelsValues.put(LE_OPEN, 0);
		levelsValues.put(LE_COMPLETED, 0);
		database.update(TABLE_LEVELS, levelsValues, null, null);

		ContentValues coinsValues = new ContentValues();
		coinsValues.put(TOTAL_COINS, 50);
		coinsValues.put(USED_COINS, 0);
		database.update(TABLE_COINS, coinsValues, null, null);

		String emptyQuery = "DELETE FROM " + TABLE_HELPS;
		database.execSQL(emptyQuery);

		String query = "SELECT " + LE_ID + " FROM " + TABLE_LEVELS + " ORDER BY  " + LE_NUMBER + " ASC";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();
		String fLevel = String.valueOf(cursor.getInt(cursor.getColumnIndex(LE_ID)));

		ContentValues fLevelValues = new ContentValues();
		fLevelValues.put(LE_OPEN, 1);
		database.update(TABLE_LEVELS, fLevelValues, LE_ID + "=?", new String[] { fLevel });
	}

	// ==============================================================================

	public Cursor getCoinsCount() {
		open();

		String query = "SELECT * " + " FROM " + TABLE_COINS + " WHERE _coid = 1";
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();

		return cursor;
	}

	// ==============================================================================

	public Cursor getHelpState(String leID) {
		open();

		String query = "SELECT *" + " FROM " + TABLE_HELPS + " WHERE " + HE_LEVEL_ID + " = " + leID;
		Cursor cursor = database.rawQuery(query, null);
		cursor.moveToFirst();

		return cursor;
	}

	// ==============================================================================

	public void updateHelpState(String heLevelID, String heField) {
		open();

		ContentValues helpsValues = new ContentValues();
		helpsValues.put(heField, 1);
		int result = database.update(TABLE_HELPS, helpsValues, HE_LEVEL_ID + "=?", new String[] { heLevelID });
		if (result == 0) {
			helpsValues.put(HE_LEVEL_ID, heLevelID);
			database.insert(TABLE_HELPS, null, helpsValues);
		}
	}

	// ==============================================================================

	public void addUsedCoins(String coins) {
		open();

		String query = "UPDATE " + TABLE_COINS + " SET " + USED_COINS + " = " + USED_COINS + " + " + coins + " WHERE " + COINS_ID + " = 1";
		database.execSQL(query);

	}

	// ==============================================================================

	public void addTotalCoins() {
		open();

		String query = "UPDATE " + TABLE_COINS + " SET " + TOTAL_COINS + " = " + TOTAL_COINS + " + 2" + " WHERE " + COINS_ID + " = 1";

		database.execSQL(query);

	}

	// ==============================================================================

	public int getLastLevel() {
		open();
		String query = "SELECT MAX(" + LE_WEB_ID + ") AS " + LE_WEB_ID + " FROM " + TABLE_LEVELS;

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(LE_WEB_ID));

	}

	// ==============================================================================

	public int getLastLevelNumber() {
		open();

		String query = "SELECT MAX(" + LE_NUMBER + ") AS " + LE_NUMBER + " FROM " + TABLE_LEVELS;

		Cursor cursor = database.rawQuery(query, null);

		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(LE_NUMBER));

	}

	// ==============================================================================

	public void addLevel(int le_x_value, int le_y_value, int le_r_value, String le_operator, int le_moves, String le_solution, int le_web_id) {
		open();
		int le_number = getLastLevelNumber() + 1;
		try {			
			ContentValues v = new ContentValues();
			v.put(LE_NUMBER, le_number);
			v.put(LE_X_VALUE, le_x_value);
			v.put(LE_Y_VALUE, le_y_value);
			v.put(LE_R_VALUE, le_r_value);
			v.put(LE_OPERATOR, le_operator);
			v.put(LE_MOVES, le_moves);
			v.put(LE_SOLUTION, le_solution);
			v.put(LE_OPEN, "0");
			v.put(LE_COMPLETED, "0");
			v.put(LE_STATUS, "1");
			v.put(LE_WEB_ID, le_web_id);

			database.insert(TABLE_LEVELS, null, v);
			closeDatabase();
		} catch (Error e) {

		}

	}

	// ==============================================================================

	// public Cursor getLevels2() {
	//
	// String query = "SELECT * FROM " + TABLE_LEVELS + " ORDER BY  " +
	// LE_ORDER + " ASC";
	// Cursor cursor = database.rawQuery(query, null);
	//
	// cursor.moveToFirst();
	// return cursor;
	//
	// }
	//
	// public void addLevels2(String le_country, int le_id) {
	//
	// ContentValues v = new ContentValues();
	// v.put("le_country", le_country);
	// v.put("le_flag", le_country + ".png");
	// v.put("le_open", 0);
	// v.put("le_completed", 0);
	// v.put("le_flag_sdcard", 0);
	// v.put("le_order", le_id);
	// v.put("le_status", 1);
	// v.put("le_web_id", le_id);
	//
	// database.insert("levels_2", null, v);
	//
	// }
	//
	// public Cursor getLevels2() {
	//
	// String query = "SELECT * FROM " + TABLE_LEVELS + " ORDER BY  " +
	// LE_LEVEL + " ASC";
	// Cursor cursor = database.rawQuery(query, null);
	//
	// cursor.moveToFirst();
	// return cursor;
	//
	// }
	//
	// public void addLevels2(String le_name, int le_level, String
	// le_wikipedia, String le_info, String le_player, int le_id) {
	//
	// ContentValues v = new ContentValues();
	// v.put("le_name", le_name);
	// v.put("le_image", String.valueOf(le_id) + ".png");
	// v.put("le_level", le_level);
	// v.put("le_wikipedia", le_wikipedia);
	// v.put("le_info", le_info);
	// v.put("le_player", le_player);
	// v.put("le_tries", 0);
	// v.put("le_points", 0);
	// v.put("le_completed", "0");
	// v.put("le_image_sdcard", 0);
	// v.put("le_order", le_id);
	// v.put("le_status", 1);
	// v.put("le_web_id", le_id);
	//
	// database.insert("levels_2", null, v);
	//
	// }

	// ==============================================================================

	public void open() throws SQLException {
		database = dbHandler.getWritableDatabase();

	}

	// ==============================================================================

	public void closeDatabase() {
		dbHandler.close();
	}

	// ==============================================================================

}
