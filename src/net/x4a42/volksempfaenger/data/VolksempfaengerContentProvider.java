package net.x4a42.volksempfaenger.data;

import java.util.List;

import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class VolksempfaengerContentProvider extends ContentProvider {

	public static final String TAG = "VolksempfaengerContentProvider";
	public static final String AUTHORITY = "net.x4a42.volksempfaenger";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri PODCAST_URI = Uri.parse("content://" + AUTHORITY
			+ "/podcast");
	public static final Uri EPISODE_URI = Uri.parse("content://" + AUTHORITY
			+ "/episode");
	public static final Uri EPISODETIME_URI = Uri.parse("content://"
			+ AUTHORITY + "/episodetime");
	public static final Uri ENCLOSURE_URI = Uri.parse("content://" + AUTHORITY
			+ "/enclosure");

	public static final String MIME_PODCAST_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.podcast";
	public static final String MIME_PODCAST_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.podcast";
	public static final String MIME_EPISODE_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.episode";
	public static final String MIME_EPISODE_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.episode";
	public static final String MIME_ENCLOSURE_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.enclosure";
	public static final String MIME_ENCLOSURE_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.enclosure";

	public enum Mime {
		PODCAST_DIR, PODCAST_ITEM, EPISODE_DIR, EPISODE_ITEM, EPISODETIME_ITEM, ENCLOSURE_DIR, ENCLOSURE_ITEM
	}

	private ContentResolver contentResolver;
	private DatabaseHelper dbHelper;
	private QueryHelper queryHelper;
	private InsertHelper insertHelper;
	private UpdateHelper updateHelper;
	private DeleteHelper deleteHelper;

	@Override
	public boolean onCreate() {
		contentResolver = getContext().getContentResolver();
		dbHelper = DatabaseHelper.getInstance(getContext());
		queryHelper = new QueryHelper(dbHelper, (DownloadManager) getContext()
				.getSystemService(Context.DOWNLOAD_SERVICE));
		insertHelper = new InsertHelper(dbHelper);
		updateHelper = new UpdateHelper(dbHelper);
		deleteHelper = new DeleteHelper(dbHelper);
		return true;
	}

	private static long parseId(Uri uri) {
		return ContentUris.parseId(uri);
	}

	private void _notifyUri(Uri uri) {
		Log.d(TAG, "notifying " + uri);
		contentResolver.notifyChange(uri, null);
	}

	private void notifyUri(Uri uri, Mime type) {
		_notifyUri(uri);
		// TODO research if the following is needed at all
		if (type == Mime.PODCAST_ITEM) {
			_notifyUri(PODCAST_URI);
		} else if (type == Mime.EPISODE_ITEM) {
			_notifyUri(EPISODE_URI);
		} else if (type == Mime.ENCLOSURE_ITEM) {
			_notifyUri(ENCLOSURE_URI);
		}
	}

	public static Mime getTypeMime(Uri uri) {
		if (!AUTHORITY.equals(uri.getAuthority())) {
			return null;
		}

		List<String> segments = uri.getPathSegments();
		if (segments == null || segments.size() == 0) {
			return null;
		}

		String type = segments.get(0);
		if ("podcast".equals(type)) {
			switch (segments.size()) {
			case 1:
				return Mime.PODCAST_DIR;
			case 2:
				return Mime.PODCAST_ITEM;
			default:
				return null;
			}
		} else if ("episode".equals(type)) {
			switch (segments.size()) {
			case 1:
				return Mime.EPISODE_DIR;
			case 2:
				return Mime.EPISODE_ITEM;
			default:
				return null;
			}
		} else if ("episodetime".equals(type)) {
			return segments.size() == 2 ? Mime.EPISODETIME_ITEM : null;
		} else if ("enclosure".equals(type)) {
			switch (segments.size()) {
			case 1:
				return Mime.ENCLOSURE_DIR;
			case 2:
				return Mime.ENCLOSURE_ITEM;
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (getTypeMime(uri)) {
		case PODCAST_DIR:
			return MIME_PODCAST_DIR;
		case PODCAST_ITEM:
			return MIME_PODCAST_ITEM;
		case EPISODE_DIR:
			return MIME_EPISODE_DIR;
		case EPISODE_ITEM:
		case EPISODETIME_ITEM:
			return MIME_EPISODE_ITEM;
		case ENCLOSURE_DIR:
			return MIME_ENCLOSURE_DIR;
		case ENCLOSURE_ITEM:
			return MIME_ENCLOSURE_ITEM;
		default:
			return null;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;

		switch (getTypeMime(uri)) {
		case PODCAST_DIR:
			cursor = queryHelper.queryPodcastDir(projection, selection,
					selectionArgs, sortOrder);
			break;
		case PODCAST_ITEM:
			cursor = queryHelper.queryPodcastItem(ContentUris.parseId(uri),
					projection);
			break;
		case EPISODE_DIR:
			cursor = queryHelper.queryEpisodeDir(projection, selection,
					selectionArgs, sortOrder);
			break;
		case EPISODE_ITEM:
		case EPISODETIME_ITEM:
			cursor = queryHelper.queryEpisodeItem(ContentUris.parseId(uri),
					projection);
			break;
		case ENCLOSURE_DIR:
			cursor = queryHelper.queryEnclosureDir(projection, selection,
					selectionArgs, sortOrder);
			break;
		case ENCLOSURE_ITEM:
			cursor = queryHelper.queryEnclosureItem(ContentUris.parseId(uri),
					projection);
			break;
		}

		if (cursor != null) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Filter keys
		Mime type = getTypeMime(uri);
		Uri newUri = null;

		switch (type) {
		case PODCAST_DIR:
			newUri = insertHelper.insertPodcast(uri, values);
			break;
		case EPISODE_DIR:
			newUri = insertHelper.insertEpisode(uri, values);
			break;
		case ENCLOSURE_DIR:
			newUri = insertHelper.insertEnclosure(uri, values);
			break;
		}

		if (newUri != null) {
			notifyUri(uri, type);
		}

		return newUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Mime type = getTypeMime(uri);
		int rowsAffected = 0;

		switch (type) {
		case PODCAST_DIR:
			rowsAffected = updateHelper.updatePodcastDir(values, selection,
					selectionArgs);
			break;
		case PODCAST_ITEM:
			rowsAffected = updateHelper.updatePodcastItem(parseId(uri), values,
					selection, selectionArgs);
			break;
		case EPISODE_DIR:
			rowsAffected = updateHelper.updateEpisodeDir(values, selection,
					selectionArgs);
			break;
		case EPISODE_ITEM:
		case EPISODETIME_ITEM:
			rowsAffected = updateHelper.updateEpisodeItem(parseId(uri), values,
					selection, selectionArgs);
			break;
		case ENCLOSURE_DIR:
			rowsAffected = updateHelper.updateEnclosureDir(values, selection,
					selectionArgs);
			break;
		case ENCLOSURE_ITEM:
			rowsAffected = updateHelper.updateEnclosureItem(parseId(uri),
					values, selection, selectionArgs);
			break;
		}

		if (rowsAffected > 0) {
			notifyUri(uri, type);
		}

		return rowsAffected;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Mime type = getTypeMime(uri);
		int rowsAffected = 0;

		switch (type) {
		case PODCAST_DIR:
			rowsAffected = deleteHelper.deletePodcastDir(selection,
					selectionArgs);
			break;
		case PODCAST_ITEM:
			rowsAffected = deleteHelper.deletePodcastItem(parseId(uri),
					selection, selectionArgs);
			break;
		case EPISODE_DIR:
			rowsAffected = deleteHelper.deleteEpisodeDir(selection,
					selectionArgs);
			break;
		case EPISODE_ITEM:
			rowsAffected = deleteHelper.deleteEpisodeItem(parseId(uri),
					selection, selectionArgs);
			break;
		case ENCLOSURE_DIR:
			rowsAffected = deleteHelper.deleteEnclosureDir(selection,
					selectionArgs);
			break;
		case ENCLOSURE_ITEM:
			rowsAffected = deleteHelper.deleteEnclosureItem(parseId(uri),
					selection, selectionArgs);
			break;
		}

		if (rowsAffected > 0) {
			notifyUri(uri, type);
		}

		return rowsAffected;
	}

}
