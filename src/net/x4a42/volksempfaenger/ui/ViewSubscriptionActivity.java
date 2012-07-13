package net.x4a42.volksempfaenger.ui;

import java.text.DateFormat;
import java.util.Date;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.UpdateService;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.Status;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewSubscriptionActivity extends Activity implements
		OnUpPressedCallback {

	/* Static Variables */
	private static int[] ROW_COLOR_MAP;
	private static final String PODCAST_WHERE = Podcast._ID + "=?";
	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";
	private static final String EPISODE_SORT = Episode.DATE + " DESC, "
			+ Episode._ID + " DESC";

	/* Subscription Attributes */
	private long mId;
	private Uri mUri;

	/* View Attributes */
	private PodcastLogoView mPodcastLogoView;
	private TextView mPodcastDescriptionView;
	private ListView mEpisodeListView;

	/* Other Attributes */
	private Adapter mAdapter;
	private boolean mIsUpdating;
	private UpdateServiceStatus.UiReceiver mUpdateReceiver;

	/* Activity Lifecycle */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mUri = intent.getData();

		if (mUri == null) {
			mId = intent.getLongExtra("id", -1);
			if (mId == -1) {
				finish();
				return;
			}
			mUri = ContentUris.withAppendedId(
					VolksempfaengerContentProvider.PODCAST_URI, mId);
		} else {
			mId = ContentUris.parseId(mUri);
		}

		mUpdateReceiver = new UpdateReceiver();

		initRowColorMap();

		setContentView(R.layout.view_subscription);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mPodcastLogoView = (PodcastLogoView) findViewById(R.id.podcast_logo);
		mPodcastDescriptionView = (TextView) findViewById(R.id.podcast_description);
		mEpisodeListView = (ListView) findViewById(R.id.episode_list);
		mEpisodeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mEpisodeListView.setMultiChoiceModeListener(mMultiChoiceModeListener);
		mEpisodeListView.setOnItemClickListener(mOnItemClickListener);

		// Update podcast information
		Cursor podcastCursor = getContentResolver().query(
				ContentUris.withAppendedId(
						VolksempfaengerContentProvider.PODCAST_URI, mId),
				new String[] {/* TODO */}, PODCAST_WHERE,
				new String[] { String.valueOf(mId) }, null);

		if (podcastCursor.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}
		podcastCursor.moveToFirst();
		setTitle(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.TITLE)));
		mPodcastDescriptionView.setText(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.DESCRIPTION)));

		mPodcastLogoView.setPodcastId(mId);
		mAdapter = new Adapter();
		mEpisodeListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, mLoaderCallbacks);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageReadable(this);

		UpdateServiceStatus.registerReceiver(mUpdateReceiver);
	}

	@Override
	public void onUpPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		UpdateServiceStatus.unregisterReceiver(mUpdateReceiver);
	}

	/* Content */

	private LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			return new CursorLoader(ViewSubscriptionActivity.this,
					VolksempfaengerContentProvider.EPISODE_URI, new String[] {
							Episode._ID, Episode.TITLE, Episode.DATE,
							Episode.STATUS }, EPISODE_WHERE,
					new String[] { String.valueOf(mId) }, EPISODE_SORT);
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
			super(ViewSubscriptionActivity.this,
					R.layout.view_subscription_row, null,
					new String[] { Episode.TITLE },
					new int[] { R.id.episode_title }, 0);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			super.bindView(row, context, cursor);

			int episodeState = cursor.getInt(cursor
					.getColumnIndex(Episode.STATUS));
			TextView episodeTitle = (TextView) row
					.findViewById(R.id.episode_title);
			episodeTitle.setTextColor(ROW_COLOR_MAP[episodeState]);

			Date date = new Date(cursor.getLong(cursor
					.getColumnIndex(Episode.DATE)) * 1000);

			TextView episodeDate = (TextView) row
					.findViewById(R.id.episode_date);
			episodeDate.setText(DateFormat.getDateInstance().format(date));
		}
	}

	private class UpdateReceiver extends UpdateServiceStatus.UiReceiver {

		public UpdateReceiver() {
			setActivity(ViewSubscriptionActivity.this);
		}

		@Override
		public void receiveUi(Status status) {
			Log.d(this, status.toString());
			if (status.isUpdating()) {
				if (!mIsUpdating && mUri.equals(status.getUri())) {
					mIsUpdating = true;
					invalidateOptionsMenu();
				}
			} else {
				if (mIsUpdating && mUri.equals(status.getUri())) {
					mIsUpdating = false;
					invalidateOptionsMenu();
				}
			}
		}
	}

	/* Menu */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_subscription, menu);
		ActivityHelper.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);

		MenuItem update = menu.findItem(R.id.item_update);
		if (mIsUpdating) {
			update.setActionView(R.layout.actionbar_updating);
		} else {
			update.setActionView(null);
		}

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case R.id.item_update:
			intent = new Intent(this, UpdateService.class);
			intent.setData(mUri);
			startService(intent);
			return true;

		case R.id.item_edit:
			intent = new Intent(this, EditSubscriptionActivity.class);
			intent.putExtra("id", mId);
			startActivity(intent);
			return true;

		case R.id.item_delete:
			intent = new Intent(this, DeleteSubscriptionActivity.class);
			intent.putExtra("id", mId);
			startActivity(intent);
			return true;

		default:
			return ActivityHelper.handleGlobalMenu(this, item);

		}
	}

	/* Action mode */

	private MultiChoiceModeListener mMultiChoiceModeListener = new MultiChoiceModeListener() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.action_episodes, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
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
			Intent intent = new Intent(ViewSubscriptionActivity.this,
					ViewEpisodeActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		}
	};

	/* Misc */

	/**
	 * This method initializes the attribute ROW_COLOR_MAP. We can't initialize
	 * this statically as we need access to the resources.
	 */
	private void initRowColorMap() {
		if (ROW_COLOR_MAP != null) {
			return;
		}
		Resources res = getResources();
		ROW_COLOR_MAP = new int[5];
		ROW_COLOR_MAP[Constants.EPISODE_STATE_NEW] = res
				.getColor(R.color.episode_title_new);
		ROW_COLOR_MAP[Constants.EPISODE_STATE_DOWNLOADING] = res
				.getColor(R.color.episode_title_downloading);
		ROW_COLOR_MAP[Constants.EPISODE_STATE_READY] = res
				.getColor(R.color.episode_title_ready);
		ROW_COLOR_MAP[Constants.EPISODE_STATE_LISTENING] = res
				.getColor(R.color.episode_title_listening);
		ROW_COLOR_MAP[Constants.EPISODE_STATE_LISTENED] = res
				.getColor(R.color.episode_title_listened);
	}

}
