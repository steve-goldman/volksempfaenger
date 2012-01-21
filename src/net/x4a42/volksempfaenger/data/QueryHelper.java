package net.x4a42.volksempfaenger.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.data.Columns.Podcast;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class QueryHelper {

	private static Map<String, String> podcastColumnMap;
	private static Map<String, String> episodeColumnMap;
	private static Map<String, String> enclosureColumnMap;

	static {
		Map<String, String> temp;

		// podcastColumnMap
		temp = new HashMap<String, String>();
		temp.put(Podcast._ID, "_id");
		temp.put(Podcast.TITLE, "title");
		temp.put(Podcast.DESCRIPTION, "description");
		temp.put(Podcast.FEED, "feed");
		temp.put(Podcast.WEBSITE, "website");
		temp.put(Podcast.LAST_UPDATE, "last_update");
		temp.put(Podcast.NEW_EPISODES,
				"(SELECT COUNT(*) FROM episode WHERE episode.podcast_id = podcast._id "
						+ "AND state IN (0,1,2)) AS new_episodes");
		podcastColumnMap = Collections.unmodifiableMap(temp);

		// episodeColumnMap
		temp = new HashMap<String, String>();
		// TODO
		episodeColumnMap = Collections.unmodifiableMap(temp);

		// enclosureColumnMap
		temp = new HashMap<String, String>();
		// TODO
		enclosureColumnMap = Collections.unmodifiableMap(temp);
	}

	private DatabaseHelper dbHelper;
	private SQLiteQueryBuilder podcastQueryBuilder;
	private SQLiteQueryBuilder episodeQueryBuilder;
	private SQLiteQueryBuilder enclosureQueryBuilder;

	protected QueryHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;

		podcastQueryBuilder = new SQLiteQueryBuilder();
		podcastQueryBuilder.setTables(DatabaseHelper.TABLE_PODCAST);
		podcastQueryBuilder.setProjectionMap(podcastColumnMap);
	}

	public Cursor queryPodcastDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		return podcastQueryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
	}

}
