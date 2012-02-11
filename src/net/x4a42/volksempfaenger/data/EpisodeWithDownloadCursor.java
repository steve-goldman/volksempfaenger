package net.x4a42.volksempfaenger.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.x4a42.volksempfaenger.data.Columns.Episode;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class EpisodeWithDownloadCursor implements Cursor {

	private static final String TAG = "EpisodeWithDownloadCursor";
	private static final boolean DEBUG = false;
	private static Map<String, String> dlColumnMap;
	private Map<Integer, Integer> dbToDlMap;
	private String[] dlColumns;
	private Cursor dbCursor;
	private Cursor dlCursor;
	private int dbDownloadId;
	private int dlDownloadId;

	static {
		Map<String, String> temp = new HashMap<String, String>();
		temp.put(Episode.DOWNLOAD_DONE,
				DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		temp.put(Episode.DOWNLOAD_FILE, DownloadManager.COLUMN_LOCAL_FILENAME);
		temp.put(Episode.DOWNLOAD_STATUS, DownloadManager.COLUMN_STATUS);
		temp.put(Episode.DOWNLOAD_TOTAL,
				DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		temp.put(Episode.DOWNLOAD_URI, DownloadManager.COLUMN_LOCAL_URI);
		dlColumnMap = Collections.unmodifiableMap(temp);
	}

	public EpisodeWithDownloadCursor(Cursor dbCursor, Cursor dlCursor) {
		this.dbCursor = dbCursor;
		this.dlCursor = dlCursor;
		this.dlColumns = new String[dlColumnMap.size()];
		dlColumnMap.keySet().toArray(dlColumns);

		dbDownloadId = dbCursor.getColumnIndex(Episode.DOWNLOAD_ID);
		dlDownloadId = dbCursor.getColumnIndex(DownloadManager.COLUMN_ID);

		updateIdMappings();
	}

	/**
	 * Generate mappings from the position in dbCursor to the position in
	 * dlCursor based on the download ID in both cursor.
	 */
	private void updateIdMappings() {
		int length = dlCursor.getCount();
		Map<Long, Integer> dlIdToDlPos = new HashMap<Long, Integer>(length);
		while (dlCursor.moveToNext()) {
			dlIdToDlPos.put(dlCursor.getLong(dlDownloadId),
					dlCursor.getPosition());
		}
		dlCursor.moveToPosition(-1);

		if (DEBUG) {
			Log.d(TAG, "Generated the following dlIdToDlPos:");
			for (Long db : dlIdToDlPos.keySet()) {
				Log.d(TAG, db + " -> " + dlIdToDlPos.get(db));
			}
		}

		Integer dlPos;
		HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>(length);
		while (dbCursor.moveToNext()) {
			dlPos = dlIdToDlPos.get(dbCursor.getLong(dbDownloadId));
			if (dlPos != null) {
				temp.put(dbCursor.getPosition(), dlPos);
			}
		}
		dbCursor.moveToPosition(-1);

		dbToDlMap = temp;

		if (DEBUG) {
			Log.d(TAG, "Generated the following dbToDlMap:");
			for (Integer db : temp.keySet()) {
				Log.d(TAG, db + " -> " + temp.get(db));
			}
		}
	}

	private boolean isDbCursor(int index) {
		return index < dbCursor.getColumnCount();
	}

	@Override
	public void close() {
		dbCursor.close();
		dlCursor.close();
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		if (isDbCursor(columnIndex)) {
			dbCursor.copyStringToBuffer(columnIndex, buffer);
		} else {
			dlCursor.copyStringToBuffer(
					columnIndex - dbCursor.getColumnCount(), buffer);
		}
	}

	@Override
	public void deactivate() {
		dbCursor.deactivate();
		dlCursor.deactivate();
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getBlob(columnIndex)
				: dlCursor.getBlob(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public int getColumnCount() {
		return dbCursor.getColumnCount() + dlColumnMap.size();
	}

	@Override
	public int getColumnIndex(String columnName) {
		return dlColumnMap.containsKey(columnName) ? dbCursor.getColumnCount()
				+ dlCursor.getColumnIndex(dlColumnMap.get(columnName))
				: dbCursor.getColumnIndex(columnName);
	}

	@Override
	public int getColumnIndexOrThrow(String columnName)
			throws IllegalArgumentException {
		return dlColumnMap.containsKey(columnName) ? dbCursor.getColumnCount()
				+ dlCursor.getColumnIndexOrThrow(dlColumnMap.get(columnName))
				: dbCursor.getColumnIndexOrThrow(columnName);
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (isDbCursor(columnIndex)) {
			return dbCursor.getColumnName(columnIndex);
		} else if (columnIndex < dbCursor.getColumnCount() + dlColumns.length) {
			return dlColumns[columnIndex - dbCursor.getColumnCount()];
		} else {
			return null;
		}
	}

	@Override
	public String[] getColumnNames() {
		String[] db = dbCursor.getColumnNames();
		String[] names = new String[db.length + dlColumns.length];
		for (int i = 0; i < db.length; i++) {
			names[i] = db[i];
		}
		for (int i = 0; i < dlColumns.length; i++) {
			names[db.length + i] = dlColumns[i];
		}
		return names;
	}

	@Override
	public int getCount() {
		return dbCursor.getCount();
	}

	@Override
	public double getDouble(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getDouble(columnIndex)
				: dlCursor.getDouble(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public Bundle getExtras() {
		Bundle bundle = new Bundle();
		bundle.putAll(dbCursor.getExtras());
		bundle.putAll(dlCursor.getExtras());
		return bundle;
	}

	@Override
	public float getFloat(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getFloat(columnIndex)
				: dlCursor.getFloat(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public int getInt(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getInt(columnIndex)
				: dlCursor.getInt(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public long getLong(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getLong(columnIndex)
				: dlCursor.getLong(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public int getPosition() {
		return dbCursor.getPosition();
	}

	@Override
	public short getShort(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getShort(columnIndex)
				: dlCursor.getShort(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public String getString(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getString(columnIndex)
				: dlCursor.getString(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public int getType(int columnIndex) {
		return isDbCursor(columnIndex) ? dbCursor.getType(columnIndex)
				: dlCursor.getType(columnIndex - dbCursor.getColumnCount());
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		return dbCursor.getWantsAllOnMoveCalls();
	}

	@Override
	public boolean isAfterLast() {
		return dbCursor.isAfterLast();
	}

	@Override
	public boolean isBeforeFirst() {
		return dbCursor.isBeforeFirst();
	}

	@Override
	public boolean isClosed() {
		return dbCursor.isClosed() || dlCursor.isClosed();
	}

	@Override
	public boolean isFirst() {
		return dbCursor.isFirst();
	}

	@Override
	public boolean isLast() {
		return dbCursor.isLast();
	}

	@Override
	public boolean isNull(int columnIndex) {
		if (isDbCursor(columnIndex)) {
			return dbCursor.isNull(columnIndex);
		} else {
			if (dlCursor.isBeforeFirst() || dlCursor.isAfterLast()) {
				return true;
			} else {
				return dlCursor.isNull(columnIndex - dbCursor.getColumnCount());
			}
		}
	}

	private void moveDlCursor() {
		Integer dlPos = dbToDlMap.get(dbCursor.getPosition());
		dlCursor.moveToPosition(dlPos == null ? -1 : dlPos);
	}

	@Override
	public boolean move(int offset) {
		boolean result = dbCursor.move(offset);
		moveDlCursor();
		return result;
	}

	@Override
	public boolean moveToFirst() {
		boolean result = dbCursor.moveToFirst();
		moveDlCursor();
		return result;
	}

	@Override
	public boolean moveToLast() {
		boolean result = dbCursor.moveToLast();
		moveDlCursor();
		return result;
	}

	@Override
	public boolean moveToNext() {
		boolean result = dbCursor.moveToNext();
		moveDlCursor();
		return result;
	}

	@Override
	public boolean moveToPosition(int position) {
		boolean result = dbCursor.moveToPosition(position);
		moveDlCursor();
		return result;
	}

	@Override
	public boolean moveToPrevious() {
		boolean result = dbCursor.moveToPrevious();
		moveDlCursor();
		return result;
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		dbCursor.registerContentObserver(observer);
		dlCursor.registerContentObserver(observer);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		dbCursor.registerDataSetObserver(observer);
		dlCursor.registerDataSetObserver(observer);
	}

	@Override
	public boolean requery() {
		if (dbCursor.requery() && dlCursor.requery()) {
			updateIdMappings();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Bundle respond(Bundle extras) {
		extras = dbCursor.respond(extras);
		extras = dlCursor.respond(extras);
		return extras;
	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		dbCursor.setNotificationUri(cr, uri);
		dlCursor.setNotificationUri(cr, uri);
	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		dbCursor.unregisterContentObserver(observer);
		dlCursor.unregisterContentObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		dbCursor.unregisterDataSetObserver(observer);
		dlCursor.unregisterDataSetObserver(observer);
	}

}
