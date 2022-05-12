package com.nileworx.matchespuzzle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class LevelsActivity extends Activity {

	GridView levelsGrid;

	LevelsAdapter adapter;

	DAO db;
	Cursor c;

	ArrayList<HashMap<String, String>> levelsArray;
	HashMap<String, String> map;

	static final String KEY_ID = "_leid";
	static final String KEY_LEVEL = "le_number";
	static final String KEY_X_VALUE = "le_x_value";
	static final String KEY_Y_VALUE = "le_y_value";
	static final String KEY_R_VALUE = "le_r_value";
	static final String KEY_OPERATOR = "le_operator";
	static final String KEY_MOVES = "le_moves";
	static final String KEY_OPEN = "le_open";
	static final String KEY_COMPLETED = "le_completed";

	CustomDialog dialog;

	UpdateClass update;
	SoundClass sou;

	// ==============================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_levels);

		AdView ad = (AdView) findViewById(R.id.adView);
		ad.loadAd(new AdRequest.Builder().build());
		
		dialog = new CustomDialog(LevelsActivity.this);
		
		sou = new SoundClass(LevelsActivity.this);

		db = new DAO(this);
		db.open();

		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);
				finish();

			}
		});

		final ImageButton reset = (ImageButton) findViewById(R.id.reset);
		reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				sou.playSound(R.raw.buttons);

				
				dialog.showDialog(R.layout.purple_dialog, "resetDlg", "After reset you will lose all your data. Continue?", null);

			}
		});

		final ImageButton checkUpdates = (ImageButton) findViewById(R.id.update);
		checkUpdates.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				update = new UpdateClass(LevelsActivity.this);
				update.handleUpdates();
			}
		});

		final ImageButton instructions = (ImageButton) findViewById(R.id.instructions);
		instructions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				final Dialog dialog = new Dialog(LevelsActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				dialog.setContentView(R.layout.instructions_dialog);

				Button dismiss = (Button) dialog.findViewById(R.id.dismissBtn);
				// if button is clicked, close the custom dialog
				dismiss.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
			}
		});

		c = db.getLevels();

		if (c.getCount() != 0) {

			levelsArray = new ArrayList<HashMap<String, String>>();

			levelsGrid = (GridView) findViewById(R.id.levelsGrid);

			do {
				map = new HashMap<String, String>();

				map.put(KEY_ID, c.getString(c.getColumnIndex(KEY_ID)));
				map.put(KEY_LEVEL, c.getString(c.getColumnIndex(KEY_LEVEL)));			
				map.put(KEY_OPEN, c.getString(c.getColumnIndex(KEY_OPEN)));
				map.put(KEY_COMPLETED, c.getString(c.getColumnIndex(KEY_COMPLETED)));

				levelsArray.add(map);

			} while (c.moveToNext());

			adapter = new LevelsAdapter(this, levelsArray);
			levelsGrid.setAdapter(adapter);

			// Click event for single list row
			levelsGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					map = levelsArray.get(position);

					if (map.get(KEY_OPEN).equals(String.valueOf(1))) {
						sou.playSound(R.raw.buttons);
						Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
						intent.putExtra("LevelId", map.get(KEY_ID));
						startActivity(intent);
					}

				}
			});

		}
	}

}
