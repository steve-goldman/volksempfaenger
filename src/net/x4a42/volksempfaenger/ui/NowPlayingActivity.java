package net.x4a42.volksempfaenger.ui;

import android.content.Intent;

public class NowPlayingActivity extends ViewEpisodeActivity {

	public static final String EXTRA_LAUNCHED_FROM_NOTIFICATION = "LAUNCHED_FROM_NOTIFICATION";

	@Override
	public void onBackPressed() {
		if (getIntent()
				.getBooleanExtra(EXTRA_LAUNCHED_FROM_NOTIFICATION, false)) {
			// TODO we should handle some special cases here:
			// * ViewEpisodeActivity with current episode is on the back stack
			// * nothing is on
			// for now we just fall back to the onUpPressed implementation which
			// might not be what we want in every case.
			onUpPressed();
		} else {
			finish();
		}
	}

	@Override
	public void onUpPressed() {
		Intent intent = new Intent(this, ViewSubscriptionActivity.class);
		intent.setData(getPodcastUri());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
