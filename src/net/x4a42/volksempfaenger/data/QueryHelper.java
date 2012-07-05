package net.x4a42.volksempfaenger.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class QueryHelper extends ContentProviderHelper {

	private static final String EPISODE_JOIN_PODCAST = "INNER JOIN podcast ON episode.podcast_id = podcast._id";
	private static final String EPISODE_JOIN_ENCLOSURE = "LEFT OUTER JOIN enclosure ON episode.enclosure_id = enclosure._id";
	private static final String EPISODE_JOIN_DOWNLOAD = "LEFT OUTER JOIN DownloadManager.download download ON episode.download_id = download._id";
	private static final Map<String, String> podcastColumnMap;
	private static final Map<String, String> episodeColumnMap;
	private static final Map<String, String> enclosureColumnMap;

	static {
		Map<String, String> temp;

		// podcastColumnMap
		temp = new HashMap<String, String>();
		temp.put(Podcast._ID, "_id AS " + Podcast._ID);
		temp.put(Podcast.TITLE, "title AS " + Podcast.TITLE);
		temp.put(Podcast.DESCRIPTION, "description AS " + Podcast.DESCRIPTION);
		temp.put(Podcast.FEED, "feed AS " + Podcast.FEED);
		temp.put(Podcast.WEBSITE, "website AS " + Podcast.WEBSITE);
		temp.put(Podcast.LAST_UPDATE, "last_update AS " + Podcast.LAST_UPDATE);
		temp.put(Podcast.NEW_EPISODES,
				"(SELECT COUNT(*) FROM episode WHERE episode.podcast_id = podcast._id "
						+ "AND status IN (0,1,2)) AS " + Podcast.NEW_EPISODES);
		podcastColumnMap = Collections.unmodifiableMap(temp);

		// episodeColumnMap
		temp = new HashMap<String, String>();
		temp.put(Episode._ID, "episode._id AS " + Episode._ID);
		temp.put(Episode.DATE, "episode.date AS " + Episode.DATE);
		temp.put(Episode.DESCRIPTION, "episode.description AS "
				+ Episode.DESCRIPTION);
		temp.put(Episode.DOWNLOAD_ID, "episode.download_id AS "
				+ Episode.DOWNLOAD_ID);
		temp.put(Episode.DOWNLOAD_LOCAL_FILENAME, "download.local_filename AS "
				+ Episode.DOWNLOAD_LOCAL_FILENAME);
		temp.put(Episode.DOWNLOAD_MEDIAPROVIDER_URI,
				"download.mediaprovider_uri AS "
						+ Episode.DOWNLOAD_MEDIAPROVIDER_URI);
		temp.put(Episode.DOWNLOAD_TITLE, "download.title AS "
				+ Episode.DOWNLOAD_TITLE);
		temp.put(Episode.DOWNLOAD_DESCRIPTION, "download.description AS "
				+ Episode.DOWNLOAD_DESCRIPTION);
		temp.put(Episode.DOWNLOAD_URI, "download.uri AS "
				+ Episode.DOWNLOAD_URI);
		temp.put(Episode.DOWNLOAD_STATUS, "download.status AS "
				+ Episode.DOWNLOAD_STATUS);
		temp.put(Episode.DOWNLOAD_MEDIA_TYPE, "download.media_type AS "
				+ Episode.DOWNLOAD_MEDIA_TYPE);
		temp.put(Episode.DOWNLOAD_TOTAL_SIZE_BYTES, "download.total_size AS "
				+ Episode.DOWNLOAD_TOTAL_SIZE_BYTES);
		temp.put(Episode.DOWNLOAD_LAST_MODIFIED_TIMESTAMP,
				"download.last_modified_timestamp AS "
						+ Episode.DOWNLOAD_LAST_MODIFIED_TIMESTAMP);
		temp.put(Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR,
				"download.bytes_so_far AS "
						+ Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR);
		temp.put(Episode.DOWNLOAD_LOCAL_URI, "download.local_uri AS "
				+ Episode.DOWNLOAD_LOCAL_URI);
		temp.put(Episode.DOWNLOAD_REASON, "download.reason AS "
				+ Episode.DOWNLOAD_REASON);
		temp.put(Episode.DURATION_LISTENED, "episode.duration_listened AS "
				+ Episode.DURATION_LISTENED);
		temp.put(Episode.DURATION_TOTAL, "episode.duration_total AS "
				+ Episode.DURATION_TOTAL);
		temp.put(Episode.ENCLOSURE_ID, "episode.enclosure_id AS "
				+ Episode.ENCLOSURE_ID);
		temp.put(Episode.ENCLOSURE_MIME, "enclosure.mime AS "
				+ Episode.ENCLOSURE_MIME);
		temp.put(Episode.ENCLOSURE_SIZE, "enclosure.size AS "
				+ Episode.ENCLOSURE_SIZE);
		temp.put(Episode.ENCLOSURE_TITLE, "enclosure.title AS "
				+ Episode.ENCLOSURE_TITLE);
		temp.put(Episode.ENCLOSURE_URL, "enclosure.url AS "
				+ Episode.ENCLOSURE_URL);
		temp.put(Episode.FEED_ITEM_ID, "episode.feed_item_id AS "
				+ Episode.FEED_ITEM_ID);
		temp.put(Episode.PODCAST_DESCRIPTION, "podcast.description AS "
				+ Episode.PODCAST_DESCRIPTION);
		temp.put(Episode.PODCAST_FEED, "podcast.feed AS "
				+ Episode.PODCAST_FEED);
		temp.put(Episode.PODCAST_ID, "episode.podcast_id AS "
				+ Episode.PODCAST_ID);
		temp.put(Episode.PODCAST_TITLE, "podcast.title AS "
				+ Episode.PODCAST_TITLE);
		temp.put(Episode.PODCAST_WEBSITE, "podcast.website AS "
				+ Episode.PODCAST_WEBSITE);
		temp.put(Episode.STATUS, "episode.status AS " + Episode.STATUS);
		temp.put(Episode.TITLE, "episode.title AS " + Episode.TITLE);
		temp.put(Episode.URL, "episode.url AS " + Episode.URL);
		temp.put(Episode.HASH, "episode.hash AS " + Episode.HASH);
		episodeColumnMap = Collections.unmodifiableMap(temp);

		// enclosureColumnMap
		temp = new HashMap<String, String>();
		temp.put(Enclosure._ID, "enclosure._id AS " + Enclosure._ID);
		temp.put(Enclosure.EPISODE_ID, "enclosure.episode_id AS "
				+ Enclosure.EPISODE_ID);
		temp.put(Enclosure.MIME, "enclosure.mime AS " + Enclosure.MIME);
		temp.put(Enclosure.SIZE, "enclosure.size AS " + Enclosure.SIZE);
		temp.put(Enclosure.TITLE, "enclosure.title AS " + Enclosure.TITLE);
		temp.put(Enclosure.URL, "enclosure.url AS " + Enclosure.URL);
		enclosureColumnMap = Collections.unmodifiableMap(temp);
	}

	private SQLiteQueryBuilder podcastQueryBuilder;
	private ThreadLocal<SQLiteQueryBuilder> episodeQueryBuilder;
	private SQLiteQueryBuilder enclosureQueryBuilder;

	protected QueryHelper(DatabaseHelper dbHelper) {
		super(dbHelper);

		podcastQueryBuilder = new SQLiteQueryBuilder();
		podcastQueryBuilder.setTables(PODCAST_TABLE);
		podcastQueryBuilder.setProjectionMap(podcastColumnMap);

		episodeQueryBuilder = new ThreadLocal<SQLiteQueryBuilder>() {

			@Override
			protected SQLiteQueryBuilder initialValue() {
				SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
				builder.setProjectionMap(episodeColumnMap);
				return builder;
			}

		};

		enclosureQueryBuilder = new SQLiteQueryBuilder();
		enclosureQueryBuilder.setTables(ENCLOSURE_TABLE);
		enclosureQueryBuilder.setProjectionMap(enclosureColumnMap);
	}

	public Cursor queryPodcastDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return podcastQueryBuilder.query(getReadableDatabase(), projection,
				selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor queryPodcastItem(long podcastId, String[] projection) {
		return queryPodcastDir(projection, PODCAST_WHERE_ID,
				selectionArray(podcastId), null);
	}

	public Cursor queryEpisodeDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// intern all strings in the projection array
		Utils.internStringArray(projection);

		boolean joinPodcast = false;
		boolean joinEnclosure = false;
		boolean joinDownload = false;

		// find out which tables must be joined
		for (String col : projection) {
			if (!joinPodcast
					&& (col == Episode.PODCAST_DESCRIPTION
							|| col == Episode.PODCAST_FEED
							|| col == Episode.PODCAST_TITLE || col == Episode.PODCAST_WEBSITE)) {
				joinPodcast = true;
			} else if (!joinEnclosure
					&& (Episode.ENCLOSURE_MIME.equals(col)
							|| Episode.ENCLOSURE_SIZE.equals(col)
							|| Episode.ENCLOSURE_TITLE.equals(col) || col == Episode.ENCLOSURE_URL)) {
				joinEnclosure = true;
			} else if (col == Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR
					|| col == Episode.DOWNLOAD_STATUS
					|| col == Episode.DOWNLOAD_TOTAL_SIZE_BYTES
					|| col == Episode.DOWNLOAD_LOCAL_URI) {
				joinDownload = true;
			}
		}

		StringBuilder stringBuilder = new StringBuilder(
				DatabaseHelper.TABLE_EPISODE);

		// join podcast table if needed
		if (joinPodcast) {
			stringBuilder.append(' ');
			stringBuilder.append(EPISODE_JOIN_PODCAST);
		}

		// join enclosure table if needed
		if (joinEnclosure) {
			stringBuilder.append(' ');
			stringBuilder.append(EPISODE_JOIN_ENCLOSURE);
		}

		// join download table if needed
		if (joinDownload) {
			stringBuilder.append(' ');
			stringBuilder.append(EPISODE_JOIN_DOWNLOAD);
		}

		// query the database
		SQLiteQueryBuilder builder = episodeQueryBuilder.get();
		builder.setTables(stringBuilder.toString());
		return builder.query(getReadableDatabase(), projection, selection,
				selectionArgs, null, null, sortOrder);
	}

	public Cursor queryEpisodeItem(long episodeId, String[] projection) {
		return queryEpisodeDir(projection, EPISODE_WHERE_ID,
				selectionArray(episodeId), null);
	}

	public Cursor queryEnclosureDir(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return enclosureQueryBuilder.query(getReadableDatabase(), projection,
				selection, selectionArgs, null, null, sortOrder);
	}

	public Cursor queryEnclosureItem(long enclosureId, String[] projection) {
		return queryPodcastDir(projection, ENCLOSURE_WHERE_ID,
				selectionArray(enclosureId), null);
	}

}
