package gotconquest.database;

import gotconquest.core.GameCore;
import gotconquest.core.Player;
import gotconquest.core.Statistics;

import java.io.Closeable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StatisticsDAO implements Closeable {
	GoTSQLiteOpenHelper dbHelper;
	SQLiteDatabase db = null;

	public StatisticsDAO(Context context) {
		dbHelper = new GoTSQLiteOpenHelper(context);
	}

	public void addGame(GameCore gameCore) {
		db = dbHelper.getWritableDatabase();
		ContentValues gameValues = new ContentValues();
		gameValues.put("map_name", gameCore.getMap().getName());
		gameValues.put("nb_player", gameCore.getConstPlayers().size());
		gameValues.put("nb_turn", gameCore.getTurnCount());
		long gameId = db.insert("Game", null, gameValues);

		ContentValues playerValues;
		for (Player player : gameCore.getConstPlayers()) {
			if (player.isAI())
				continue;
			playerValues = new ContentValues();
			playerValues.put("name", player.getName());
			playerValues.put("won", player.hasWon());
			playerValues.put("game_id", gameId);
			db.insert("Player", null, playerValues);
		}

		db.close();
	}

	public String[] getPlayers() {
		db = dbHelper.getReadableDatabase();
		String query = "SELECT DISTINCT name FROM Player";
		Cursor cursor = db.rawQuery(query, null);

		int i = 0;
		String[] players = new String[cursor.getCount()];
		while (cursor.moveToNext())
			players[i++] = cursor.getString(0);
		cursor.close();

		db.close();
		return players;
	}

	public Statistics getStats(String player) {
		db = dbHelper.getReadableDatabase();
		String query = "SELECT g.nb_player, count(1) FROM Game g JOIN Player p USING (game_id) WHERE p.name = ? GROUP BY nb_player";
		Cursor cursor = db.rawQuery(query, new String[] { player });

		Statistics stats = new Statistics(player, cursor.getCount());

		while (cursor.moveToNext()) {
			int nbPlayer = cursor.getInt(0);
			int nbGamePlayed = cursor.getInt(1);
			int nbVictory = getNumberOfVictory(player, nbPlayer);
			int nbDefeat = nbGamePlayed - nbVictory;
			stats.addLine(nbPlayer, nbGamePlayed, nbVictory, nbDefeat);
		}
		cursor.close();

		db.close();
		return stats;
	}

	private int getNumberOfVictory(String player, int nbPlayer) {
		String query = "SELECT count(1) FROM Game g JOIN Player p USING (game_id) WHERE p.name = ? AND p.won = 1 AND g.nb_player = ?";
		Cursor cursor = db.rawQuery(query, new String[] { player, "" + nbPlayer });
		cursor.moveToFirst();
		int nbVictory = cursor.getInt(0);
		cursor.close();
		return nbVictory;
	}

	@Override
	public void close() {
		if (db.isOpen())
			db.close();
		dbHelper.close();
	}
}
