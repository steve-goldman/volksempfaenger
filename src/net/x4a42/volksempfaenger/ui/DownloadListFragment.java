package net.x4a42.volksempfaenger.ui;

import java.text.DateFormat;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.app.DownloadManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DownloadListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private Adapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new Adapter();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(adapter);
		setListShown(false);
		setEmptyText(getText(R.string.message_no_downloads));
		getLoaderManager().initLoader(0, null, this);
		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(mMultiChoiceModeListener);
	}

	private class Adapter extends SimpleCursorAdapter {
		public Adapter() {
			super(getActivity(), R.layout.download_list_row, null,
					new String[] { Episode.TITLE, Episode.PODCAST_TITLE },
					new int[] { R.id.episode_title, R.id.podcast_title }, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);

			EpisodeCursor episodeCursor = (EpisodeCursor) cursor;

			int status = episodeCursor.getDownloadStatus();
			long done = episodeCursor.getDownloadDone();
			long total = episodeCursor.getDownloadTotal();

			// percentage
			TextView percentage = (TextView) view.findViewById(R.id.percentage);
			if (status == DownloadManager.STATUS_RUNNING
					|| status == DownloadManager.STATUS_PAUSED) {
				percentage.setVisibility(View.VISIBLE);
				percentage.setText(String.format("%.0f%%", (double) done
						/ total * 100));
			} else {
				percentage.setVisibility(View.GONE);
			}

			// progress text
			TextView progressText = (TextView) view
					.findViewById(R.id.progress_text);
			if (status == DownloadManager.STATUS_RUNNING
					|| status == DownloadManager.STATUS_PAUSED) {
				progressText.setVisibility(View.VISIBLE);
				progressText.setText(String.format("%s / %s",
						Utils.readableFileSize(done),
						Utils.readableFileSize(total)));
			} else if (status == DownloadManager.STATUS_SUCCESSFUL) {
				progressText.setVisibility(View.VISIBLE);
				progressText.setText(Utils.readableFileSize(done));
			} else {
				progressText.setVisibility(View.GONE);
			}

			// progress bar
			ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.progress_bar);
			if (status == DownloadManager.STATUS_PENDING
					|| status == DownloadManager.STATUS_PAUSED) {
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setIndeterminate(true);
			} else if (status == DownloadManager.STATUS_RUNNING) {
				int progress;
				int progressMax;

				if (done > Integer.MAX_VALUE || total > Integer.MAX_VALUE) {
					if (done > total) {
						// this shouldn't happen but we are prepared
						progress = Integer.MAX_VALUE;
					} else {
						progress = (int) ((double) done / Long.MAX_VALUE * Integer.MAX_VALUE);
					}
					progressMax = Integer.MAX_VALUE;
				} else {
					progress = (int) done;
					progressMax = (int) total;
				}

				progressBar.setVisibility(View.VISIBLE);
				progressBar.setIndeterminate(false);
				progressBar.setMax(progressMax);
				progressBar.setProgress(progress);
			} else {
				progressBar.setVisibility(View.GONE);
			}

			// additional info
			TextView additionalInfo = (TextView) view
					.findViewById(R.id.additional_info);
			if (status == DownloadManager.STATUS_SUCCESSFUL) {
				additionalInfo.setVisibility(View.VISIBLE);
				additionalInfo.setText(DateFormat.getDateInstance().format(
						episodeCursor.getDate() * 1000));
			} else if (status == DownloadManager.STATUS_FAILED) {
				additionalInfo.setVisibility(View.VISIBLE);
				// TODO show error message
				additionalInfo.setText("Download failed");
			} else {
				additionalInfo.setVisibility(View.GONE);
			}

			// episode duration
			TextView episodeDuration = (TextView) view
					.findViewById(R.id.episode_duration);
			if (status == DownloadManager.STATUS_SUCCESSFUL) {
				episodeDuration.setVisibility(View.VISIBLE);
				episodeDuration.setText(Utils.formatTime(episodeCursor
						.getDurationTotal()));
			} else {
				episodeDuration.setVisibility(View.GONE);
			}

			// download error
			View downloadError = view.findViewById(R.id.download_error);

			if (status == DownloadManager.STATUS_FAILED) {
				downloadError.setVisibility(View.VISIBLE);

			} else {
				downloadError.setVisibility(View.GONE);
			}

			// change layout based on visibility of the download error icon
			View episodeTitle = view.findViewById(R.id.episode_title);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) episodeTitle
					.getLayoutParams();
			layoutParams.addRule(RelativeLayout.LEFT_OF, downloadError
					.getVisibility() == View.VISIBLE ? downloadError.getId()
					: percentage.getId());
			episodeTitle.setLayoutParams(layoutParams);

			// podcast logo
			PodcastLogoView logoView = (PodcastLogoView) view
					.findViewById(R.id.podcast_logo);
			logoView.setPodcastId(episodeCursor.getPodcastId());
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				VolksempfaengerContentProvider.EPISODE_URI, new String[] {
						Episode.PODCAST_ID, Episode._ID, Episode.TITLE,
						Episode.DATE, Episode.PODCAST_TITLE,
						Episode.DOWNLOAD_ID, Episode.DURATION_TOTAL,
						Episode.DOWNLOAD_STATUS,
						Episode.DOWNLOAD_BYTES_DOWNLOADED_SO_FAR,
						Episode.DOWNLOAD_TOTAL_SIZE_BYTES }, String.format(
						"%s != 0 AND %s < %s", Episode.DOWNLOAD_ID,
						Episode.DOWNLOAD_STATUS,
						DownloadManager.STATUS_SUCCESSFUL), null,
				"download.status ASC, download._id DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(new EpisodeCursor(data));
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ViewEpisodeActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	/* Action Mode */

	private MultiChoiceModeListener mMultiChoiceModeListener = new MultiChoiceModeListener() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.action_downloads, menu);
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
			case R.id.item_delete:
				EpisodeHelper.deleteDownload(
						getActivity().getContentResolver(),
						(DownloadManager) getActivity().getSystemService(
								Context.DOWNLOAD_SERVICE), getListView()
								.getCheckedItemIds());
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

}
