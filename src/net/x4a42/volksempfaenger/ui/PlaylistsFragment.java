package net.x4a42.volksempfaenger.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class PlaylistsFragment extends ListFragment implements
		OnItemClickListener {

	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> row = new HashMap<String, String>();
		row.put("title", "hello");
		data.add(row);

		adapter = new SimpleAdapter(getActivity(), data,
				R.layout.playlists_list_row, new String[] { "title" },
				new int[] { R.id.textView1 });
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
		Log.e(this, data.get(position).get("title"));
	}

}
