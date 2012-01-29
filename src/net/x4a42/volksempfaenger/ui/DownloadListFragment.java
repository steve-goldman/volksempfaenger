package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

public class DownloadListFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private Adapter adapter;
	private ListView downloadList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new Adapter();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.download_list, container, false);
		downloadList = (ListView) view.findViewById(R.id.download_list);
		downloadList.setAdapter(adapter);
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
			EpisodeCursor episodeCursor = new EpisodeCursor(cursor);
			ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.download_progress_bar);
			long done = episodeCursor.getDownloadDone();
			long total = episodeCursor.getDownloadTotal();
			if (done > Integer.MAX_VALUE || total > Integer.MAX_VALUE) {
				Log.wtf("DownloadListFragment",
						"Size > Integer.MAX_VALUE not yet supported"); // TODO
				return;
			}

			progressBar.setMax((int) total);
			progressBar.setProgress((int) done);

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				VolksempfaengerContentProvider.EPISODE_URI, new String[] {
						Episode._ID, Episode.TITLE, Episode.DOWNLOAD_ID,
						Episode.DOWNLOAD_STATUS, Episode.DOWNLOAD_DONE,
						Episode.DOWNLOAD_TOTAL },
				Episode.DOWNLOAD_ID + " != 0", null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}
