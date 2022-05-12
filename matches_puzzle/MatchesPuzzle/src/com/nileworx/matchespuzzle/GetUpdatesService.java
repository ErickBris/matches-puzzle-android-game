package com.nileworx.matchespuzzle;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class GetUpdatesService extends IntentService {

	DAO db;
	Context context;
	JSONArray entries = null;

	Handler mHandler;

	Cursor c;

	String siteUrl;

	String jsonExtra;
	JSONObject json;

	JSONArray levels = null;

	private ConnectionDetector cd;

	private NotificationManager nm;
	Notification mBuilder;
	RemoteViews contentView;

	int mCount, mMax;

	// ==============================================================================

	public GetUpdatesService() {
		super("getUpdatesService");
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
		Bundle extras = intent.getExtras();
		if (extras != null) {
			jsonExtra = extras.getString("json");
		}

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

			CharSequence title = "Downloading initializing...";

			contentView = new RemoteViews(getPackageName(), R.layout.download_progress);
			contentView.setImageViewResource(R.id.status_icon, R.drawable.app_icon_medium);
			contentView.setTextViewText(R.id.status_text, title);
			contentView.setProgressBar(R.id.status_progress, 100, 0, false);

			Intent in = new Intent(context, LevelsActivity.class);
			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);			

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder = new NotificationCompat.Builder(context).setTicker("Downloading initializing...").setSmallIcon(R.drawable.app_icon_sml).setContentIntent(contentIntent).build();

			mBuilder.contentView = contentView;

			nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

			nm.notify(1, mBuilder);

			getUpdates(jsonExtra);

		}

	}

	// ==============================================================================

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		context = getApplicationContext();
		db = new DAO(context);
		db.open();

		siteUrl = context.getResources().getString(R.string.siteUrl);			

		return super.onStartCommand(intent, flags, startId);
	}

	// ==============================================================================

	public void getUpdates(final String j) {

		new Thread(new Runnable() {
			public void run() {
				mCount = 0;		

				try {
					json = new JSONObject(j);

					// Getting Array of levels
					levels = json.getJSONArray("levels");					

					mMax = levels.length();

					// looping through All levels
					for (int i = 0; i < levels.length(); i++) {

						JSONObject e = levels.getJSONObject(i);

						// Storing each json item in variable
						
//						int le_number = e.getInt("le_number");
						int le_x_value = e.getInt("le_x_value");
						int le_y_value = e.getInt("le_y_value");
						int le_r_value = e.getInt("le_r_value");
						String le_operator = e.getString("le_operator");
						int le_moves = e.getInt("le_moves");
						String le_solution = e.getString("le_solution");
						int le_web_id = e.getInt("_leid");

						db.addLevel(le_x_value, le_y_value, le_r_value, le_operator, le_moves, le_solution, le_web_id);

						++mCount;

						CharSequence title = "Downloading: " + (int) (((double) mCount / (double) mMax) * 100) + "%";

						contentView.setTextViewText(R.id.status_text, title);
						contentView.setProgressBar(R.id.status_progress, mMax, mCount, false);

						mBuilder.contentView = contentView;
						nm.notify(1, mBuilder);

					}
					

					if ((mCount % mMax) == 0) {
						nm.cancelAll();

						Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon_medium);

						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setTicker("Updating completed successfully").setLargeIcon(largeIcon).setSmallIcon(R.drawable.app_icon_sml).setContentTitle("Matches Puzzle").setContentText("Updating completed successfully")
								.setAutoCancel(true);

						PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

						mBuilder.setContentIntent(contentIntent);

						NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification
						// later on.
						mNotificationManager.notify(1, mBuilder.build());						
						
						// Refresh LevelsActivity if it is current activity
						ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
					    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//					    Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
					    ComponentName componentInfo = taskInfo.get(0).topActivity;				
					    					    
						if(componentInfo.getClassName().equals(componentInfo.getPackageName() + ".LevelsActivity")) {
							Intent intent = new Intent(context, LevelsActivity.class);		
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
							context.startActivity(intent);							
						} 
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}).start();

	}
	
}