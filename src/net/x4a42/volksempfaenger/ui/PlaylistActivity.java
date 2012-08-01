package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.app.DownloadManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class PlaylistActivity extends EpisodeListActivity {
	public static final String EXTRA_TYPE = "playlist_type";
	public static final int NEW = 0;
	public static final int LISTENING = 1;
	public static final int DOWNLOADED = 2;

	private int type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		type = intent.getIntExtra(EXTRA_TYPE, -1);
		if (type == -1) {
			finish();
		}
		int titleResource = 0;
		switch (type) {
		case NEW:
			titleResource = R.string.title_playlist_new;
			break;
		case LISTENING:
			titleResource = R.string.title_playlist_listening;
			break;
		case DOWNLOADED:
			titleResource = R.string.title_playlist_downloaded;
			break;
		}
		if (titleResource != 0) {
			setTitle(getString(titleResource));
		}
		attachAdapter();
	}

	@Override
	protected CursorLoader getCursorLoader() {
		return PlaylistActivity.getCursorLoader(this, type);
	}

	public static CursorLoader getCursorLoader(Context context, int type) {
		String where = null;
		switch (type) {
		case NEW:
			where = String.format("%s.%s IN (%s, %s, %s)",
					DatabaseHelper.TABLE_EPISODE, Episode.STATUS,
					Constants.EPISODE_STATE_NEW, Constants.EPISODE_STATE_READY,
					Constants.EPISODE_STATE_DOWNLOADING);
			break;
		case LISTENING:
			where = String.format("%s.%s = %s", DatabaseHelper.TABLE_EPISODE,
					Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
			break;
		case DOWNLOADED:
			where = String.format("%s = %s", Episode.DOWNLOAD_STATUS,
					DownloadManager.STATUS_SUCCESSFUL);
			break;
		}
		if (where != null) {
			return new CursorLoader(context,
					VolksempfaengerContentProvider.EPISODE_URI,
					EPISODE_PROJECTION, where, null, EPISODE_SORT);
		} else {
			return null;
		}

	}

	@Override
	protected String getSubtitle(EpisodeCursor cursor) {
		return cursor.getPodcastTitle();
	}

	@Override
	protected boolean logoEnabled() {
		return true;
	}

	@Override
	public void onUpPressed() {
		Intent intent = NavUtils.getParentActivityIntent(PlaylistActivity.this);
		intent.putExtra("tag", MainActivity.playlistsTag);
		NavUtils.navigateUpTo(PlaylistActivity.this, intent);
	}

}
