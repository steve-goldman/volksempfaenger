package net.x4a42.volksempfaenger.data;

import java.util.List;

import android.app.DownloadManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class VolksempfaengerContentProvider extends ContentProvider {

	public static final String AUTHORITY = "net.x4a42.volksempfaenger";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final Uri PODCAST_URI = Uri.parse("content://" + AUTHORITY
			+ "/podcast");
	public static final Uri EPISODE_URI = Uri.parse("content://" + AUTHORITY
			+ "/episode");
	public static final Uri ENCLOSURE_URI = Uri.parse("content://" + AUTHORITY
			+ "/enclosure");

	public static final String MIME_PODCAST_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.podcast";
	public static final String MIME_PODCAST_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.podcast";
	public static final String MIME_EPISODE_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.episode";
	public static final String MIME_EPISODE_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.episode";
	public static final String MIME_ENCLOSURE_DIR = "vnd.android.cursor.dir/vnd.volksempfaenger.enclosure";
	public static final String MIME_ENCLOSURE_ITEM = "vnd.android.cursor.item/vnd.volksempfaenger.enclosure";

	private enum Mime {
		PODCAST_DIR, PODCAST_ITEM, EPISODE_DIR, EPISODE_ITEM, ENCLOSURE_DIR, ENCLOSURE_ITEM
	}

	private DatabaseHelper dbHelper;
	private QueryHelper queryHelper;
	private InsertHelper insertHelper;
	private UpdateHelper updateHelper;
	private DeleteHelper deleteHelper;

	@Override
	public boolean onCreate() {
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

	private Mime getTypeMime(Uri uri) {
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
		switch (getTypeMime(uri)) {
		case PODCAST_DIR:
			return queryHelper.queryPodcastDir(projection, selection,
					selectionArgs, sortOrder);
		case PODCAST_ITEM:
			return queryHelper.queryPodcastItem(ContentUris.parseId(uri),
					projection);
		case EPISODE_DIR:
			return queryHelper.queryEpisodeDir(projection, selection,
					selectionArgs, sortOrder);
		case EPISODE_ITEM:
			return queryHelper.queryEpisodeItem(ContentUris.parseId(uri),
					projection);
		case ENCLOSURE_DIR:
			return queryHelper.queryEnclosureDir(projection, selection,
					selectionArgs, sortOrder);
		case ENCLOSURE_ITEM:
			return queryHelper.queryEnclosureItem(ContentUris.parseId(uri),
					projection);
		default:
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO: Filter keys
		Uri newUri = null;
		switch (getTypeMime(uri)) {
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
			getContext().getContentResolver().notifyChange(newUri, null);
		}
		return newUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int rowsAffected = 0;
		switch (getTypeMime(uri)) {
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
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsAffected;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rowsAffected = 0;
		switch (getTypeMime(uri)) {
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
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsAffected;
	}

}
