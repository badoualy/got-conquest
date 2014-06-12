package com.GoTConquest.AndroidUI;

import gotconquest.core.City;
import gotconquest.core.GameCore;
import gotconquest.core.Kingdom;
import gotconquest.core.Map;
import gotconquest.core.MapParser;
import gotconquest.core.Player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class GameActivity extends Activity {
	public static final int BONUS_INIT_STEP = 10;

	private MapUI ui;
	private GameCore gameCore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.board);
		int requestCode = getIntent().getIntExtra("requestCode", GoTActivity.NEW_GAME_REQUEST_CODE);

		if (requestCode == GoTActivity.LOAD_GAME_REQUEST_CODE)
			loadGame();
		else
			newGame();
	}

	@Override
	public void onPause() {
		ui.cancelTasks();

		if (gameCore.getAlivePlayers() > 1)
			saveGame();
		else
			deleteFile("save");

		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (findViewById(R.id.infos).getVisibility() == View.INVISIBLE)
			super.onBackPressed();
		else
			findViewById(R.id.infos).setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.infosButton) {
			ui.showInfos();
			return true;
		}
		return false;
	}

	public void saveGame() {
		ui.saveCities();

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput("save", Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(gameCore);
			oos.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
			}
		}
	}

	public void newGame() {
		AssetManager assetManager = getAssets();
		InputStream stream = null;
		MapParser parser = null;

		try {
			parser = new MapParser(assetManager.open("westeros.xml"));
			parser.parse();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
				}
			}
		}

		Map map = new Map();
		@SuppressWarnings("unchecked")
		List<Player> players = (List<Player>) getIntent().getExtras().get("players");
		List<Kingdom> kingdoms = parser.getKingdoms();
		List<City> cities = parser.getCities();

		gameCore = new GameCore(map, players, kingdoms, cities);
		for (Player player : players) {
			if (player.isAI()) {
				player.getAI().setGame(gameCore);
			}
		}
		ui = new MapUI(gameCore, this);
		ui.startGame();
	}

	public void loadGame() {
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = openFileInput("save");
			ois = new ObjectInputStream(fis);
			gameCore = (GameCore) ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				Log.e(this.getClass().getName(), e.getLocalizedMessage(), e);
			}
		}

		gameCore.load();

		ui = new MapUI(gameCore, this);
		ui.resume();
	}
}
