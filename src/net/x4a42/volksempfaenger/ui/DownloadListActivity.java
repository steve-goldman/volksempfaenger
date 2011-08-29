package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class DownloadListActivity extends BaseActivity {

	private static final String TAG_RUNNING = "running";
	private static final String TAG_QUEUE = "queue";
	private static final String TAG_FINISHED = "finished";

	private TabHost tabHost;
	private LocalActivityManager localActivityManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list);

		localActivityManager = new LocalActivityManager(this, true);
		localActivityManager.dispatchCreate(savedInstanceState);

		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup(localActivityManager);

		TabSpec spec;

		// running
		spec = tabHost.newTabSpec(TAG_RUNNING);
		spec.setContent(new Intent(this, DownloadListRunningActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_running));
		tabHost.addTab(spec);

		// queue
		spec = tabHost.newTabSpec(TAG_RUNNING);
		spec.setContent(new Intent(this, DownloadListQueueActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_queue));
		tabHost.addTab(spec);

		// finished
		spec = tabHost.newTabSpec(TAG_RUNNING);
		spec.setContent(new Intent(this, DownloadListFinishedActivity.class));
		spec.setIndicator(getString(R.string.title_tab_download_finished));
		tabHost.addTab(spec);
	}

	@Override
	protected void onResume() {
		super.onResume();
		localActivityManager.dispatchResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		localActivityManager.dispatchPause(isFinishing());
	}

	@Override
	protected void onStop() {
		super.onStop();
		localActivityManager.dispatchStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		localActivityManager.dispatchDestroy(isFinishing());
	}

}
