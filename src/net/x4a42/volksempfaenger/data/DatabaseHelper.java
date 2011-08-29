package net.x4a42.volksempfaenger.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "podcast.db";
	private static final int DB_VERSION = 1;

	public static class Podcast {
		public static final String _TABLE = "podcast";
		public static final String ID = BaseColumns._ID;
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String URL = "url";
		public static final String WEBSITE = "website";

		private static String createSql() {
			return String.format("CREATE TABLE \"%s\" (\n"
					+ "  \"%s\" INTEGER PRIMARY KEY AUTOINCREMENT,\n"
					+ "  \"%s\" TEXT,\n" + "  \"%s\" TEXT,\n"
					+ "  \"%s\" TEXT UNIQUE,\n" + "  \"%s\" TEXT\n" + ")",
					_TABLE, ID, TITLE, DESCRIPTION, URL, WEBSITE);
		}
	}

	public static class Episode {
		public static final String _TABLE = "episode";
		public static final String ID = BaseColumns._ID;
		public static final String PODCAST = "podcast_id";
		public static final String ITEM_ID = "item_id";
		public static final String TITLE = "title";
		public static final String DATE = "date";
		public static final String URL = "url";
		public static final String DESCRIPTION = "description";

		private static String createSql() {
			return String
					.format("CREATE TABLE \"%s\" (\n"
							+ "  \"%s\" INTEGER PRIMARY KEY AUTOINCREMENT,\n"
							+ "  \"%s\" INTEGER REFERENCES \"%s\" (\"%s\") ON DELETE CASCADE,\n"
							+ "  \"%s\" TEXT,\n" + "  \"%s\" TEXT,\n"
							+ "  \"%s\" INTEGER,\n" + "  \"%s\" TEXT,\n"
							+ "  \"%s\" TEXT,\n"
							+ "  UNIQUE (\"%s\", \"%s\")\n" + ")", _TABLE, ID,
							PODCAST, Podcast._TABLE, Podcast.ID, ITEM_ID,
							TITLE, DATE, URL, DESCRIPTION, PODCAST, ITEM_ID);
		}
	}

	public static class Enclosure {
		public static final String _TABLE = "enclosure";
		public static final String ID = BaseColumns._ID;
		public static final String EPISODE = "episode_id";
		public static final String TITLE = "title";
		public static final String URL = "url";
		public static final String MIME = "mime"; // mime type
		public static final String FILE = "file"; // path to file
		public static final String SIZE = "size"; // file size
		public static final String STATE = "state"; // see state constants below
		public static final String DOWNLOAD_ID = "download_id"; // DownloadManager
																// id
		public static final String DURATION_TOTAL = "duration_total"; // total
																		// duration
																		// in
																		// seconds
		public static final String DURATION_LISTENED = "duration_listened"; // listened
																			// time
																			// in
																			// seconds

		public static final int STATE_NEW = 0;
		public static final int STATE_DOWNLOAD_QUEUED = 1;
		public static final int STATE_DOWNLOADING = 2;
		public static final int STATE_DOWNLOADED = 3;
		public static final int STATE_LISTEN_QUEUED = 4;
		public static final int STATE_LISTENING = 5;
		public static final int STATE_LISTENED = 6;
		public static final int STATE_DELETED = 7;

		private static String createSql() {
			return String
					.format("CREATE TABLE \"%s\" (\n"
							+ "  \"%s\" INTEGER PRIMARY KEY AUTOINCREMENT,\n"
							+ "  \"%s\" INTEGER REFERENCES \"%s\" (\"%s\") ON DELETE CASCADE,\n"
							+ "  \"%s\" TEXT,\n" + "  \"%s\" TEXT,\n"
							+ "  \"%s\" TEXT,\n" + "  \"%s\" TEXT,\n"
							+ "  \"%s\" INTEGER,\n"
							+ "  \"%s\" INTEGER DEFAULT %d,\n"
							+ "  \"%s\" INTEGER,\n" + "  \"%s\" INTEGER,\n"
							+ "  \"%s\" INTEGER,\n"
							+ "  UNIQUE (\"%s\", \"%s\")\n" + ")", _TABLE, ID,
							EPISODE, Episode._TABLE, Episode.ID, TITLE, URL,
							MIME, FILE, SIZE, STATE, STATE_NEW, DOWNLOAD_ID,
							DURATION_TOTAL, DURATION_LISTENED, EPISODE, URL);
		}
	}

	Context context;

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	private static DatabaseHelper instance;

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			makeInstance(context);
		}
		return instance;
	}

	private static synchronized void makeInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DbHelper", "onCreate()");
		db.execSQL(Podcast.createSql());
		db.execSQL(Episode.createSql());
		db.execSQL(Enclosure.createSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to do here
	}

}
