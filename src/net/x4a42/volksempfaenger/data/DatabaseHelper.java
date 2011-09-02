package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.Utils;
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
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE \"podcast\" (\n");
			sql.append("  \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n");
			sql.append("  \"title\" TEXT,\n");
			sql.append("  \"description\" TEXT,\n");
			sql.append("  \"url\" TEXT UNIQUE,\n");
			sql.append("  \"website\" TEXT\n");
			sql.append(")");
			return sql.toString();
		}

	}

	public static class Episode {

		public static final String _TABLE = "episode";
		public static final String ID = BaseColumns._ID;
		public static final String ITEM_ID = "item_id";
		public static final String PODCAST = "podcast_id";
		public static final String TITLE = "title";
		public static final String DATE = "date";
		public static final String URL = "url";
		public static final String DESCRIPTION = "description";
		public static final String STATE = "state";
		public static final String ENCLOSURE = "enclosure_id";

		public static final int STATE_NEW = 0;
		public static final int STATE_DOWNLOADING = 1;
		public static final int STATE_READY = 2;
		public static final int STATE_LISTENING = 3;
		public static final int STATE_LISTENED = 4;

		private static String createSql() {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE \"episode\" (\n");
			sql.append("  \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n");
			sql.append("  \"item_id\" TEXT,\n");
			sql.append("  \"podcast_id\" INTEGER REFERENCES \"podcast\" (\"_id\") ON DELETE CASCADE,\n");
			sql.append("  \"title\" TEXT,\n");
			sql.append("  \"date\" INTEGER,\n");
			sql.append("  \"url\" TEXT,\n");
			sql.append("  \"description\" TEXT,\n");
			sql.append("  \"state\" INTEGER DEFAULT 0,\n");
			sql.append("  \"enclosure_id\" INTEGER REFERENCES \"enclosure\" (\"_id\"),\n");
			sql.append("  UNIQUE (\"podcast_id\", \"item_id\")\n");
			sql.append(")");
			return sql.toString();
		}

	}

	public static class Enclosure {

		public static final String _TABLE = "enclosure";
		public static final String ID = BaseColumns._ID;
		public static final String EPISODE = "episode_id";
		public static final String TITLE = "title";
		public static final String URL = "url";
		public static final String MIME = "mime";
		public static final String FILE = "file";
		public static final String SIZE = "size";
		public static final String DOWNLOAD_ID = "download_id";
		public static final String DURATION_TOTAL = "duration_total";
		public static final String DURATION_LISTENED = "duration_listened";

		private static String createSql() {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE TABLE \"enclosure\" (\n");
			sql.append("  \"_id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n");
			sql.append("  \"episode_id\" INTEGER REFERENCES \"episode\" (\"_id\") ON DELETE CASCADE,\n");
			sql.append("  \"title\" TEXT,\n");
			sql.append("  \"url\" TEXT,\n");
			sql.append("  \"mime\" TEXT,\n");
			sql.append("  \"file\" TEXT,\n");
			sql.append("  \"size\" INTEGER,\n");
			sql.append("  \"download_id\" INTEGER,\n");
			sql.append("  \"duration_total\" INTEGER,\n");
			sql.append("  \"duration_listened\" INTEGER,\n");
			sql.append("  UNIQUE (\"episode_id\", \"url\")\n");
			sql.append(")");
			return sql.toString();
		}

	}

	public static class ExtendedPodcast extends Podcast {

		public static final String _TABLE = "extended_podcast";
		public static final String NEW_EPISODES = "new_episodes";

		private static String createSql() {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE VIEW \"extended_podcast\" AS\n");
			sql.append("SELECT\n");
			sql.append("  \"podcast\".\"_id\" AS \"_id\",\n");
			sql.append("  \"podcast\".\"title\" AS \"title\",\n");
			sql.append("  \"podcast\".\"description\" AS \"description\",\n");
			sql.append("  \"podcast\".\"url\" AS \"url\",\n");
			sql.append("  \"podcast\".\"website\" AS \"website\",\n");
			sql.append("  (SELECT COUNT(*) FROM \"episode\" ");
			sql.append("WHERE \"episode\".\"podcast_id\" = \"podcast\".\"_id\" ");
			sql.append("AND \"state\" IN (");
			sql.append(Utils.joinArray(new long[] { Episode.STATE_NEW,
					Episode.STATE_DOWNLOADING, Episode.STATE_READY }, ","));
			sql.append(")) AS \"new_episodes\"\n");
			sql.append("FROM \"podcast\"");
			return sql.toString();
		}

	}

	public static class ExtendedEpisode {

		public static final String _TABLE = "extended_episode";
		public static final String ID = "_id";
		public static final String EPISODE_ITEM_ID = "episode_item_id";
		public static final String EPISODE_TITLE = "episode_title";
		public static final String EPISODE_DATE = "episode_date";
		public static final String EPISODE_URL = "episode_url";
		public static final String EPISODE_DESCRIPTION = "episode_description";
		public static final String EPISODE_STATE = "episode_state";
		public static final String PODCAST_ID = "podcast_id";
		public static final String PODCAST_TITLE = "podcast_title";
		public static final String PODCAST_DESCRIPTION = "podcast_description";
		public static final String PODCAST_URL = "podcast_url";
		public static final String PODCAST_WEBSITE = "podcast_website";
		public static final String ENCLOSURE_ID = "enclosure_id";
		public static final String ENCLOSURE_TITLE = "enclosure_title";
		public static final String ENCLOSURE_URL = "enclosure_url";
		public static final String ENCLOSURE_MIME = "enclosure_mime";
		public static final String ENCLOSURE_FILE = "enclosure_file";
		public static final String ENCLOSURE_SIZE = "enclosure_size";
		public static final String DOWNLOAD_ID = "download_id";
		public static final String DURATION_TOTAL = "duration_total";
		public static final String DURATION_LISTENED = "duration_listened";

		private static String createSql() {
			StringBuilder sql = new StringBuilder();
			sql.append("CREATE VIEW \"extended_episode\" AS\n");
			sql.append("SELECT\n");
			sql.append("  \"episode\".\"_id\" AS \"_id\",\n");
			sql.append("  \"episode\".\"item_id\" AS \"episode_item_id\",\n");
			sql.append("  \"episode\".\"title\" AS \"episode_title\",\n");
			sql.append("  \"episode\".\"date\" AS \"episode_date\",\n");
			sql.append("  \"episode\".\"url\" AS \"episode_url\",\n");
			sql.append("  \"episode\".\"description\" AS \"episode_description\",\n");
			sql.append("  \"episode\".\"state\" AS \"episode_state\",\n");
			sql.append("  \"podcast\".\"_id\" AS \"podcast_id\",\n");
			sql.append("  \"podcast\".\"title\" AS \"podcast_title\",\n");
			sql.append("  \"podcast\".\"description\" AS \"podcast_description\",\n");
			sql.append("  \"podcast\".\"url\" AS \"podcast_url\",\n");
			sql.append("  \"podcast\".\"website\" AS \"podcast_website\",\n");
			sql.append("  \"enclosure\".\"_id\" AS \"enclosure_id\",\n");
			sql.append("  \"enclosure\".\"title\" AS \"enclosure_title\",\n");
			sql.append("  \"enclosure\".\"url\" AS \"enclosure_url\",\n");
			sql.append("  \"enclosure\".\"mime\" AS \"enclosure_mime\",\n");
			sql.append("  \"enclosure\".\"file\" AS \"enclosure_file\",\n");
			sql.append("  \"enclosure\".\"size\" AS \"enclosure_size\",\n");
			sql.append("  \"enclosure\".\"download_id\" AS \"download_id\",\n");
			sql.append("  \"enclosure\".\"duration_total\" AS \"duration_total\",\n");
			sql.append("  \"enclosure\".\"duration_listened\" AS \"duration_listened\"\n");
			sql.append("FROM \"episode\"\n");
			sql.append("INNER JOIN \"podcast\" ON \"episode\".\"podcast_id\" = \"podcast\".\"_id\"\n");
			sql.append("LEFT OUTER JOIN \"enclosure\" ON \"episode\".\"enclosure_id\" = \"enclosure\".\"_id\"");
			return sql.toString();
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
		db.execSQL(ExtendedPodcast.createSql());
		db.execSQL(ExtendedEpisode.createSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Nothing to do here
	}

}
