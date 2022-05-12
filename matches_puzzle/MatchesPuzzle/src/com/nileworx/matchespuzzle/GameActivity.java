package com.nileworx.matchespuzzle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class GameActivity extends Activity implements View.OnTouchListener {

	// Variables

	public DragController dragController;
	public DragController mDragController; // Object that sends out
											// drag-drop
	// events while a view is being
	// moved.
	public DragLayer mDragLayer; // The ViewGroup that supports
									// drag-drop.

	public static final boolean Debugging = false;

	DropSpot[] xValMatchesImgs = new DropSpot[7];
	DropSpot[] yValMatchesImgs = new DropSpot[7];
	DropSpot[] rValMatchesImgs = new DropSpot[7];
	DropSpot operatorMatcheImg;

	public int[] xMatchesIDs, yMatchesIDs, rMatchesIDs;
	final int[] matchesDrawables = new int[] { R.drawable.match_on,
			R.drawable.match2_on, R.drawable.match2_on, R.drawable.match_on,
			R.drawable.match2_on, R.drawable.match2_on, R.drawable.match_on };
	int match = R.drawable.match_on;
	int match2 = R.drawable.match2_on;

	final String[] matchesTags = new String[] { "match_on", "match2_on",
			"match2_on", "match_on", "match2_on", "match2_on", "match_on" };

	String[] xMatches, yMatches, rMatches;

	DAO db;
	Cursor c;

	String levelId;
	Integer x, y, r;
	String operator;
	int moves;
	String leSolution;
	String leCompleted;
	int cMoves = 0;
	int completedBefore;

	SoundClass sou;
	CustomDialog dialog;

	static final String KEY_ID = "_leid";
	static final String KEY_LEVEL = "le_number";
	static final String KEY_X_VALUE = "le_x_value";
	static final String KEY_Y_VALUE = "le_y_value";
	static final String KEY_R_VALUE = "le_r_value";
	static final String KEY_OPERATOR = "le_operator";
	static final String KEY_MOVES = "le_moves";
	static final String KEY_SOLUTION = "le_solution";
	static final String KEY_OPEN = "le_open";
	static final String KEY_COMPLETED = "le_completed";
	static final String KEY_WEB_ID = "le_web_id";

	TextView levelInfo, movesInfo, helpMsg, coinsValue, coinsX;

	SharedPreferences mSharedPreferences;
	Editor editor;

	String siteUrl, urlToShare;
	int leWebId;
	int costCoins = 0;

	int globalViewId;

	ImageButton solution;
	Button facebook, twitter;
	private InterstitialAd interstitial;

	int dragMode = 1;
	View clickedMatch = null;
	int cancelClicked = 1;

	// Methods

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		final Editor e = mSharedPreferences.edit();

		dialog = new CustomDialog(GameActivity.this);
		sou = new SoundClass(GameActivity.this);

		siteUrl = getResources().getString(R.string.siteUrl);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		int sWidth = displaymetrics.widthPixels;
		int sHeight = displaymetrics.heightPixels;

		int dens = displaymetrics.densityDpi;
		double wi = (double) sWidth / (double) dens;
		double hi = (double) sHeight / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		double screenInches = Math.sqrt(x + y);

		if (sWidth == 320 && sHeight == 480 && screenInches >= 3) {
			setContentView(R.layout.activity_game_3_2);
		} else if (sWidth > 480 && screenInches >= 4 && screenInches <= 5) {
			setContentView(R.layout.activity_game_4x);	
		} else if (screenInches >= 5 && screenInches <= 6.5) {
			setContentView(R.layout.activity_game_5x);		
		} else {
			setContentView(R.layout.activity_game);
		}

		AdView ad = (AdView) findViewById(R.id.adView);
		ad.loadAd(new AdRequest.Builder().build());

		// Create the interstitial.
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getResources().getString(
				R.string.adInterstitialUnitId));

		// Create ad request.
		AdRequest adRequest = new AdRequest.Builder().build();

		// Begin loading your interstitial.
		interstitial.loadAd(adRequest);

		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		editor = mSharedPreferences.edit();

		mDragController = new DragController(this);
		//
		db = new DAO(this);
		db.open();

		levelId = getIntent().getStringExtra("LevelId");

		setupViews();

		Typeface hoboSTD = Typeface.createFromAsset(getAssets(), "fonts/"
				+ getResources().getString(R.string.main_font));

		coinsX = (TextView) findViewById(R.id.coinsX);
		coinsValue = (TextView) findViewById(R.id.coinsValue);

		coinsX.setTypeface(hoboSTD);
		coinsValue.setTypeface(hoboSTD);
		coinsValue.setText(String.valueOf(getCoinsNumber()));

		solution = (ImageButton) findViewById(R.id.solution);
		facebook = (Button) findViewById(R.id.facebook);
		twitter = (Button) findViewById(R.id.twitter);
		facebook.setTypeface(hoboSTD);
		twitter.setTypeface(hoboSTD);

		solution.setOnClickListener(helpClickHandler);
		facebook.setOnClickListener(helpClickHandler);
		twitter.setOnClickListener(helpClickHandler);

		final ImageButton levels = (ImageButton) findViewById(R.id.levels);
		levels.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);

				Intent intent = new Intent(GameActivity.this,
						LevelsActivity.class);

				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				startActivity(intent);

			}
		});

		final ImageButton restart = (ImageButton) findViewById(R.id.restart);
		restart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sou.playSound(R.raw.buttons);

				Intent intent = getIntent();
				intent.putExtra("LevelId", levelId);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				startActivity(intent);

			}
		});

		final ImageButton mode = (ImageButton) findViewById(R.id.mode);

		if (dragMode == 1) {
			mode.setBackgroundResource(R.drawable.button_drag_mode_game);
		} else {
			mode.setBackgroundResource(R.drawable.button_click_mode_game);
		}

		mode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (dragMode == 1) {
					e.putInt("mode", 0);
					e.commit();
				} else {
					e.putInt("mode", 1);
					e.commit();

					sou.playSound(R.raw.buttons);
				}

				Intent intent = getIntent();
				intent.putExtra("LevelId", levelId);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				startActivity(intent);
			}
		});

	}

	// =========================================================================================

	public void showInterstitialAd() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (interstitial.isLoaded()) {
					interstitial.show();
				}

			}
		}, 4000);
	}

	// =========================================================================================

	public boolean onTouch(View v, MotionEvent ev) {
		// If we are configured to start only on a long click, we are not going
		// to handle any events here.
		// if (mLongClickStartsDrag)
		// return false;

		boolean handledHere = false;

		final int action = ev.getAction();

		// In the situation where a long click is not needed to initiate a drag,
		// simply start on the down event.
		if (action == MotionEvent.ACTION_DOWN) {
			handledHere = startDrag(v);
		}

		return handledHere;
	}

	// ==============================================================================

	private View.OnTouchListener dragMatchClickHandler = new View.OnTouchListener() {
		// public void onTouch(View v) {
		//
		// }

		@Override
		public boolean onTouch(View v, MotionEvent arg1) {

			// GameActivity.this.getHint(v.getId());

			if (clickedMatch != null) {
				if (clickedMatch.getTag().equals("match_clicked")) {
					// v.setBackgroundResource(Color.TRANSPARENT);
					((ImageView) clickedMatch).setImageResource(match);
					clickedMatch.setTag("match_on");
					Log.e("clicked", "no");
				} else if (clickedMatch.getTag().equals("match2_clicked")) {
					((ImageView) clickedMatch).setImageResource(match2);
					clickedMatch.setTag("match2_on");
					Log.e("clicked", "no");
				}
				clickedMatch = null;
				cancelClicked = 1;
			}

			if (clickedMatch != v && cancelClicked != 1) {
				clickedMatch = v;
				if (clickedMatch.getTag().equals("match_on")) {
					// v.setBackgroundResource(Color.TRANSPARENT);
					((ImageView) clickedMatch)
							.setImageResource(R.drawable.match_clicked);

					Log.e("clicked", "yes");
					clickedMatch.setTag("match_clicked");
				} else if (clickedMatch.getTag().equals("match2_on")) {
					// v.setBackgroundResource(Color.TRANSPARENT);
					((ImageView) clickedMatch)
							.setImageResource(R.drawable.match2_clicked);
					clickedMatch.setTag("match2_clicked");
					Log.e("clicked", "yes");
				}

			}

			cancelClicked = 0;
			// ((ImageView) v).setImageResource(0);
			return false;
		}
	};

	// ==============================================================================

	private View.OnTouchListener dropMatchClickHandler = new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent arg1) {
			// GameActivity.this.getHint(v.getId());

			Log.e("istouvhed", "yes");
			if (clickedMatch != null) {
				// ImageView droppedMatch = (ImageView) v;
				if (v.getTag().equals("match_off")) {
					((ImageView) v).setImageResource(match);
					v.setTag("match_on");
				} else if (v.getTag().equals("match2_off")) {
					((ImageView) v).setImageResource(match2);
					v.setTag("match2_on");
				}

				v.setOnTouchListener(dragMatchClickHandler);
				if (clickedMatch.getTag().equals("match_clicked")) {
					((ImageView) clickedMatch)
							.setImageResource(R.drawable.match_off);
					clickedMatch.setTag("match_off");

				} else if (clickedMatch.getTag().equals("match2_clicked")) {
					((ImageView) clickedMatch)
							.setImageResource(R.drawable.match2_off);
					clickedMatch.setTag("match2_off");
				}

				clickedMatch.setOnTouchListener(dropMatchClickHandler);

				clickedMatch = null;

				checkAnswer();
			}

			return false;
		}
	};

	// =========================================================================================

	public boolean startDrag(View v) {
		// Let the DragController initiate a drag-drop sequence.
		// I use the dragInfo to pass along the object being dragged.
		// I'm not sure how the Launcher designers do this.
		Object dragInfo = v;
		// int vWidth = v.getWidth() * 2;
		// int vHeight = v.getHeight() * 2;
		// v.setLayoutParams(new LayoutParams(vWidth, vHeight));

		mDragController.startDrag(v, mDragLayer, dragInfo,
				DragController.DRAG_ACTION_MOVE);
		return true;
	}

	// =========================================================================================

	private void setupViews() {

		dragController = mDragController;

		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(dragController);

		c = db.getOneLevel(levelId);

		leWebId = c.getInt(c.getColumnIndex(KEY_WEB_ID));

		x = c.getInt(c.getColumnIndex(KEY_X_VALUE));
		y = c.getInt(c.getColumnIndex(KEY_Y_VALUE));
		r = c.getInt(c.getColumnIndex(KEY_R_VALUE));
		operator = c.getString(c.getColumnIndex(KEY_OPERATOR)).trim();
		moves = c.getInt(c.getColumnIndex(KEY_MOVES));
		leSolution = c.getString(c.getColumnIndex(KEY_SOLUTION)).trim();
		leCompleted = c.getString(c.getColumnIndex(KEY_COMPLETED)).trim();

		Typeface hoboSTD = Typeface.createFromAsset(getAssets(), "fonts/"
				+ getResources().getString(R.string.main_font));

		levelInfo = (TextView) findViewById(R.id.levelInfo);
		levelInfo.setText("Level : "
				+ String.valueOf(c.getInt(c.getColumnIndex(KEY_LEVEL))));
		levelInfo.setTypeface(hoboSTD);

		movesInfo = (TextView) findViewById(R.id.movesInfo);
		movesInfo.setText("0 / " + String.valueOf(moves));
		movesInfo.setTypeface(hoboSTD);
		// movesInfo.setW

		helpMsg = (TextView) findViewById(R.id.helpMsg);
		if (moves == 2) {
			helpMsg.setText("Move 2 matches to fix the equation");
		}
		helpMsg.setTypeface(hoboSTD);

		addXValEvents();
		addOperatorEvents();
		addYValEvents();
		addRValEvents();

	}

	// =========================================================================================

	private String[] getNumMatchesArray(int num) {
		final String[] numMatches;

		switch (num) {
		case 0:
			numMatches = new String[] { "0", "1", "2", "4", "5", "6" };
			break;
		case 1:
			numMatches = new String[] { "2", "5" };
			break;
		case 2:
			numMatches = new String[] { "0", "2", "3", "4", "6" };
			break;
		case 3:
			numMatches = new String[] { "0", "2", "3", "5", "6" };
			break;
		case 4:
			numMatches = new String[] { "1", "2", "3", "5" };
			break;
		case 5:
			numMatches = new String[] { "0", "1", "3", "5", "6" };
			break;
		case 6:
			numMatches = new String[] { "0", "1", "3", "4", "5", "6" };
			break;
		case 7:
			numMatches = new String[] { "0", "2", "5" };
			break;
		case 8:
			numMatches = new String[] { "0", "1", "2", "3", "4", "5", "6" };
			break;
		case 9:
			numMatches = new String[] { "0", "1", "2", "3", "5", "6" };
			break;
		default:
			numMatches = new String[] {};
			break;
		}

		return numMatches;
	}

	// =========================================================================================

	private void addXValEvents() {
		xMatchesIDs = new int[] { R.id.xVal0, R.id.xVal1, R.id.xVal2,
				R.id.xVal3, R.id.xVal4, R.id.xVal5, R.id.xVal6 };
		xMatches = getNumMatchesArray(x);

		for (int i = 0; i <= 6; i++) {
			xValMatchesImgs[i] = (DropSpot) mDragLayer
					.findViewById(xMatchesIDs[i]);

			if (Arrays.asList(xMatches).contains(String.valueOf(i))) {
				xValMatchesImgs[i].setImageResource(matchesDrawables[i]);
				xValMatchesImgs[i].setTag(matchesTags[i]);

				if (dragMode == 1) {
					xValMatchesImgs[i].setOnTouchListener(this);
				} else if (dragMode == 0) {
					xValMatchesImgs[i]
							.setOnTouchListener(dragMatchClickHandler);
				}

			} else {
				if (dragMode == 1) {
					xValMatchesImgs[i].setup(mDragLayer, dragController);
				} else if (dragMode == 0) {
					xValMatchesImgs[i]
							.setOnTouchListener(dropMatchClickHandler);
				}
			}
		}
	}

	// =========================================================================================

	private void addOperatorEvents() {

		operatorMatcheImg = (DropSpot) mDragLayer.findViewById(R.id.operator0);

		if (operator.equals("+")) {
			operatorMatcheImg.setImageResource(matchesDrawables[1]);
			operatorMatcheImg.setTag(matchesTags[1]);
			if (dragMode == 1) {
				operatorMatcheImg.setOnTouchListener(this);
			} else if (dragMode == 0) {
				operatorMatcheImg.setOnTouchListener(dragMatchClickHandler);
			}
		} else {
			if (dragMode == 1) {
				operatorMatcheImg.setup(mDragLayer, dragController);
			} else if (dragMode == 0) {
				operatorMatcheImg.setOnTouchListener(dropMatchClickHandler);
			}
		}

		DropSpot operatorMatcheImg1 = (DropSpot) mDragLayer
				.findViewById(R.id.operator1);
		operatorMatcheImg1.setImageResource(match);

		DropSpot equalMatcheImg = (DropSpot) mDragLayer
				.findViewById(R.id.equal0);
		DropSpot equalMatcheImg1 = (DropSpot) mDragLayer
				.findViewById(R.id.equal1);

		equalMatcheImg.setImageResource(match);
		equalMatcheImg1.setImageResource(match);
	}

	// =========================================================================================

	private void addYValEvents() {
		yMatchesIDs = new int[] { R.id.yVal0, R.id.yVal1, R.id.yVal2,
				R.id.yVal3, R.id.yVal4, R.id.yVal5, R.id.yVal6 };
		yMatches = getNumMatchesArray(y);

		for (int i = 0; i <= 6; i++) {
			yValMatchesImgs[i] = (DropSpot) mDragLayer
					.findViewById(yMatchesIDs[i]);

			if (Arrays.asList(yMatches).contains(String.valueOf(i))) {
				yValMatchesImgs[i].setImageResource(matchesDrawables[i]);
				yValMatchesImgs[i].setTag(matchesTags[i]);
				if (dragMode == 1) {
					yValMatchesImgs[i].setOnTouchListener(this);
				} else if (dragMode == 0) {
					yValMatchesImgs[i]
							.setOnTouchListener(dragMatchClickHandler);
				}
			} else {
				if (dragMode == 1) {
					yValMatchesImgs[i].setup(mDragLayer, dragController);
				} else if (dragMode == 0) {
					yValMatchesImgs[i]
							.setOnTouchListener(dropMatchClickHandler);
				}
			}
		}
	}

	// =========================================================================================

	private void addRValEvents() {
		rMatchesIDs = new int[] { R.id.rVal0, R.id.rVal1, R.id.rVal2,
				R.id.rVal3, R.id.rVal4, R.id.rVal5, R.id.rVal6 };
		rMatches = getNumMatchesArray(r);

		for (int i = 0; i <= 6; i++) {
			rValMatchesImgs[i] = (DropSpot) mDragLayer
					.findViewById(rMatchesIDs[i]);

			if (Arrays.asList(rMatches).contains(String.valueOf(i))) {
				rValMatchesImgs[i].setImageResource(matchesDrawables[i]);
				rValMatchesImgs[i].setTag(matchesTags[i]);
				if (dragMode == 1) {
					rValMatchesImgs[i].setOnTouchListener(this);
				} else if (dragMode == 0) {
					rValMatchesImgs[i]
							.setOnTouchListener(dragMatchClickHandler);
				}
			} else {
				if (dragMode == 1) {
					rValMatchesImgs[i].setup(mDragLayer, dragController);
				} else if (dragMode == 0) {
					rValMatchesImgs[i]
							.setOnTouchListener(dropMatchClickHandler);
				}
			}
		}
	}

	// ==============================================================================

	private View.OnClickListener helpClickHandler = new View.OnClickListener() {
		public void onClick(View v) {
			sou.playSound(R.raw.buttons);
			GameActivity.this.getHelp(v.getId());
		}
	};

	// ==============================================================================

	private void getHelp(final int viewId) {
		int remainCoins = Integer.parseInt(coinsValue.getText().toString());
		if (isHelpUsed(viewId) != 1) {
			boolean noSolutionCoins = (viewId == R.id.solution && remainCoins < 10);
			boolean noFacebookCoins = (viewId == R.id.facebook && remainCoins < 5);
			boolean noTwitterCoins = (viewId == R.id.twitter && remainCoins < 5);

			if (noSolutionCoins || noFacebookCoins || noTwitterCoins) {

				dialog.showDialog(R.layout.purple_dialog, "noCoinsDlg",
						"You don't have enough coins!", null);

			} else {

				String msg = "";

				switch (viewId) {
				case R.id.solution:
					msg = "Show the solution!\nCost : 10 coins";
					break;

				case R.id.facebook:
					msg = "Ask your friends on facebook!\nCost : 5 coins";
					break;

				case R.id.twitter:
					msg = "Ask your friends on twitter!\nCost : 5 coins";
					break;
				}

				globalViewId = viewId;
				dialog.showDialog(R.layout.purple_dialog, "helpDlg", msg, null);

			}

		} else {
			executeHelp(viewId);
		}

	}

	// ==============================================================================

	private int isHelpUsed(int viewId) {
		int state = 0;
		c = db.getHelpState(levelId);
		if (c.getCount() != 0) {
			switch (viewId) {
			case R.id.solution:
				state = c.getInt(c.getColumnIndex("he_solution"));
				break;

			case R.id.facebook:
				state = c.getInt(c.getColumnIndex("he_facebook"));
				break;

			case R.id.twitter:
				state = c.getInt(c.getColumnIndex("he_twitter"));
				break;

			}
		}

		return state;
	}

	// ==============================================================================

	public void executeHelp(int viewId) {

		switch (viewId) {

		case R.id.solution:
			db.updateHelpState(levelId, "he_solution");

			dialog.showDialog(R.layout.red_dialog, "solutionDlg", leSolution,
					null);

			break;

		case R.id.facebook:
			db.updateHelpState(levelId, "he_facebook");

			urlToShare = siteUrl + "site/show_level/" + String.valueOf(leWebId);

			Intent fIntent = new Intent(Intent.ACTION_SEND);
			fIntent.setType("text/plain");
			fIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);

			String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
					+ urlToShare;
			fIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));

			startActivity(fIntent);
			break;

		case R.id.twitter:
			db.updateHelpState(levelId, "he_twitter");

			urlToShare = siteUrl + "site/show_level/" + String.valueOf(leWebId);

			Intent tTntent = new Intent(Intent.ACTION_SEND);

			String tweetUrl = String.format(
					"https://twitter.com/intent/tweet?text=%s",
					urlEncode("Who can fix this equation? " + urlToShare));
			tTntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

			startActivity(tTntent);
			break;

		}

	}

	// =========================================================================================

	public void checkAnswer() {

		sou = new SoundClass(GameActivity.this);
		sou.playSound(R.raw.match);

		DropSpot operatorPlus = (DropSpot) mDragLayer
				.findViewById(R.id.operator0);
		Integer result;

		x = (getNumValue(xMatchesIDs) != 100) ? getNumValue(xMatchesIDs) : null;
		y = (getNumValue(yMatchesIDs) != 100) ? getNumValue(yMatchesIDs) : null;
		r = (getNumValue(rMatchesIDs) != 100) ? getNumValue(rMatchesIDs) : null;

		cMoves = cMoves + 1;

		movesInfo.setText(String.valueOf(cMoves) + " / "
				+ String.valueOf(moves));

		if (cMoves == moves) {
			if (x != null && y != null && r != null) {
				if (operatorPlus.getTag().equals("match2_on")) {
					result = x + y;
				} else {
					result = x - y;
				}

				if (result == r) {
					correctAnswer();
				} else {
					wrongAnswer();
				}
			} else {
				wrongAnswer();
			}
		}

	}

	// =========================================================================================

	private int getNumValue(int[] matchesIDs) {
		String matchesNum = "";
		DropSpot matchesImgs[] = new DropSpot[7];
		for (int i = 0; i <= 6; i++) {
			matchesImgs[i] = (DropSpot) mDragLayer.findViewById(matchesIDs[i]);
			if (matchesImgs[i].getTag().equals(matchesTags[i])) {
				matchesNum = matchesNum.concat(String.valueOf(i));
			}
		}

		return getNumValue(matchesNum);
	}

	// =========================================================================================

	private int getNumValue(String mn) {

		String[] mNumCodes = new String[] { "012456", "25", "02346", "02356",
				"1235", "01356", "013456", "025", "0123456", "012356" };
		for (int i = 0; i <= 9; i++) {
			if (mNumCodes[i].equals(mn)) {
				return i;
			}
		}
		return 100;

	}

	// =========================================================================================

	public void correctAnswer() {
		countPlayingNumForAds();
		sou.playSound(R.raw.right_crowd);

		int nextLevel = db.getNextLevel(levelId);
		if (nextLevel != 0) {
			db.setLevelOpened(String.valueOf(nextLevel));
		}

		if (!leCompleted.equals("1")) {
			db.setLevelCompleted(levelId);
			addCoins();
		} else {
			completedBefore = 1;
		}

		dialog.showDialog(R.layout.correct_dialog, "correctDlg",
				"Congratulations!", String.valueOf(nextLevel));

	}

	// =========================================================================================

	public void wrongAnswer() {
		countPlayingNumForAds();
		sou.playSound(R.raw.wrong_crowd);

		dialog.showDialog(R.layout.red_dialog, "wrongDlg", "Wrong!", null);

	}

	// =========================================================================================

	public void countPlayingNumForAds() {

		editor.putInt("playingNum",
				mSharedPreferences.getInt("playingNum", 0) + 1);
		editor.commit();

		if (mSharedPreferences.getInt("playingNum", 0) >= 5) {
			showInterstitialAd();
			editor.putInt("playingNum", 0);
			editor.commit();
		}

		Log.e("playing",
				String.valueOf(mSharedPreferences.getInt("playingNum", 0)));

	}

	// ==============================================================================

	private void addCoins() {

		db.addTotalCoins();
		coinsValue.setText(String.valueOf(getCoinsNumber()));

	}

	// ==============================================================================

	public int getCoinsNumber() {
		Cursor cCoins = db.getCoinsCount();

		int coinsNumber = cCoins.getInt(cCoins.getColumnIndex("total_coins"))
				- cCoins.getInt(cCoins.getColumnIndex("used_coins"));
		return coinsNumber;
	}

	// ==============================================================================

	private static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.wtf("encoder error", "UTF-8 should always be supported", e);
			throw new RuntimeException("URLEncoder.encode() failed for " + s);
		}
	}

	// ==============================================================================

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(GameActivity.this, LevelsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		startActivity(intent);
	}

	// =========================================================================================

	/**
	 * Send a message to the debug log and display it using Toast.
	 */

	public void trace(String msg) {
		if (!Debugging)
			return;
		Log.d("DragActivity", msg);
	}

} // end class
