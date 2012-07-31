package net.x4a42.volksempfaenger.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import android.app.ListFragment;
import android.content.Intent;
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
				R.layout.playlists_list_row, new String[] { "title" },
				new int[] { R.id.textView1 });
		addPlaylist(R.string.title_playlist_listening,
				PlaylistActivity.LISTENING);
		addPlaylist(R.string.title_playlist_new, PlaylistActivity.NEW);
		addPlaylist(R.string.title_playlist_downloaded,
				PlaylistActivity.DOWNLOADED);
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

	private void addPlaylist(int titleResource, int type) {
		HashMap<String, String> row = new HashMap<String, String>();
		row.put("title", getString(titleResource));
		row.put("type", String.valueOf(type));
		data.add(row);
	}
}
