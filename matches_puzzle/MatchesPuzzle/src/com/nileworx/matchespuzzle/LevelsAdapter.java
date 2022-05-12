package com.nileworx.matchespuzzle;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelsAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	Context context;
	Typeface tf;

	public LevelsAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		context = a;

		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// ==============================================================================

	public int getCount() {
		return data.size();
	}

	// ==============================================================================

	public Object getItem(int position) {
		return position;
	}

	// ==============================================================================

	public long getItemId(int position) {
		return position;
	}

	// ==============================================================================

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.level_row_grid, null);
		} else {
			vi = convertView;
		}

		ImageView leItemImage = (ImageView) vi.findViewById(R.id.leItemImage);
		TextView leItemText = (TextView) vi.findViewById(R.id.leItemText);

		HashMap<String, String> level = new HashMap<String, String>();
		level = data.get(position);

		Typeface hoboSTD = Typeface.createFromAsset(context.getAssets(), "fonts/" + context.getResources().getString(R.string.main_font));

		if (level.get(LevelsActivity.KEY_OPEN).equals(String.valueOf(1))) {
			leItemImage.setBackgroundResource(R.drawable.button_grid_item_levels);
			leItemText.setVisibility(View.VISIBLE);
			leItemText.setText(level.get(LevelsActivity.KEY_LEVEL));
			leItemText.setTypeface(hoboSTD);
		} else {
			leItemImage.setBackgroundResource(R.drawable.grid_item_off);
			leItemText.setVisibility(View.GONE);
			leItemText.setText("");
		}

		return vi;
	}
}