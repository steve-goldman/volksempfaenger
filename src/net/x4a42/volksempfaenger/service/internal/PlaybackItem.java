package net.x4a42.volksempfaenger.service.internal;

import java.io.File;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.ui.MainActivity;
import net.x4a42.volksempfaenger.ui.ViewEpisodeActivity;
import net.x4a42.volksempfaenger.ui.ViewSubscriptionActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

public class PlaybackItem {
	private Context context;
	private Uri uri, uriTime, podcastUri;
	private String path, title, podcastTitle;
	private int durationListenedAtStart, flattrStatus;
	private long podcastId;
	private Notification notification;

	public PlaybackItem(Context context, Uri episode,
			PendingIntent pauseIntent, PendingIntent stopIntent) {
		if (episode == null) {
			throw new IllegalArgumentException("Episode is null");
		}
		this.context = context;
		uri = episode;
		uriTime = ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODETIME_URI,
				ContentUris.parseId(episode));

		EpisodeCursor cursor = new EpisodeCursor(context.getContentResolver()
				.query(episode,
						new String[] { Episode._ID, Episode.TITLE,
								Episode.STATUS, Episode.PODCAST_ID,
								Episode.ENCLOSURE_ID, Episode.DOWNLOAD_ID,
								Episode.DOWNLOAD_LOCAL_URI,
								Episode.DURATION_LISTENED,
								Episode.PODCAST_TITLE, Episode.ENCLOSURE_URL,
								Episode.FLATTR_STATUS }, null, null, null));
		try {
			if (!cursor.moveToFirst()) {
				throw new IllegalArgumentException("Episode not found");
			}
			initWithCursor(cursor);
		} finally {
			cursor.close();
		}
		notification = makeNotification(pauseIntent, stopIntent);
	}

	private void initWithCursor(EpisodeCursor cursor) {
		podcastId = cursor.getPodcastId();
		podcastUri = cursor.getPodcastUri();
		podcastTitle = cursor.getPodcastTitle();
		title = cursor.getTitle();
		File enclosureFile = cursor.getDownloadFile();
		if (enclosureFile == null || !enclosureFile.isFile()) {
			path = cursor.getEnclosureUrl();
		} else {
			path = enclosureFile.getAbsolutePath();
		}
		durationListenedAtStart = cursor.getDurationListened();
		flattrStatus = cursor.getFlattrStatus();
	}

	public Uri getUri() {
		return uri;
	}

	public Uri getUriTime() {
		return uriTime;
	}

	public String getPath() {
		return path;
	}

	public int getDurationListenedAtStart() {
		return durationListenedAtStart;
	}

	public int getFlattrStatus() {
		return flattrStatus;
	}

	public Notification getNotification() {
		return notification;
	}

	public void editMetadata(RemoteControlClient remoteControlClient) {
		final MetadataEditor metadataEditor = remoteControlClient
				.editMetadata(true);
		metadataEditor
				.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK,
						Utils.getPodcastLogoBitmap(context, podcastId))
				.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, title)
				.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,
						podcastTitle);
		metadataEditor.apply();
	}

	/**
	 * Helper method to create a new notification for the currently playing
	 * episode.
	 * 
	 * @return The newly created Notification.
	 */
	private Notification makeNotification(PendingIntent pauseIntent,
			PendingIntent stopIntent) {

		// Build back stack as proposed in
		// "Google I/O 2012 - Navigation in Android", see
		// http://www.youtube.com/watch?v=XwGHJJYBs0Q
		Intent intent;
		TaskStackBuilder taskBuilder = TaskStackBuilder.create(context);

		// MainActivity
		intent = new Intent(context, MainActivity.class);
		taskBuilder.addNextIntent(intent);

		// ViewSubscriptionActivity
		intent = new Intent(context, ViewSubscriptionActivity.class);
		intent.setData(podcastUri);
		taskBuilder.addNextIntent(intent);

		// ViewEpisodeActivity
		intent = new Intent(context, ViewEpisodeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setData(uri);
		taskBuilder.addNextIntent(intent);

		// Get the podcast logo and scale it
		Bitmap podcastLogo = Utils.getPodcastLogoBitmap(context, podcastId);
		if (podcastLogo != null) {
			Resources res = context.getResources();
			podcastLogo = Bitmap
					.createScaledBitmap(
							podcastLogo,
							res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
							res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
							true);
		}

		// Build the layout for the notification
		RemoteViews content = new RemoteViews(context.getPackageName(),
				R.layout.notification_playing);
		if (podcastLogo != null) {
			content.setImageViewBitmap(R.id.podcast_logo, podcastLogo);
		}
		content.setTextViewText(R.id.episode_title, title);
		content.setTextViewText(R.id.podcast_title, podcastTitle);
		content.setOnClickPendingIntent(R.id.pause, pauseIntent);
		content.setOnClickPendingIntent(R.id.collapse, stopIntent);

		// Build the notification and return it
		return Utils.notificationFromBuilder(new Notification.Builder(context)
				.setContent(content).setSmallIcon(R.drawable.notification)
				.setContentTitle(title).setContentText(podcastTitle)
				.setContentIntent(taskBuilder.getPendingIntent(0, 0))
				.setOngoing(true).setWhen(0));

	}
}
