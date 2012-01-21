package net.x4a42.volksempfaenger.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class QueryHelper {

	private static final String PODCAST_WHERE_ID = Podcast._ID + "=?";
	private static final Map<String, String> podcastColumnMap;
	private static final Map<String, String> episodeColumnMap;
	private static final Map<String, String> enclosureColumnMap;

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
						+ "AND status IN (0,1,2)) AS new_episodes");
		podcastColumnMap = Collections.unmodifiableMap(temp);

		// episodeColumnMap
		temp = new HashMap<String, String>();
		temp.put(Episode._ID, "_id");
		temp.put(Episode.DATE, "date");
		temp.put(Episode.DESCRIPTION, "description");
		// temp.put(Episode.DOWNLOAD_DONE, value);
		// temp.put(Episode.DOWNLOAD_FILE, value);
		temp.put(Episode.DOWNLOAD_ID, "download_id");
		// temp.put(Episode.DOWNLOAD_PROGRESS, value);
		// temp.put(Episode.DOWNLOAD_STATUS, value);
		// temp.put(Episode.DOWNLOAD_TOTAL, value);
		temp.put(Episode.DURATION_LISTENED, "duration_listened");
		temp.put(Episode.DURATION_TOTAL, "duration_total");
		temp.put(Episode.ENCLOSURE_ID, "encosure_id");
		// temp.put(Episode.ENCLOSURE_MIME, value);
		// temp.put(Episode.ENCLOSURE_SIZE, value);
		// temp.put(Episode.ENCLOSURE_TITLE, value);
		// temp.put(Episode.ENCLOSURE_URL, value);
		temp.put(Episode.FEED_ITEM_ID, "feed_item_id");
		// temp.put(Episode.PODCAST_DESCRIPTION, value);
		// temp.put(Episode.PODCAST_FEED, value);
		temp.put(Episode.PODCAST_ID, "podcast_id");
		// temp.put(Episode.PODCAST_TITLE, value);
		// temp.put(Episode.PODCAST_WEBSITE, value);
		temp.put(Episode.STATUS, "status");
		temp.put(Episode.TITLE, "title");
		temp.put(Episode.URL, "url");
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

		episodeQueryBuilder = new SQLiteQueryBuilder();
		episodeQueryBuilder.setTables(DatabaseHelper.TABLE_EPISODE);
		episodeQueryBuilder.setProjectionMap(episodeColumnMap);
	}

	public Cursor queryPodcastDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return podcastQueryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor queryPodcastItem(long podcastId, String[] projection) {
		return podcastQueryBuilder.query(dbHelper.getReadableDatabase(),
				projection, PODCAST_WHERE_ID,
				new String[] { String.valueOf(podcastId) }, null, null, null,
				"1");
	}

	public Cursor queryEpisodeDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return episodeQueryBuilder.query(dbHelper.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor queryEpisodeItem(long episodeId, String[] projection) {
		return episodeQueryBuilder.query(dbHelper.getReadableDatabase(),
				projection, PODCAST_WHERE_ID,
				new String[] { String.valueOf(episodeId) }, null, null, null,
				"1");
	}

}
