package net.x4a42.volksempfaenger.ui;

import java.text.DateFormat;
import java.util.Date;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.service.UpdateService;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.Status;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewSubscriptionActivity extends EpisodeListActivity {

	/* Static Variables */
	private static final String PODCAST_WHERE = Podcast._ID + "=?";
	private static final String EPISODE_WHERE = Episode.PODCAST_ID + "=?";

	/* Subscription Attributes */
	private long mId;
	private Uri mUri;

	/* View Attributes */
	private PodcastLogoView mPodcastLogoView;
	private TextView mPodcastDescriptionView;

	/* Other Attributes */
	private boolean mIsUpdating;
	private UpdateServiceStatus.UiReceiver mUpdateReceiver;
	private PodcastCursor podcastCursor;

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

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			ViewGroup header = (ViewGroup) getLayoutInflater().inflate(
					R.layout.view_subscription_header, mEpisodeListView, false);
			View separator = getLayoutInflater().inflate(
					R.layout.horizontal_separator, mEpisodeListView, false);
			mPodcastLogoView = (PodcastLogoView) header
					.findViewById(R.id.podcast_logo);
			mPodcastDescriptionView = (TextView) header
					.findViewById(R.id.podcast_description);

			mEpisodeListView.addHeaderView(header);
			mEpisodeListView.addHeaderView(separator);
		} else {
			mPodcastLogoView = (PodcastLogoView) findViewById(R.id.podcast_logo);
			mPodcastDescriptionView = (TextView) findViewById(R.id.podcast_description);
		}
		attachAdapter();

		// Update podcast information
		podcastCursor = new PodcastCursor(getContentResolver().query(
				ContentUris.withAppendedId(
						VolksempfaengerContentProvider.PODCAST_URI, mId),
				new String[] {/* TODO */}, PODCAST_WHERE,
				new String[] { String.valueOf(mId) }, null));

		if (podcastCursor.getCount() == 0) {
			// ID does not exist
			finish();
			return;
		}
		podcastCursor.moveToFirst();
		setTitle(podcastCursor.getTitle());
		mPodcastDescriptionView.setText(podcastCursor.getDescription());

		mPodcastLogoView.setPodcastId(mId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		UpdateServiceStatus.registerReceiver(mUpdateReceiver);
	}

	@Override
	public void onPause() {
		super.onPause();
		UpdateServiceStatus.unregisterReceiver(mUpdateReceiver);
	}

	/* Content */
	@Override
	protected int getLayout() {
		return R.layout.view_subscription;
	}

	@Override
	protected CursorLoader getCursorLoader() {
		return new CursorLoader(ViewSubscriptionActivity.this,
				VolksempfaengerContentProvider.EPISODE_URI, EPISODE_PROJECTION,
				EPISODE_WHERE, new String[] { String.valueOf(mId) },
				EPISODE_SORT);
	}

	@Override
	protected String getSubtitle(EpisodeCursor cursor) {
		Date date = new Date(cursor.getDate() * 1000);
		return DateFormat.getDateInstance().format(date);
	}

	@Override
	protected boolean logoEnabled() {
		return false;
	}

	private class UpdateReceiver extends UpdateServiceStatus.UiReceiver {

		public UpdateReceiver() {
			setActivity(ViewSubscriptionActivity.this);
		}

		@Override
		public void receiveUi(Status status) {
			Log.v(this, status.toString());
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
		boolean result = super.onOptionsItemSelected(item);
		if (result) {
			return result;
		}
		Intent intent;
		switch (item.getItemId()) {

		case R.id.item_update:
			intent = new Intent(this, UpdateService.class);
			intent.setData(mUri);
			startService(intent);
			return true;

		case R.id.item_delete:
			intent = new Intent(this, DeleteSubscriptionActivity.class);
			intent.putExtra("id", mId);
			startActivity(intent);
			return true;

		case R.id.item_website:
			intent = new Intent(Intent.ACTION_VIEW,
					podcastCursor.getWebsiteUri());
			startActivity(intent);
			return true;

		case R.id.item_share:
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, podcastCursor.getWebsite());
			startActivity(Intent.createChooser(intent,
					getString(R.string.title_share)));
			return true;
		default:
			return false;
		}
	}
}