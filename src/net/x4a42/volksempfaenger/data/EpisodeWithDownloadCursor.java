package net.x4a42.volksempfaenger.data;

import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.BuildConfig;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

public class EpisodeWithDownloadCursor implements Cursor {

	private Cursor databaseCursor;
	private Cursor downloadCursor;
	private int[] positionMap;
	private int databaseWidth;
	private int downloadWidth;
	private int totalWidth;

	public EpisodeWithDownloadCursor(Cursor dbCursor, Cursor dlCursor) {
		{
			String[] from = new String[] { Episode.DOWNLOAD_ID,
					Episode.DOWNLOAD_DONE, /* Episode.DOWNLOAD_FILE, */
					Episode.DOWNLOAD_STATUS, Episode.DOWNLOAD_TOTAL,
					Episode.DOWNLOAD_URI };
			String[] to = new String[] {
					DownloadManager.COLUMN_ID,
					DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR,
					// DownloadManager.COLUMN_LOCAL_FILENAME,
					DownloadManager.COLUMN_STATUS,
					DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
					DownloadManager.COLUMN_LOCAL_URI };
			dlCursor = new ColumnMapCursor(dlCursor, from, to);
		}
		databaseCursor = dbCursor;
		downloadCursor = dlCursor;
		update();
	}

	@Override
	public void close() {
		databaseCursor.close();
		downloadCursor.close();
		databaseCursor = null;
		downloadCursor = null;
		positionMap = null;
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		getCursor(columnIndex).copyStringToBuffer(getColumn(columnIndex),
				buffer);
	}

	@Override
	public void deactivate() {
		databaseCursor.deactivate();
		downloadCursor.deactivate();
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return getCursor(columnIndex).getBlob(getColumn(columnIndex));
	}

	/**
	 * Helper method that returns the column index in the sub-cursor for the
	 * given column in this cursor.
	 * 
	 * @param columnIndex
	 *            Column index in this cursor.
	 * @return Column index in sub-cursor.
	 */
	private int getColumn(int columnIndex) {
		if (columnIndex < databaseWidth) {
			return columnIndex;
		} else {
			return columnIndex - databaseWidth;
		}
	}

	@Override
	public int getColumnCount() {
		return totalWidth;
	}

