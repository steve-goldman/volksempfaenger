package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public abstract class EpisodeListActivity extends Activity implements
		OnUpPressedCallback {

	/* View Attributes */
	protected ListView mEpisodeListView;

	/* Other Attributes */
	private Adapter mAdapter;
	protected static final String[] EPISODE_PROJECTION = new String[] {
			Episode._ID, Episode.TITLE, Episode.DATE, Episode.STATUS,
			Episode.DOWNLOAD_STATUS, Episode.PODCAST_ID, Episode.PODCAST_TITLE };
	protected static final String EPISODE_SORT = Episode.DATE + " DESC, "
			+ Episode._ID + " DESC";

	/* Activity Lifecycle */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mEpisodeListView = (ListView) findViewById(R.id.episode_list);

		mEpisodeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mEpisodeListView.setMultiChoiceModeListener(mMultiChoiceModeListener);
		mEpisodeListView.setOnItemClickListener(mOnItemClickListener);

		View emptyView = findViewById(android.R.id.empty);
		if (emptyView != null) {
			mEpisodeListView.setEmptyView(emptyView);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageReadable(this);
	}

	/* Menu */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityHelper.handleGlobalMenu(this, item);
	}

	@Override
	public void onUpPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/* Callbacks */

	protected int getLayout() {
		return R.layout.episode_list_default;
	}

	abstract protected CursorLoader getCursorLoader();

	abstract protected String getSubtitle(EpisodeCursor cursor);

	abstract protected boolean logoEnabled();

	/* Content */

	protected void attachAdapter() {
		mAdapter = new Adapter();
		mEpisodeListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, mLoaderCallbacks);
	}

	private LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			return getCursorLoader();
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mAdapter.swapCursor(new EpisodeCursor(data));
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			mAdapter.swapCursor(null);
		}

	};

	private class Adapter extends SimpleCursorAdapter {

		public Adapter() {
			super(EpisodeListActivity.this, R.layout.episode_list_row, null,
					new String[] { Episode.TITLE },
					new int[] { R.id.episode_title }, 0);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			super.bindView(row, context, cursor);
			EpisodeCursor episodeCursor = (EpisodeCursor) cursor;
			int episodeStatus = episodeCursor.getStatus();
			TextView episodeTitle = (TextView) row
					.findViewById(R.id.episode_title);
			TextView episodeDate = (TextView) row
					.findViewById(R.id.episode_date);
			ImageView badge = (ImageView) row.findViewById(R.id.badge);
			PodcastLogoView podcastLogo = (PodcastLogoView) row
					.findViewById(R.id.podcast_logo);

			if (logoEnabled()) {
				podcastLogo.setPodcastId(episodeCursor.getPodcastId());
			} else {
				podcastLogo.setVisibility(View.GONE);
			}

			episodeDate.setText(getSubtitle(episodeCursor));

			int colorId;
			if (episodeCursor.getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL) {
				colorId = android.R.color.primary_text_light;
			} else {
				colorId = android.R.color.darker_gray;
			}
			int color = getResources().getColor(colorId);
			episodeTitle.setTextColor(color);
			episodeDate.setTextColor(color);

			switch (episodeStatus) {
			case Constants.EPISODE_STATE_NEW:
			case Constants.EPISODE_STATE_DOWNLOADING:
			case Constants.EPISODE_STATE_READY:
				badge.setVisibility(View.VISIBLE);
				badge.setImageResource(R.drawable.badge_episode_new);
				break;

			case Constants.EPISODE_STATE_LISTENING:
				badge.setVisibility(View.VISIBLE);
				badge.setImageResource(R.drawable.badge_episode_listening);
				break;

			default:
				badge.setVisibility(View.GONE);
			}

		}
	}

	/* Action mode */

	private MultiChoiceModeListener mMultiChoiceModeListener = new MultiChoiceModeListener() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			menu.clear();
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.action_episodes, menu);

			SparseBooleanArray checked = mEpisodeListView
					.getCheckedItemPositions();
			boolean canMarkAnyAsNew = false;
			boolean canMarkAnyAsListened = false;

			for (int i = 0; i < checked.size(); i++) {

				if (checked.valueAt(i)) {
					EpisodeCursor cursor = (EpisodeCursor) mEpisodeListView
							.getItemAtPosition(checked.keyAt(i));
					int status = cursor.getStatus();

					if (EpisodeHelper.canMarkAsNew(status)) {
						canMarkAnyAsNew = true;
					}

					if (EpisodeHelper.canMarkAsListened(status)) {
						canMarkAnyAsListened = true;
					}
				}
			}

			if (!canMarkAnyAsNew) {
				menu.removeItem(R.id.item_mark_new);
			}

			if (!canMarkAnyAsListened) {
				menu.removeItem(R.id.item_mark_listened);
			}

			return true;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			mode.invalidate();
			return;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			switch (item.getItemId()) {
			// case R.id.item_download:
			// // TODO implement this when #52 is fixed
			// mode.finish();
			// return true;

			case R.id.item_mark_listened:
				EpisodeHelper.markAsListened(getContentResolver(),
						mEpisodeListView.getCheckedItemIds());
				mode.finish();
				return true;

			case R.id.item_mark_new:
				EpisodeHelper.markAsNew(getContentResolver(),
						mEpisodeListView.getCheckedItemIds());
				mode.finish();
				return true;

			case R.id.item_delete:
				EpisodeHelper
						.deleteDownload(
								getContentResolver(),
								(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE),
								mEpisodeListView.getCheckedItemIds());
				mode.finish();
				return true;

			default:
				return false;
			}

		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			return;
		}

	};

	/* Item Click */

	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent(EpisodeListActivity.this,
					ViewEpisodeActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	};

}
