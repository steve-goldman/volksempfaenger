package net.x4a42.volksempfaenger.ui;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.UpdateService;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.Status;
import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ViewSubscriptionActivity extends FragmentActivity implements
		OnItemClickListener, OnUpPressedCallback,
		LoaderManager.LoaderCallbacks<Cursor>, OnItemLongClickListener {

	private static int[] rowColorMap;
	private static final String PODCAST_WHERE = Podcast._ID + "=?";
	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";
	private static final String EPISODE_SORT = Episode.DATE + " DESC, "
			+ Episode._ID + " DESC";

	private long id;
	private Uri uri;

	private PodcastLogoView podcastLogo;
	private TextView podcastDescription;
	private ListView episodeList;
	private Adapter adapter;
	private boolean isUpdating;
	private UpdateServiceStatus.UiReceiver updateReceiver;

	private ActionMode mActionMode;
	private ArrayList<Long> mActionModeSelected = new ArrayList<Long>();
	private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
			mActionModeSelected.clear();
			int childCount = episodeList.getChildCount();
			for (int i = 0; i < childCount; ++i) {
				episodeList.getChildAt(i).setActivated(false);
			}
		}

	};

	private boolean actionModeIsSelected(long id) {
		return mActionModeSelected.contains(id);
	}

	private boolean actionModeToggle(long id) {
		boolean selected = actionModeIsSelected(id);
		if (selected) {
			mActionModeSelected.remove(id);
		} else {
			mActionModeSelected.add(id);
		}
		return !selected;
	}

	private boolean actionModeToggle(long id, View view) {
		boolean selected = actionModeToggle(id);
		view.setActivated(selected);
		return selected;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		uri = intent.getData();

		if (uri == null) {
			id = intent.getLongExtra("id", -1);
			if (id == -1) {
				finish();
				return;
			}
			uri = ContentUris.withAppendedId(
					VolksempfaengerContentProvider.PODCAST_URI, id);
		} else {
			id = ContentUris.parseId(uri);
		}

		updateReceiver = new UpdateReceiver();

		initRowColorMap();

		setContentView(R.layout.view_subscription);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		podcastLogo = (PodcastLogoView) findViewById(R.id.podcast_logo);
		podcastDescription = (TextView) findViewById(R.id.podcast_description);
		episodeList = (ListView) findViewById(R.id.episode_list);
		episodeList.setOnItemClickListener(this);
		episodeList.setOnItemLongClickListener(this);

		// Update podcast information
		Cursor podcastCursor = getContentResolver().query(
				ContentUris.withAppendedId(
						VolksempfaengerContentProvider.PODCAST_URI, id),
				new String[] {/* TODO */}, PODCAST_WHERE,
				new String[] { String.valueOf(id) }, null);

		if (podcastCursor.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}
		podcastCursor.moveToFirst();
		setTitle(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.TITLE)));
		updatePodcastDescription(podcastCursor.getString(podcastCursor
				.getColumnIndex(Podcast.DESCRIPTION)));

		podcastLogo.setPodcastId(id);
		adapter = new Adapter();
		episodeList.setAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageReadable(this);

		UpdateServiceStatus.registerReceiver(updateReceiver);
	}

	@Override
	public void onPause() {
		super.onPause();

		UpdateServiceStatus.unregisterReceiver(updateReceiver);
	}

	private void updatePodcastDescription(String description) {
		podcastDescription.setText(description);
	}

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
		if (isUpdating) {
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
			intent.setData(uri);
			startService(intent);
			return true;

		case R.id.item_edit:
			intent = new Intent(this, EditSubscriptionActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			return true;

		case R.id.item_delete:
			intent = new Intent(this, DeleteSubscriptionActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			return true;

		default:
			return ActivityHelper.handleGlobalMenu(this, item);

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mActionMode == null) {
			Intent intent = new Intent(this, ViewEpisodeActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
		} else {
			actionModeToggle(id, view);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (mActionMode != null) {
			return false;
		}

		mActionMode = startActionMode(mActionModeCallback);
		actionModeToggle(id, view);
		return true;
	}

	private void initRowColorMap() {
		if (rowColorMap != null) {
			return;
		}
		Resources res = getResources();
		rowColorMap = new int[5];
		rowColorMap[Constants.EPISODE_STATE_NEW] = res
				.getColor(R.color.episode_title_new);
		rowColorMap[Constants.EPISODE_STATE_DOWNLOADING] = res
				.getColor(R.color.episode_title_downloading);
		rowColorMap[Constants.EPISODE_STATE_READY] = res
				.getColor(R.color.episode_title_ready);
		rowColorMap[Constants.EPISODE_STATE_LISTENING] = res
				.getColor(R.color.episode_title_listening);
		rowColorMap[Constants.EPISODE_STATE_LISTENED] = res
				.getColor(R.color.episode_title_listened);
	}

	public class Adapter extends SimpleCursorAdapter {

		public Adapter() {
			super(ViewSubscriptionActivity.this,
					R.layout.view_subscription_row, null,
					new String[] { Episode.TITLE },
					new int[] { R.id.episode_title }, 0);
		}

		@Override
		public void bindView(View row, Context context, Cursor cursor) {
			super.bindView(row, context, cursor);

			if (actionModeIsSelected(cursor.getLong(cursor
					.getColumnIndex(Episode._ID)))) {
				row.setActivated(true);
			} else {
				row.setActivated(false);
			}

			int episodeState = cursor.getInt(cursor
					.getColumnIndex(Episode.STATUS));
			TextView episodeTitle = (TextView) row
					.findViewById(R.id.episode_title);
			episodeTitle.setTextColor(rowColorMap[episodeState]);

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
				if (!isUpdating && uri.equals(status.getUri())) {
					isUpdating = true;
					invalidateOptionsMenu();
				}
			} else {
				if (isUpdating && uri.equals(status.getUri())) {
					isUpdating = false;
					invalidateOptionsMenu();
				}
			}
		}
	}

	@Override
	public void onUpPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("tag", MainActivity.subscriptionsTag);
		NavUtils.navigateUpTo(this, intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(this,
				VolksempfaengerContentProvider.EPISODE_URI, new String[] {
						Episode._ID, Episode.TITLE, Episode.DATE,
						Episode.STATUS }, EPISODE_WHERE,
				new String[] { String.valueOf(id) }, EPISODE_SORT);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(new EpisodeCursor(data));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);

	}

}
