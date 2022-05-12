package com.nileworx.matchespuzzle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	SharedPreferences mSharedPreferences;

	DAO db;

	String marketLink;

	String siteUrl, updatesUrl;

	int lastLevel;
	private ConnectionDetector cd;

	UpdateClass update;

	SoundClass sou;
	CustomDialog dialog;

	// ========================================================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dialog = new CustomDialog(MainActivity.this);

		sou = new SoundClass(MainActivity.this);

		db = new DAO(this);
		db.open();

		marketLink = "https://play.google.com/store/apps/details?id=" + getPackageName();

		cd = new ConnectionDetector(MainActivity.this);
		lastLevel = db.getLastLevel();

		if (cd.isConnectingToInternet()) {
			// Internet Connection is not present
			Intent checkUpdates = new Intent(MainActivity.this, CheckUpdatesService.class);
			startService(checkUpdates);
		}

		mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
		final Editor e = mSharedPreferences.edit();

		final ImageButton play = (ImageButton) findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.play);
				Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
				startActivity(intent);

			}
		});

		final ImageButton sound = (ImageButton) findViewById(R.id.sound);

		if (mSharedPreferences.getInt("sound", 1) == 1) {
			sound.setBackgroundResource(R.drawable.button_sound_on_main);
		} else {
			sound.setBackgroundResource(R.drawable.button_sound_off_main);
		}

		sound.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mSharedPreferences.getInt("sound", 1) == 1) {
					e.putInt("sound", 0);
					e.commit();
					sound.setBackgroundResource(R.drawable.button_sound_off_main);
				} else {
					e.putInt("sound", 1);
					e.commit();
					sound.setBackgroundResource(R.drawable.button_sound_on_main);

					sou.playSound(R.raw.buttons);
				}
				// e.commit(); // save changes
			}
		});

		final ImageButton share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String shareBody = "Matches Puzzle on Google Play \n\n" + marketLink;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Matches Puzzle");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});

		final ImageButton rate = (ImageButton) findViewById(R.id.rate);
		rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				Intent intent = new Intent(Intent.ACTION_VIEW);

				intent.setData(Uri.parse("market://details?id=" + getPackageName()));

				if (!MyStartActivity(intent)) {
					// Market (Google play) app seems not installed, let's try
					// to open a webbrowser
					intent.setData(Uri.parse(marketLink));
					if (!MyStartActivity(intent)) {
						// Well if this also fails, we have run out of options,
						// inform the user.
						Toast.makeText(MainActivity.this, "Could not open Android market, please install the market app.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		final ImageButton exit = (ImageButton) findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				dialog.showDialog(R.layout.purple_dialog, "exitDlg", "Are you sure you want to exit?", null);

			}
		});
	}

	// ========================================================================================================

	private boolean MyStartActivity(Intent aIntent) {
		try {
			startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}	
}
