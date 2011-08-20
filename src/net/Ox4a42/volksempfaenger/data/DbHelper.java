package net.Ox4a42.volksempfaenger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	static final String DB_NAME = "podcast.db";
	static final int DB_VERSION = 1;
	
	public static class Podcast {
		static final String _TABLE = "podcast";
		static final String ID = BaseColumns._ID;
		static final String TITLE = "title";
		static final String DESCRIPTION = "description";
		static final String URL = "url";
		static final String WEBSITE = "website";
		
		static String createSql() {
			return String.format("CREATE TABLE %s (\n"
					+ "  %s INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT\n"
					+ ")", _TABLE, ID, TITLE, DESCRIPTION, URL, WEBSITE);
		}
	}
	
	public static class Episode {
		static final String _TABLE = "episode";
		static final String ID = BaseColumns._ID;
		static final String PODCAST = "podcast_id";
		static final String TITLE = "title";
		static final String DATE = "date";
		static final String URL = "url";
		static final String DESCRIPTION = "description";
		
		static String createSql() {
			return String.format("CREATE TABLE %s (\n"
					+ "  %s INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  %s INTEGER REFERENCES %s (%s) ON DELETE CASCADE,\n"
					+ "  %s TEXT,\n"
					+ "  %s INTEGER,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT\n"
					+ ")", _TABLE, ID, PODCAST, Podcast._TABLE,
					Podcast.ID, TITLE, DATE, URL, DESCRIPTION);
		}
	}
	
	public static class Enclosure {
		static final String _TABLE = "enclosure";
		static final String ID = BaseColumns._ID;
		static final String EPISODE = "episode_id";
		static final String TITLE = "title";
		static final String URL = "url";
		static final String MIME = "mime";
		static final String LENGTH = "length";
		
		static String createSql() {
			return String.format("CREATE TABLE %s (\n"
					+ "  %s INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  %s INTEGER REFERENCES %s (%s) ON DELETE CASCADE,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT,\n"
					+ "  %s TEXT,\n"
					+ "  %s INTEGER\n"
					+ ")", _TABLE, ID, EPISODE,
					Episode._TABLE, Episode.ID, TITLE, URL, MIME, LENGTH);
		}
	}
	
	Context context;

	public DbHelper(Context context) { //
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DbHelper", "onCreate()");
		db.execSQL(Podcast.createSql());
		db.execSQL(Episode.createSql());
		db.execSQL(Enclosure.createSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
		// Nothing to do here
	}
}