	@Override
	public int getColumnIndex(String columnName) {
		try {
			return getColumnIndexOrThrow(columnName);
		} catch (IllegalArgumentException e) {
			return -1;
		}
	}

	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		try {
			return databaseCursor.getColumnIndexOrThrow(columnName);
		} catch (IllegalArgumentException e) {
			return downloadCursor.getColumnIndexOrThrow(columnName)
					+ databaseWidth;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < 0) {
			return null;
		} else if (columnIndex < databaseWidth) {
			return databaseCursor.getColumnName(columnIndex);
		} else if (columnIndex < (databaseWidth + downloadWidth)) {
			return downloadCursor.getColumnName(columnIndex - databaseWidth);
		} else {
			return null;
		}
	}

	@Override
	public String[] getColumnNames() {
		String[] db = databaseCursor.getColumnNames();
		String[] dl = downloadCursor.getColumnNames();
		String[] names = new String[db.length + dl.length];
		int n = 0;
		for (int i = 0; i < db.length; i++) {
			names[n++] = db[i];
		}
		for (int i = 0; i < dl.length; i++) {
			names[n++] = dl[i];
		}
		return names;

	}

	@Override
	public int getCount() {
		return databaseCursor.getCount();
	}

	/**
	 * Helper method to return the Cursor a specific column belongs to.
	 * 
	 * @param columnIndex
	 *            Column index in this cursor.
	 * @return Sub-cursor this column belongs to.
	 */
	private Cursor getCursor(int columnIndex) {
		if (columnIndex < databaseWidth) {
			return databaseCursor;
		} else {
			return downloadCursor;
		}
	}

	@Override
	public double getDouble(int columnIndex) {
		return getCursor(columnIndex).getDouble(getColumn(columnIndex));
	}

	@Override
	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		bundle.putAll(databaseCursor.getExtras());
		bundle.putAll(downloadCursor.getExtras());
		return bundle;
	}

	@Override
	public float getFloat(int columnIndex) {
		return getCursor(columnIndex).getFloat(getColumn(columnIndex));
	}

	@Override
	public int getInt(int columnIndex) {
		return getCursor(columnIndex).getInt(getColumn(columnIndex));
	}

	@Override
	public long getLong(int columnIndex) {
		return getCursor(columnIndex).getLong(getColumn(columnIndex));
	}

	@Override
	public int getPosition() {
		return databaseCursor.getPosition();
	}

	@Override
	public short getShort(int columnIndex) {
		return getCursor(columnIndex).getShort(getColumn(columnIndex));
	}

	@Override
	public String getString(int columnIndex) {
		return getCursor(columnIndex).getString(getColumn(columnIndex));
	}

	@Override
	public int getType(int columnIndex) {
		return getCursor(columnIndex).getType(getColumn(columnIndex));
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		return databaseCursor.getWantsAllOnMoveCalls();
	}

	@Override
	public boolean isAfterLast() {
		return databaseCursor.isAfterLast();
	}

	@Override
	public boolean isBeforeFirst() {
		return databaseCursor.isBeforeFirst();
	}

	@Override
	public boolean isClosed() {
		return databaseCursor.isClosed() || downloadCursor.isClosed();
	}

	@Override
	public boolean isFirst() {
		return databaseCursor.isFirst();
	}

	@Override
	public boolean isLast() {
		return databaseCursor.isLast();
	}

	@Override
	public boolean isNull(int columnIndex) {
		Cursor c = getCursor(columnIndex);
		if (c.isBeforeFirst() || c.isAfterLast()) {
			return true;
		} else {
			return c.isNull(getColumn(columnIndex));
		}
	}

	@Override
	public boolean move(int offset) {
		boolean result = databaseCursor.move(offset);
		moveDownloadCursor();
		return result;
	}

	/**
	 * Helper method to move the download cursor to the right row after this
	 * cursor has been moved.
	 */
	private void moveDownloadCursor() {
		if (databaseCursor.isBeforeFirst() || databaseCursor.isAfterLast()) {
			downloadCursor.moveToPosition(-1);
		} else {
			downloadCursor.moveToPosition(positionMap[databaseCursor
					.getPosition()]);
		}
	}

	@Override
	public boolean moveToFirst() {
		boolean result = databaseCursor.moveToFirst();
		moveDownloadCursor();
		return result;
	}

	@Override
	public boolean moveToLast() {
		boolean result = databaseCursor.moveToLast();
		moveDownloadCursor();
		return result;
	}

	@Override
	public boolean moveToNext() {
		boolean result = databaseCursor.moveToNext();
		moveDownloadCursor();
		return result;
	}

	@Override
	public boolean moveToPosition(int position) {
		boolean result = databaseCursor.moveToPosition(position);
		moveDownloadCursor();
		return result;
	}

	@Override
	public boolean moveToPrevious() {
		boolean result = databaseCursor.moveToPrevious();
		moveDownloadCursor();
		return result;
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		databaseCursor.registerContentObserver(observer);
		downloadCursor.registerContentObserver(observer);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		databaseCursor.registerDataSetObserver(observer);
		downloadCursor.registerDataSetObserver(observer);
	}

	@Override
	public boolean requery() {
		if (databaseCursor.requery() && downloadCursor.requery()) {
			update();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Bundle respond(Bundle extras) {
		extras = databaseCursor.respond(extras);
		extras = downloadCursor.respond(extras);
		return extras;
	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		databaseCursor.setNotificationUri(cr, uri);
		downloadCursor.setNotificationUri(cr, uri);
	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		databaseCursor.unregisterContentObserver(observer);
		downloadCursor.unregisterContentObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		databaseCursor.unregisterDataSetObserver(observer);
		downloadCursor.unregisterDataSetObserver(observer);
	}

	/**
	 * Generate mappings from the position in dbCursor to the position in
	 * dlCursor based on the download ID in both cursor.
	 */
	private void update() {
		databaseWidth = databaseCursor.getColumnCount();
		downloadWidth = downloadCursor.getColumnCount();
		totalWidth = databaseWidth + downloadWidth;

		// create mapping: download id -> position in download cursor
		Map<Long, Integer> dlIdToDlPos = new HashMap<Long, Integer>(
				downloadCursor.getCount());
		{
			int idCol = downloadCursor
					.getColumnIndexOrThrow(Episode.DOWNLOAD_ID);
			while (downloadCursor.moveToNext()) {
				dlIdToDlPos.put(downloadCursor.getLong(idCol),
						downloadCursor.getPosition());
			}
		}
		downloadCursor.moveToPosition(-1);

		if (BuildConfig.DEBUG) {
			Log.d(this, "Generated the following dlIdToDlPos:");
			for (Long db : dlIdToDlPos.keySet()) {
				Log.d(this, db + " -> " + dlIdToDlPos.get(db));
			}
		}

		// create mapping: position in database cursor -> position in download
		// cursor
		positionMap = new int[databaseCursor.getCount()];
		{
			int idCol = databaseCursor
					.getColumnIndexOrThrow(Episode.DOWNLOAD_ID);
			Integer p;
			while (databaseCursor.moveToNext()) {
				p = dlIdToDlPos.get(databaseCursor.getLong(idCol));
				positionMap[databaseCursor.getPosition()] = p == null ? -1 : p;
			}
		}
		databaseCursor.moveToPosition(-1);

		if (BuildConfig.DEBUG) {
			Log.d(this, "Generated the following dbToDlMap:");
			for (int i = 0; i < positionMap.length; i++) {
				Log.d(this, i + " -> " + positionMap[i]);
			}
		}
	}
}
