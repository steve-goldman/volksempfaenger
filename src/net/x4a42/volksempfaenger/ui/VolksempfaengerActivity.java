package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class VolksempfaengerActivity extends BaseActivity implements
		OnClickListener {

	private Button buttonSubscriptionList;
	private Button buttonListenManager;
	private Button buttonDownloadQueue;
	private Button buttonDebug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		buttonSubscriptionList = (Button) findViewById(R.id.button_subscriptionlist);
		buttonDownloadQueue = (Button) findViewById(R.id.button_downloadmanager);
		buttonListenManager = (Button) findViewById(R.id.button_listenqueue);
		buttonDebug = (Button) findViewById(R.id.button_debug);

		buttonSubscriptionList.setOnClickListener(this);
		buttonDownloadQueue.setOnClickListener(this);
		buttonListenManager.setOnClickListener(this);
		buttonDebug.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.button_subscriptionlist:
			intent = new Intent(this, SubscriptionListActivity.class);
			startActivity(intent);
			return;
		case R.id.button_downloadmanager:
			intent = new Intent(this, DownloadListActivity.class);
			startActivity(intent);
			return;
		case R.id.button_listenqueue:
			intent = new Intent(this, ListenQueueActivity.class);
			startActivity(intent);
			return;
		case R.id.button_debug:
			intent = new Intent(this, DebugActivity.class);
			startActivity(intent);
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addGlobalMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			handleGlobalMenu(item);
		}
		return true;
	}

}
