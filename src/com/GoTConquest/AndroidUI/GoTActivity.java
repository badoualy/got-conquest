package com.GoTConquest.AndroidUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class GoTActivity extends Activity {
	public final static int OPTION_REQUEST_CODE = 10;
	public final static int NEW_GAME_REQUEST_CODE = 11;
	public final static int LOAD_GAME_REQUEST_CODE = 12;
	public final static int REPLAY_RESULT_CODE = 14;
	public final static int STOP_RESULT_CODE = 15;
	public final static int STATS_RESULT_CODE = 16;
	public final static String WIKI_URL = "http://en.wikipedia.org/wiki/Risk_%28game%29";
	public final static String FACEBOOK_URL = "fb.com/WesterosConquest";
	public final static String TWITTER_URL = "twitter.com/badoualy";
	public final static String HTTP_PREFIX = "http://";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		checkVersion();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == GoTActivity.STATS_RESULT_CODE)
			showStats();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.statsButton) {
			showStats();
			return true;
		} else if (item.getItemId() == R.id.aboutButton) {
			showWebsites();
			return true;
		}
		return false;
	}

	private void checkVersion() {
		BufferedReader reader = null;
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int versionNumber = pinfo.versionCode;

			if (!getFileStreamPath("version").exists()) {
				onUpgradeVersion(versionNumber);
				return;
			}

			reader = new BufferedReader(new FileReader(getFileStreamPath("version")));
			int lastVersion = Integer.parseInt(reader.readLine());

			if (versionNumber > lastVersion)
				onUpgradeVersion(versionNumber);

			reader.close();
		} catch (Exception e) {
		}
	}

	private void onUpgradeVersion(int versionNumber) throws Exception {
		deleteFile("save");
		deleteFile("version");

		BufferedWriter writer = new BufferedWriter(new FileWriter(openFileOutput("version", MODE_PRIVATE).getFD()));
		writer.write(Integer.toString(versionNumber));

		writer.close();
		showWebsites();
	}

	public void newGame(View v) {
		File file = getFileStreamPath("save");
		if (file.exists()) {
			final Activity activity = this;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.saveexist)).setMessage(getString(R.string.saveexistdetails))
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(activity, GameActivity.class);
							intent.putExtra("requestCode", GoTActivity.LOAD_GAME_REQUEST_CODE);
							startActivityForResult(intent, LOAD_GAME_REQUEST_CODE);
						}
					}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(activity, OptionActivity.class);
							startActivityForResult(intent, OPTION_REQUEST_CODE);
						}
					});
			builder.create().show();
		} else {
			Intent intent = new Intent(this, OptionActivity.class);
			startActivityForResult(intent, OPTION_REQUEST_CODE);
		}
	}

	public void howToPlay(View v) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(WIKI_URL));
		startActivity(i);
	}

	public void showStats() {
		Intent intent = new Intent(this, StatisticsActivity.class);
		startActivityForResult(intent, LOAD_GAME_REQUEST_CODE);
	}

	public void showWebsites() {
		String message = getString(R.string.websites) + "\n" + FACEBOOK_URL + "\n" + TWITTER_URL;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.checklinks));
		builder.setMessage(message);
		builder.setPositiveButton("Ok", null);
		builder.setNeutralButton("Facebook", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(HTTP_PREFIX + FACEBOOK_URL));
				startActivity(i);
			}
		});
		builder.setNegativeButton("Twitter", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(HTTP_PREFIX + TWITTER_URL));
				startActivity(i);
			}
		});
		builder.create().show();
	}
}