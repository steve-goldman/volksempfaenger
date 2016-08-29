package net.x4a42.volksempfaenger.data;

import net.x4a42.volksempfaenger.data.Columns.Enclosure;
import android.database.Cursor;

public class EnclosureCursor extends ExtendedCursorWrapper {

	private int COLUMN_ID;
	private int COLUMN_EPISODE_ID;
	private int COLUMN_MIME;
	private int COLUMN_SIZE;
	private int COLUMN_TITLE;
	private int COLUMN_URL;

	public EnclosureCursor(Cursor cursor) {
		super(cursor);
		COLUMN_ID = getColumnIndex(Enclosure._ID);
		COLUMN_EPISODE_ID = getColumnIndex(Enclosure.EPISODE_ID);
		COLUMN_MIME = getColumnIndex(Enclosure.MIME);
		COLUMN_SIZE = getColumnIndex(Enclosure.SIZE);
		COLUMN_TITLE = getColumnIndex(Enclosure.TITLE);
		COLUMN_URL = getColumnIndex(Enclosure.URL);
	}

	public long getId() {
		return getLong(COLUMN_ID);
	}

	public long getEpisodeId() {
		return getLong(COLUMN_EPISODE_ID);
	}

	public String getMime() {
		return getString(COLUMN_MIME);
	}

	public long getSize() {
		return getLong(COLUMN_SIZE);
	}

	public String getTitle() {
		return getString(COLUMN_TITLE);
	}

	public String getUrl() {
		return getString(COLUMN_URL);
	}

}
