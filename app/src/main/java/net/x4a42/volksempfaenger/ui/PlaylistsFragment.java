package net.x4a42.volksempfaenger.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class PlaylistsFragment extends ListFragment implements
		OnItemClickListener {

	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adapter = new SimpleAdapter(getActivity(), data,
				R.layout.playlists_list_row, new String[] { "title", "icon",
						"number" }, new int[] { R.id.playlistName,
						R.id.playlistIcon, R.id.playlistNumber });
		LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int id, Bundle args) {
				return PlaylistActivity.getCursorLoader(getActivity(),
						args.getInt("playlist_id"));
			}

			@Override
			public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
				data.get(loader.getId()).put("number",
						String.valueOf(cursor.getCount()));
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onLoaderReset(Loader<Cursor> loader) {
				data.get(loader.getId()).put("number", "0");
				adapter.notifyDataSetChanged();
			}
		};
		LoaderManager lm = getLoaderManager();
		Bundle args = new Bundle();

		args.putInt("playlist_id", PlaylistActivity.LISTENING);
		addPlaylist(R.string.title_playlist_listening,
				PlaylistActivity.LISTENING, R.drawable.listening_holo_light);
		lm.initLoader(0, args, loaderCallbacks);
		args.clear();

		args.putInt("playlist_id", PlaylistActivity.NEW);
		addPlaylist(R.string.title_playlist_new, PlaylistActivity.NEW,
				R.drawable.new_holo_light);
		lm.initLoader(1, args, loaderCallbacks);
		args.clear();

		args.putInt("playlist_id", PlaylistActivity.DOWNLOADED);
		addPlaylist(R.string.title_playlist_downloaded,
				PlaylistActivity.DOWNLOADED, R.drawable.downloaded_holo_light);
		lm.initLoader(2, args, loaderCallbacks);
		args.clear();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> list, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), PlaylistActivity.class);
		intent.putExtra(PlaylistActivity.EXTRA_TYPE,
				Integer.parseInt(data.get(position).get("type")));
		startActivity(intent);
	}

	private void addPlaylist(int titleResource, int type, int icon) {
		HashMap<String, String> row = new HashMap<String, String>();
		row.put("title", getString(titleResource));
		row.put("type", String.valueOf(type));
		row.put("icon", String.valueOf(icon));
		row.put("number", "");
		data.add(row);
	}
}
