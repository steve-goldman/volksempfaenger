package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.database.Cursor;
import android.database.CursorWrapper;

public class EpisodeCursor extends CursorWrapper {

	private int COLUMN_ID;
	private int COLUMN_DATE;
	private int COLUMN_DESCRIPTION;
	private int COLUMN_DOWNLOAD_DONE;
	private int COLUMN_DOWNLOAD_FILE;
	private int COLUMN_DOWNLOAD_ID;
	private int COLUMN_DOWNLOAD_STATUS;
	private int COLUMN_DOWNLOAD_TOTAL;
	private int COLUMN_DOWNLOAD_URI;
	private int COLUMN_DURATION_LISTENED;
	private int COLUMN_DURATION_TOTAL;
	private int COLUMN_ENCLOSURE_ID;
	private int COLUMN_ENCLOSURE_MIME;
	private int COLUMN_ENCLOSURE_SIZE;
	private int COLUMN_ENCLOSURE_TITLE;
	private int COLUMN_ENCLOSURE_URL;
	private int COLUMN_FEED_ITEM_ID;
	private int COLUMN_PODCAST_DESCRIPTION;
	private int COLUMN_PODCAST_FEED;
	private int COLUMN_PODCAST_ID;
	private int COLUMN_PODCAST_TITLE;
	private int COLUMN_PODCAST_WEBSITE;
	private int COLUMN_STATUS;
	private int COLUMN_TITLE;
	private int COLUMN_URL;

	public EpisodeCursor(Cursor cursor) {
		super(cursor);
		COLUMN_ID = getColumnIndex(Episode._ID);
		COLUMN_DATE = getColumnIndex(Episode.DATE);
		COLUMN_DESCRIPTION = getColumnIndex(Episode.DESCRIPTION);
		COLUMN_DOWNLOAD_DONE = getColumnIndex(Episode.DOWNLOAD_DONE);
		COLUMN_DOWNLOAD_FILE = getColumnIndex(Episode.DOWNLOAD_FILE);
		COLUMN_DOWNLOAD_ID = getColumnIndex(Episode.DOWNLOAD_ID);
		COLUMN_DOWNLOAD_STATUS = getColumnIndex(Episode.DOWNLOAD_STATUS);
		COLUMN_DOWNLOAD_TOTAL = getColumnIndex(Episode.DOWNLOAD_TOTAL);
		COLUMN_DOWNLOAD_URI = getColumnIndex(Episode.DOWNLOAD_URI);
		COLUMN_DURATION_LISTENED = getColumnIndex(Episode.DURATION_LISTENED);
		COLUMN_DURATION_TOTAL = getColumnIndex(Episode.DURATION_TOTAL);
		COLUMN_ENCLOSURE_ID = getColumnIndex(Episode.ENCLOSURE_ID);
		COLUMN_ENCLOSURE_MIME = getColumnIndex(Episode.ENCLOSURE_MIME);
		COLUMN_ENCLOSURE_SIZE = getColumnIndex(Episode.ENCLOSURE_SIZE);
		COLUMN_ENCLOSURE_TITLE = getColumnIndex(Episode.ENCLOSURE_TITLE);
		COLUMN_ENCLOSURE_URL = getColumnIndex(Episode.ENCLOSURE_URL);
		COLUMN_FEED_ITEM_ID = getColumnIndex(Episode.FEED_ITEM_ID);
		COLUMN_PODCAST_DESCRIPTION = getColumnIndex(Episode.PODCAST_DESCRIPTION);
		COLUMN_PODCAST_FEED = getColumnIndex(Episode.PODCAST_FEED);
		COLUMN_PODCAST_ID = getColumnIndex(Episode.PODCAST_ID);
		COLUMN_PODCAST_TITLE = getColumnIndex(Episode.PODCAST_TITLE);
		COLUMN_PODCAST_WEBSITE = getColumnIndex(Episode.PODCAST_WEBSITE);
		COLUMN_STATUS = getColumnIndex(Episode.STATUS);
		COLUMN_TITLE = getColumnIndex(Episode.TITLE);
		COLUMN_URL = getColumnIndex(Episode.URL);
	}

	public long getId() {
		return getLong(COLUMN_ID);
	}

	public long getDate() {
		return getLong(COLUMN_DATE);
	}

	public String getDescription() {
		return getString(COLUMN_DESCRIPTION);
	}

	public long getDownloadDone() {
		return getLong(COLUMN_DOWNLOAD_DONE);
	}

	public String getDownloadFile() {
		return getString(COLUMN_DOWNLOAD_FILE);
	}

	public long getDownloadId() {
		return getLong(COLUMN_DOWNLOAD_ID);
	}

	public int getDownloadStatus() {
		return getInt(COLUMN_DOWNLOAD_STATUS);
	}

	public long getDownloadTotal() {
		return getLong(COLUMN_DOWNLOAD_TOTAL);
	}

	public String getDownloadUri() {
		return getString(COLUMN_DOWNLOAD_URI);
	}

	public long getDurationListened() {
		return getLong(COLUMN_DURATION_LISTENED);
	}

	public long getDurationTotal() {
		return getLong(COLUMN_DURATION_TOTAL);
	}

	public long getEnclosureId() {
		return getLong(COLUMN_ENCLOSURE_ID);
	}

	public String getEnclosureMime() {
		return getString(COLUMN_ENCLOSURE_MIME);
	}

	public long getEnclosureSize() {
		return getLong(COLUMN_ENCLOSURE_SIZE);
	}

	public String getEnclosureTitle() {
		return getString(COLUMN_ENCLOSURE_TITLE);
	}

	public String getEnclosureUrl() {
		return getString(COLUMN_ENCLOSURE_URL);
	}

	public String getFeeddItemId() {
		return getString(COLUMN_FEED_ITEM_ID);
	}

	public String getPodcastDescription() {
		return getString(COLUMN_PODCAST_DESCRIPTION);
	}

	public String getPodcastFeed() {
		return getString(COLUMN_PODCAST_FEED);
	}

	public long getPodcastId() {
		return getLong(COLUMN_PODCAST_ID);
	}

	public String getPodcastTitle() {
		return getString(COLUMN_PODCAST_TITLE);
	}

	public String getPodcastWebsite() {
		return getString(COLUMN_PODCAST_WEBSITE);
	}

	public int getStatus() {
		return getInt(COLUMN_STATUS);
	}

	public String getTitle() {
		return getString(COLUMN_TITLE);
	}

	public String getUrl() {
		return getString(COLUMN_URL);
	}

}
