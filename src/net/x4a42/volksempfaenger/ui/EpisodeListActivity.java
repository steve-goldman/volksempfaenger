package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.borrowed.PinProgressButton;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public abstract class EpisodeListActivity extends Activity implements
		OnUpPressedCallback {

	/* View Attributes */
	protected ListView mEpisodeListView;

	/* Other Attributes */
	private Adapter mAdapter;
	protected static final String[] EPISODE_PROJECTION = new String[] {
			Episode._ID, Episode.TITLE, Episode.DATE, Episode.STATUS,
			Episode.DOWNLOAD_STATUS, Episode.PODCAST_ID, Episode.PODCAST_TITLE,
			Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR,
			Episode.DOWNLOAD_TOTAL_SIZE_BYTES, Episode.DURATION_LISTENED,
			Episode.DURATION_TOTAL };
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
			TextView episodeDate = (TextView) row
					.findViewById(R.id.episode_date);
			PodcastLogoView podcastLogo = (PodcastLogoView) row
					.findViewById(R.id.podcast_logo);

			if (logoEnabled()) {
				podcastLogo.setPodcastId(episodeCursor.getPodcastId());
			} else {
				podcastLogo.setVisibility(View.GONE);
			}

			episodeDate.setText(getSubtitle(episodeCursor));

			PinProgressButton pinProgress = (PinProgressButton) row
					.findViewById(R.id.pin_progress);
			double progress = 0.0;
			int colorId = android.R.color.background_light;
			int iconId;
			switch (episodeCursor.getStatus()) {
			case Constants.EPISODE_STATE_NEW:
				iconId = R.drawable.progress_downloading;
				colorId = R.color.state_new;
				progress = 1.0;
				break;
			case Constants.EPISODE_STATE_DOWNLOADING:
				iconId = R.drawable.progress_downloading;
				colorId = R.color.state_downloading;
				long total = episodeCursor.getDownloadTotal();
				if (total <= 0) {
					progress = 0.0;
				} else {
					progress = (double) episodeCursor.getDownloadDone() / total;
				}
				break;
			case Constants.EPISODE_STATE_READY:
				iconId = R.drawable.progress_play;
				colorId = R.color.state_downloading;
				progress = 1.0;
				break;
			case Constants.EPISODE_STATE_LISTENING:
				iconId = R.drawable.progress_listening;
				colorId = R.color.state_listening;
				int max = episodeCursor.getDurationTotal();
				int duration = episodeCursor.getDurationListened();
				if (max <= 0) {
					progress = 0.0;
				} else {
					progress = (double) duration / max;
				}
				break;
			case Constants.EPISODE_STATE_LISTENED:
				if (episodeCursor.getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL) {
					iconId = R.drawable.progress_play;
				} else {
					iconId = R.drawable.progress_downloading;
				}
				progress = 0.0;
				break;
			default:
				iconId = R.drawable.progress_downloading;
				pinProgress.setVisibility(View.GONE);
				break;
			}
			pinProgress.setProgressColor(getResources().getColor(colorId));
			pinProgress.setProgress((int) (progress * 100));
			pinProgress.setDrawable(iconId);
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
					if (cursor == null) {
						continue;
					}
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

			Spinner selectionSpinner = (Spinner) menu.findItem(
					R.id.item_selection_spinner).getActionView();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					EpisodeListActivity.this,
					R.layout.spinner_dropdown_item_actionbar);
			int selectedCount = 0;
			for (int i = 0; i < checked.size(); i++) {
				selectedCount += checked.valueAt(i) ? 1 : 0;
			}
			selectionSpinner.setAdapter(adapter);
			adapter.addAll(getString(R.string.menu_n_selected, selectedCount),
					getString(R.string.menu_select_all));
			selectionSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							if (position == 1) {
								for (int i = 0; i < mEpisodeListView.getCount(); i++) {
									mEpisodeListView.setItemChecked(i, true);
								}
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
						}
					});

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
