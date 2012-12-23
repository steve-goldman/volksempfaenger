package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class ExternalStorageMissingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.external_storage_missing);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	private static final IntentFilter filter = new IntentFilter();

	static {
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_NOFS);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_SHARED);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO make this more useful somehow
			Toast.makeText(ExternalStorageMissingActivity.this,
					intent.getAction(), Toast.LENGTH_SHORT).show();

			String action = intent.getAction().intern();
			if (action == Intent.ACTION_MEDIA_MOUNTED) {
				finish();
			}
		}
	};

}
