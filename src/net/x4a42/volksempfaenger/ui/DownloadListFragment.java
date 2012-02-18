package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.SortByStatusCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		// TODO find a better way to do this
		// ID is taken from the support library source:
		// static final int INTERNAL_EMPTY_ID = 0x00ff0001;
		((TextView) view.findViewById(0x00ff0001)).setTextSize(
				TypedValue.COMPLEX_UNIT_SP, 22);

		return view;
	}

	private class Adapter extends SimpleCursorAdapter {
		public Adapter() {
			super(getActivity(), R.layout.download_list_row, null,
					new String[] { Episode.TITLE },
					new int[] { R.id.download_title }, 0);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			super.bindView(view, context, cursor);
			EpisodeCursor episodeCursor = (EpisodeCursor) cursor;
			ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.download_progress_bar);
			TextView statusText = (TextView) view
					.findViewById(R.id.download_status);
			if (episodeCursor.getDownloadStatus() == DownloadManager.STATUS_RUNNING) {
				progressBar.setVisibility(View.VISIBLE);
				statusText.setVisibility(View.GONE);
				long done = episodeCursor.getDownloadDone();
				long total = episodeCursor.getDownloadTotal();
				if (done > Integer.MAX_VALUE || total > Integer.MAX_VALUE) {
					Log.wtf("DownloadListFragment",
							"Size > Integer.MAX_VALUE not yet supported"); // TODO
					return;
				}
				progressBar.setMax((int) total);
				progressBar.setProgress((int) done);
			} else {
				progressBar.setVisibility(View.GONE);
				statusText.setVisibility(View.VISIBLE);
				String text;
				switch (episodeCursor.getDownloadStatus()) {
				case DownloadManager.STATUS_FAILED:
					text = getString(R.string.download_status_failed);
					break;
				case DownloadManager.STATUS_PAUSED:
					text = getString(R.string.download_status_paused);
					break;
				case DownloadManager.STATUS_PENDING:
					text = getString(R.string.download_status_pending);
					break;
				case DownloadManager.STATUS_SUCCESSFUL:
					text = getString(R.string.download_status_successful);
					break;
				default:
					text = "Invalid status";
				}
				statusText.setText(text);

			}
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
						Episode.DOWNLOAD_ID, Episode.DOWNLOAD_STATUS,
						Episode.DOWNLOAD_DONE, Episode.DOWNLOAD_TOTAL },
				Episode.DOWNLOAD_ID + " != 0", null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(new EpisodeCursor(new SortByStatusCursor(data)));
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

}
