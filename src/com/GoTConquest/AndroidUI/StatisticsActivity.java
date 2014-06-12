package com.GoTConquest.AndroidUI;

import gotconquest.core.Statistics.Line;
import gotconquest.database.StatisticsDAO;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StatisticsActivity extends Activity implements OnItemSelectedListener {
	private StatisticsDAO dao;
	private String[] players;
	private TableLayout tableLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistics);

		dao = new StatisticsDAO(this);
		players = dao.getPlayers();

		tableLayout = (TableLayout) findViewById(R.id.statisticsTable);
		initTable();
		Spinner spinner = (Spinner) findViewById(R.id.playersStatsSpinner);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, players);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);

		spinner.setOnItemSelectedListener(this);
	}

	private void initTable() {
		for (int i = 1; i < 8; i++)
			((TextView) ((TableRow) tableLayout.getChildAt(i)).getChildAt(0)).setText("" + (i + 1));
	}

	@Override
	public void finish() {
		dao.close();
		super.finish();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		Line[] lines = dao.getStats(players[pos]).getLines();
		TableRow row = null;
		String victories, defeats, nbGamePlayed;

		for (int i = 0; i < lines.length; i++) {
			row = (TableRow) tableLayout.getChildAt(i + 1);
			if (lines[i] != null) {
				victories = "" + lines[i].nbVictory;
				defeats = "" + lines[i].nbDefeat;
				nbGamePlayed = "" + lines[i].nbGamePlayed;
			} else {
				victories = "-";
				defeats = victories;
				nbGamePlayed = victories;
			}

			((TextView) row.getChildAt(1)).setText(victories);
			((TextView) row.getChildAt(2)).setText(defeats);
			((TextView) row.getChildAt(3)).setText(nbGamePlayed);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}