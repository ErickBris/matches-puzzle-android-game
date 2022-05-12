package com.nileworx.matchespuzzle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog {

	public Context context;
	MediaPlayer sound;

	SharedPreferences mSharedPreferences;

	DAO db;
	Cursor c;
	SoundClass sou;

	// ==============================================================================

	public CustomDialog(Context context) {
		this.context = context;

		db = new DAO(context);
		db.open();

		sou = new SoundClass(context);	
	}

	// ==============================================================================

	public void showDialog(int layout, String dialogName, String msg, String data) {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.setContentView(layout);

		Typeface hoboSTD = Typeface.createFromAsset(context.getAssets(), "fonts/" + context.getResources().getString(R.string.main_font));

		// set the custom dialog components - text, image and button
		TextView message = (TextView) dialog.findViewById(R.id.message);
		message.setText(msg.trim());
		message.setTypeface(hoboSTD);

		LinearLayout confirmDlg = (LinearLayout) dialog.findViewById(R.id.confirmDlg);
		LinearLayout wrongDlg = (LinearLayout) dialog.findViewById(R.id.wrongDlg);
		LinearLayout alertDlg = (LinearLayout) dialog.findViewById(R.id.alertDlg);

		if (dialogName.equals("exitDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			exitDlg(dialog);
		} else if (dialogName.equals("updatesActivityDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			updatesActivityDlg(dialog, data);
		} else if (dialogName.equals("resetDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			resetDlg(dialog);
		} else if (dialogName.equals("noUpdatesDlg")) {
			alertDlg.setVisibility(View.VISIBLE);
			noUpdatesDlg(dialog);
		} else if (dialogName.equals("updatesDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			updatesDlg(dialog, data);
		} else if (dialogName.equals("noCoinsDlg")) {
			alertDlg.setVisibility(View.VISIBLE);
			noCoinsDlg(dialog);
		} else if (dialogName.equals("helpDlg")) {
			confirmDlg.setVisibility(View.VISIBLE);
			helpDlg(dialog);
		} else if (dialogName.equals("solutionDlg")) {
			alertDlg.setVisibility(View.VISIBLE);
			solutionDlg(dialog);
		} else if (dialogName.equals("correctDlg")) {
			GameActivity act = (GameActivity) context;
			LinearLayout coins = (LinearLayout) dialog.findViewById(R.id.coins);
			
			if (act.completedBefore == 1) {
				coins.setVisibility(View.GONE);
			} else {
				TextView coinsEarned = (TextView) dialog.findViewById(R.id.coinsEarned);
				coinsEarned.setTypeface(hoboSTD);
			}
						
			correctDlg(dialog, data);
		} else if (dialogName.equals("wrongDlg")) {
			wrongDlg.setVisibility(View.VISIBLE);
			wrongDlg(dialog);
		}

		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
	}

	// ==============================================================================

	private void exitDlg(final Dialog dialog) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				((Activity) context).finish();
				System.exit(0);
			}
		});
	}

	// ==============================================================================

	private void updatesActivityDlg(final Dialog dialog, final String json) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				// dialog.dismiss();
				((Activity) context).finish();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				// dialog.dismiss();
				((Activity) context).finish();
				Intent getUpdates = new Intent(context, GetUpdatesService.class);
				getUpdates.putExtra("json", json);
				((Activity) context).startService(getUpdates);
			}
		});

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					((Activity) context).finish();
				}
				return false;
			}
		});

	}

	// ==============================================================================

	private void resetDlg(final Dialog dialog) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				db.resetGame();
				Intent intent = ((Activity) context).getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity) context).finish();
				context.startActivity(intent);
				Toast.makeText(context, "The game has been reset successfully", Toast.LENGTH_LONG).show();
			}
		});

	}

	// ==============================================================================

	private void noUpdatesDlg(final Dialog dialog) {
		Button dismissBtn = (Button) dialog.findViewById(R.id.dismissBtn);
		// if button is clicked, close the custom dialog
		dismissBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});
	}

	// ==============================================================================

	private void updatesDlg(final Dialog dialog, final String json) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				Intent getUpdates = new Intent(context, GetUpdatesService.class);
				getUpdates.putExtra("json", json);
				context.startService(getUpdates);

				dialog.dismiss();
			}
		});
	}

	// ==============================================================================

	private void noCoinsDlg(final Dialog dialog) {
		Button dismissBtn = (Button) dialog.findViewById(R.id.dismissBtn);
		// if button is clicked, close the custom dialog
		dismissBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});
	}

	// ==============================================================================

	private void helpDlg(final Dialog dialog) {

		Button noBtn = (Button) dialog.findViewById(R.id.noBtn);
		// if button is clicked, close the custom dialog
		noBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});

		Button yesBtn = (Button) dialog.findViewById(R.id.yesBtn);
		// if button is clicked, close the custom dialog
		yesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				GameActivity act = (GameActivity) context;

				String coins = "0";
				switch (act.globalViewId) {
				case R.id.solution:
					coins = "10";
					break;

				case R.id.facebook:
					coins = "5";
					break;

				case R.id.twitter:
					coins = "5";
					break;
				}

				db.addUsedCoins(coins);
				act.coinsValue.setText(String.valueOf(act.getCoinsNumber()));
				dialog.dismiss();
				act.executeHelp(act.globalViewId);

			}
		});
	}

	// ==============================================================================

	private void solutionDlg(final Dialog dialog) {
		Button dismissBtn = (Button) dialog.findViewById(R.id.dismissBtn);
		// if button is clicked, close the custom dialog
		dismissBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				dialog.dismiss();
			}
		});
	}

	// ==============================================================================

	private void correctDlg(final Dialog dialog, final String nextLevelId) {
		Button levelsBtn = (Button) dialog.findViewById(R.id.levelsBtn);
		// if button is clicked, close the custom dialog
		levelsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				Intent intent = new Intent(context, LevelsActivity.class);

				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity) context).finish();
				context.startActivity(intent);
			}
		});

		Button nextBtn = (Button) dialog.findViewById(R.id.nextBtn);
		// if button is clicked, close the custom dialog
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);

				Intent intent = new Intent(context, GameActivity.class);
				intent.putExtra("LevelId", nextLevelId);
				((Activity) context).finish();
				context.startActivity(intent);
			}
		});

		if (nextLevelId.equals("0")) {
			nextBtn.setVisibility(View.GONE);
		}

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					Intent intent = ((Activity) context).getIntent();
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					((Activity) context).finish();
					context.startActivity(intent);
				}
				return false;
			}
		});
	}

	// ==============================================================================

	private void wrongDlg(final Dialog dialog) {
		Button levelsBtn = (Button) dialog.findViewById(R.id.levelsBtn);
		// if button is clicked, close the custom dialog
		levelsBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);
				Intent intent = new Intent(context, LevelsActivity.class);

				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity) context).finish();
				context.startActivity(intent);
			}
		});

		Button retryBtn = (Button) dialog.findViewById(R.id.retryBtn);
		// if button is clicked, close the custom dialog
		retryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);

				Intent intent = ((Activity) context).getIntent();
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity) context).finish();
				context.startActivity(intent);
			}
		});

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
					Intent intent = ((Activity) context).getIntent();
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					((Activity) context).finish();
					context.startActivity(intent);
				}
				return false;
			}
		});
	}

}