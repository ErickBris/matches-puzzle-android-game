package com.nileworx.matchespuzzle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;

public class CheckUpdatesService extends IntentService {

	DAO db;
	Context context;
	Handler mHandler;
	Cursor c;

	String siteUrl, updatesUrl;
	int lastLevel;

	JSONObject json;

	JSONArray levels = null;

	String jsonResultNull = "";

	int levelsNum;

	// ==============================================================================

	public CheckUpdatesService() {
		super("checkUpdatesService");
		mHandler = new Handler();
	}

	// ==============================================================================

	// @Override
	public void onDestroy() {
		db.closeDatabase();
		Log.e("destroy service", "destroy");
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	// ==============================================================================

	@Override
	protected void onHandleIntent(Intent intent) {		
		if (checkUpdates(updatesUrl)) {

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(context, UpdatesDialogActivity.class);
					intent.putExtra("levelsNum", String.valueOf(levelsNum));
					intent.putExtra("json", String.valueOf(json));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				}
			});
		}
	}

	// ==============================================================================

	public int onStartCommand(Intent intent, int flags, int startId) {

		context = getApplicationContext();
		db = new DAO(context);
		db.open();

		siteUrl = context.getResources().getString(R.string.siteUrl);

		lastLevel = db.getLastLevel();

		updatesUrl = siteUrl + "site/get_updates/" + String.valueOf(lastLevel);

		return super.onStartCommand(intent, flags, startId);
	}

	// ==============================================================================

	public boolean checkUpdates(String url) {

		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();

		// getting JSON string from URL
		json = jParser.getJSONFromUrl(url);	

		if (json != null) {			
			if (json.has("levels")) {
				try {

					levels = json.getJSONArray("levels");

					if (levels.length() == 0) {
						return false;
					} else {
						levelsNum = levels.length();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				return false;
			}
		} else {
			return false;
		}

		return true;

	}

}