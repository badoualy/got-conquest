package gotconquest.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GoTSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "statistics.db";
	public static final int DB_VERSION = 1;

	public GoTSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Game(game_id integer primary key autoincrement, map_name text, nb_player integer, nb_turn integer);");
		db.execSQL("CREATE TABLE Player(name text, won boolean, game_id integer);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
