package com.GoTConquest.AndroidUI;

import gotconquest.ai.WeakAI;
import gotconquest.core.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class OptionActivity extends Activity implements OnSeekBarChangeListener, OnItemSelectedListener, Serializable {
	private static final long serialVersionUID = -5645652977296242335L;

	public static final String PREFERENCES_FILENAME = "preferences";

	private transient LinearLayout playersOptionLayouts;
	private int playersCount = 8;
	private String[] playersNames = new String[8];
	private int[] playersHouses = { 0, 1, 2, 3, 4, 5, 6, 7 };
	private boolean[] bots = { false, false, false, false, false, false, false, false };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);

		playersOptionLayouts = (LinearLayout) findViewById(R.id.playersOptionLayouts);

		((SeekBar) findViewById(R.id.playerSeeker)).setOnSeekBarChangeListener(this);

		LinearLayout layout;
		for (int i = 0; i < 8; i++) {
			layout = ((LinearLayout) playersOptionLayouts.getChildAt(i));
			((Spinner) layout.getChildAt(2)).setOnItemSelectedListener(this);

			playersNames[i] = getString(R.string.player) + " " + (i + 1);
		}

		loadPreferences();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != GoTActivity.REPLAY_RESULT_CODE) {
			setResult(resultCode);
			finish();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		progress += 2;
		((TextView) findViewById(R.id.playersCount)).setText("(" + progress + ")");

		for (int i = 2; i < 8; ++i) {
			if (i >= progress)
				playersOptionLayouts.getChildAt(i).setVisibility(View.INVISIBLE);
			else
				playersOptionLayouts.getChildAt(i).setVisibility(View.VISIBLE);
		}

		playersCount = progress;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		int player = playersOptionLayouts.indexOfChild((View) parent.getParent());

		playersHouses[player] = pos;

		for (int i = 0; i < 8; ++i) {
			if (i != player && playersHouses[i] == pos) {
				playersHouses[i] = getFirstFreeHouse();
				((Spinner) ((LinearLayout) playersOptionLayouts.getChildAt(i)).getChildAt(2)).setSelection(playersHouses[i]);
			}
		}
	}

	public void onClick(View v) {
		ArrayList<Player> players = new ArrayList<Player>(playersCount);
		String[] colors = getResources().getStringArray(R.array.houses_colors);
		LinearLayout field;
		int colorCode;

		for (int i = 0; i < playersCount; i++) {
			field = ((LinearLayout) playersOptionLayouts.getChildAt(i));
			playersNames[i] = ((EditText) field.getChildAt(1)).getText().toString();
			bots[i] = ((CheckBox) field.getChildAt(0)).isChecked();
			colorCode = Color.parseColor(colors[playersHouses[i]]);

			Player player = new Player(playersNames[i], playersHouses[i], colorCode);
			players.add(player);
			if (bots[i]) {
				player.setAI(new WeakAI(player));
			}
		}

		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("players", players);
		startActivityForResult(intent, GoTActivity.NEW_GAME_REQUEST_CODE);

		savePreferences();
	}

	private int getFirstFreeHouse() {
		List<Integer> colors = new ArrayList<Integer>(8);

		for (int i = 0; i < 8; i++)
			colors.add(i);

		for (int i = 0; i < 8; i++)
			colors.remove((Integer) playersHouses[i]);

		return colors.get(0);
	}

	private void updateLayouts() {
		LinearLayout layout;
		((SeekBar) findViewById(R.id.playerSeeker)).setProgress(playersCount - 2);
		for (int i = 0; i < 8; i++) {
			layout = (LinearLayout) playersOptionLayouts.getChildAt(i);
			((CheckBox) (layout).getChildAt(0)).setChecked(bots[i]);
			((EditText) (layout).getChildAt(1)).setText(playersNames[i]);
			((Spinner) (layout).getChildAt(2)).setSelection(playersHouses[i]);
		}
	}

	private void savePreferences() {
		try {
			new File(getCacheDir(), OptionActivity.PREFERENCES_FILENAME).delete();
			File cacheFile = new File(getCacheDir(), OptionActivity.PREFERENCES_FILENAME);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
			oos.writeObject(this);

			oos.flush();
			oos.close();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
		}
	}

	private void loadPreferences() {
		try {
			File cacheFile = new File(getCacheDir(), OptionActivity.PREFERENCES_FILENAME);
			if (cacheFile.exists()) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
				OptionActivity oa = (OptionActivity) ois.readObject();

				playersNames = oa.getPlayersNames();
				playersHouses = oa.getPlayersHouses();
				playersCount = oa.getPlayersCount();
				bots = oa.getBots();
				ois.close();
			}
			updateLayouts();
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
		}
	}

	public int getPlayersCount() {
		return playersCount;
	}

	public String[] getPlayersNames() {
		return playersNames;
	}

	public int[] getPlayersHouses() {
		return playersHouses;
	}

	public boolean[] getBots() {
		return bots;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
