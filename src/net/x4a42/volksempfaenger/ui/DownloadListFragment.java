package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class DownloadListFragment extends Fragment {

	private static final String TAG_RUNNING = "running";
	private static final String TAG_QUEUE = "queue";
	private static final String TAG_FINISHED = "finished";

	private TabHost tabHost;
	private LocalActivityManager localActivityManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.download_list, container, false);
		localActivityManager = new LocalActivityManager(this.getActivity(),
				true);
		localActivityManager.dispatchCreate(savedInstanceState);

		tabHost = (TabHost) view.findViewById(R.id.tabhost);
		tabHost.setup(localActivityManager);

		TabSpec spec;

		// running
		spec = tabHost.newTabSpec(TAG_RUNNING);
		spec.setContent(new Intent(this.getActivity(),
				DownloadListRunningActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_running));
		tabHost.addTab(spec);

		// queue
		spec = tabHost.newTabSpec(TAG_QUEUE);
		spec.setContent(new Intent(this.getActivity(),
				DownloadListQueueActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_queue));
		tabHost.addTab(spec);

		// finished
		spec = tabHost.newTabSpec(TAG_FINISHED);
		spec.setContent(new Intent(this.getActivity(),
				DownloadListFinishedActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_finished));
		tabHost.addTab(spec);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		localActivityManager.dispatchResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		localActivityManager.dispatchPause(isDetached());
	}

	@Override
	public void onStop() {
		super.onStop();
		localActivityManager.dispatchStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		localActivityManager.dispatchDestroy(isDetached());
	}

}
