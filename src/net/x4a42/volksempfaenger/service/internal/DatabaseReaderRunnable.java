package net.x4a42.volksempfaenger.service.internal;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

public class DatabaseReaderRunnable extends UpdateRunnable {

	private static final String TAG = "UpdateService";
	private static final String[] PODCAST_PROJECTION = { Podcast._ID,
			Podcast.TITLE, Podcast.FEED, Podcast.HTTP_LAST_MODIFIED,
			Podcast.HTTP_EXPIRES, Podcast.HTTP_ETAG };

	public DatabaseReaderRunnable(UpdateState update) {
		super(update);
	}

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		Log.i(TAG, "Started reading from database");

		Intent intent = getUpdate().getIntent();
		Uri podcastUri = intent.getData();

		ContentResolver resolver = getUpdate().getUpdateService()
				.getContentResolver();

		PodcastCursor cursor;
		if (podcastUri != null) {

			// sync a single podcast
			cursor = new PodcastCursor(resolver.query(podcastUri,
					PODCAST_PROJECTION, null, null, null));

		} else {

			// sync all podcasts
			cursor = new PodcastCursor(resolver.query(
					VolksempfaengerContentProvider.PODCAST_URI,
					PODCAST_PROJECTION, null, null, null));

		}

		int count = cursor.getCount();

		if (count == 0) {

			getUpdate().stopUpdate();

		} else {

			getUpdate().setRemainingFeedCounter(count);

			while (cursor.moveToNext()) {
				PodcastData podcast = new PodcastData();
				podcast.uri = cursor.getUri();
				podcast.id = cursor.getId();
				podcast.title = cursor.getTitle();
				podcast.feed = cursor.getFeed();
				podcast.cacheInfo = cursor.getCacheInformation();
				podcast.firstSync = cursor.titleIsNull();
				podcast.forceUpdate = (podcastUri != null);
				getUpdate().getUpdateService().enqueueFeedDownloader(
						getUpdate(), podcast);
			}

		}

		cursor.close();

		long endTime = System.currentTimeMillis();
		Log.i(TAG, "Finished reading from database (took "
				+ (endTime - startTime) + "ms)");

	}

}
