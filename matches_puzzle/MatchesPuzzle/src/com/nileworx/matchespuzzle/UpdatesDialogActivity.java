package com.nileworx.matchespuzzle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class UpdatesDialogActivity extends Activity {

	CustomDialog dialog;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dialog = new CustomDialog(UpdatesDialogActivity.this);
		dialog.showDialog(R.layout.purple_dialog, "updatesActivityDlg", "There are new " + getIntent().getStringExtra("levelsNum") + " levels. Download?", getIntent().getStringExtra("json"));		
	}

	// ==============================================================================

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();		
	}
	
}
