package com.nileworx.matchespuzzle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.KeyEvent;

public class UpdateClass {

	public Context context;

	String siteUrl, updatesUrl;

	DAO db;
	Cursor c;

	int lastLevel;

	JSONArray levels = null;

	JSONObject json;
	String jsonResultNull = "";
	CustomDialog dialog;

	private ConnectionDetector cd;

	// ==============================================================================

	public UpdateClass(Context context) {
		this.context = context;

		db = new DAO(context);
		db.open();

		lastLevel = db.getLastLevel();

		siteUrl = context.getResources().getString(R.string.siteUrl);
		updatesUrl = siteUrl + "site/get_updates/" + String.valueOf(lastLevel);

		dialog = new CustomDialog(context);

	}

	// ==============================================================================

	public void handleUpdates() {

		// check first for internet
		cd = new ConnectionDetector(context);

		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present

			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Internet Connection Error");
			builder.setMessage("Please connect to an internet connection!");

			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

				}

			});

			builder.show();

		} else {
			new CheckUpdates().execute(new String[] { updatesUrl });
		}

	}

	// ==============================================================================

	private class CheckUpdates extends AsyncTask<String, Void, Void> {

		ProgressDialog mProgressDialog;

		@Override
		protected void onPostExecute(Void result) {

			mProgressDialog.dismiss();

			if (json != null) {
				if (jsonResultNull.equals("true")) {
					dialog.showDialog(R.layout.purple_dialog, "noUpdatesDlg", "There are not any updates!", null);

				} else {
					dialog.showDialog(R.layout.purple_dialog, "updatesDlg", "There are new " + String.valueOf(levels.length()) + " levels. Download?", json.toString());

				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Internet Connection Error");
				builder.setMessage("Please connect to an internet connection!");

				builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

					}

				});

				builder.show();
			}

		} 

		// ------------------------------------------------------------------------

		@Override
		protected void onPreExecute() {

			mProgressDialog = ProgressDialog.show(context, "Loading...", "Checking updates...");
			mProgressDialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
						mProgressDialog.dismiss();
//						cancel(true);
					return false;
				}
			});

		}

		// ------------------------------------------------------------------------

		@Override
		protected Void doInBackground(String... params) {

			// Creating JSON Parser instance
			JSONParser jParser = new JSONParser();

			// getting JSON string from URL
//			Log.e("url", params[0]);
			json = jParser.getJSONFromUrl(params[0]);

			//
			try {
				if (json != null) {
//					Log.e("json", json.toString());
					levels = json.getJSONArray("levels");

					if (levels.length() == 0) {						
						jsonResultNull = "true";						
					}
				} else {
					jsonResultNull = "true";
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			mProgressDialog.dismiss();
		}
	}
}