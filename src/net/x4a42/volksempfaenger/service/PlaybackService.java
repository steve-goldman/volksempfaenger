package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.io.IOException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.ui.ViewEpisodeActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements EventListener {

	private static final String TAG = "PlaybackService";
	private static final int NOTIFICATION_ID = 0x59d54313;

	public static final String ACTION_PLAY = "net.x4a42.volksempfaenger.intent.action.PLAY";

	private Notification notification;
	private EpisodeCursor cursor;

	private Handler saveHandler;

	private PlaybackBinder binder;
	private PlaybackRemote remote;
	private PlaybackHelper helper;

	private Uri uri;

	@Override
	public void onCreate() {
		super.onCreate();

		saveHandler = new Handler();
		binder = new PlaybackBinder();
		remote = new PlaybackRemote();
		helper = new PlaybackHelper(this);
		helper.registerEventListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return START_STICKY;
		}

		if (ACTION_PLAY.equals(intent.getAction())) {
			try {
				playEpisode(intent.getData());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		helper.destroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * Public API for PlaybackService.
	 */
	public class PlaybackRemote {
		private PlaybackRemote() {
			super();
		}

		public Uri getEpisodeUri() {
			return uri;
		}

		public boolean isPlaying() {
			return helper.isPlaying();
		}

		public int getPosition() {
			return helper.getPosition();
		}

		public int getDuration() {
			return helper.getDuration();
		}

		public void play() {
			helper.play();
		}

		public void pause() {
			helper.pause();
		}

		public void seekTo(int position) {
			helper.seekTo(position);
		}

		public void movePosition(int offset) {
			helper.movePosition(offset);
		}

		public void registerEventListener(EventListener listener) {
			helper.registerEventListener(listener);
		}

		public void unregisterEventListener(EventListener listener) {
			helper.unregisterEventListener(listener);
		}
	}

	public class PlaybackBinder extends Binder {
		private PlaybackBinder() {
			super();
		}

		public PlaybackRemote getRemote() {
			return remote;
		}
	}

	/**
	 * Helper method to start playback of an episode.
	 * 
	 * @param episode
	 *            Uri of the episode to play.
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private void playEpisode(Uri episode) throws IllegalArgumentException,
			IOException {
		uri = episode;
		cursor = new EpisodeCursor(getContentResolver().query(
				episode,
				new String[] { Episode._ID, Episode.TITLE, Episode.STATUS,
						Episode.PODCAST_ID, Episode.ENCLOSURE_ID,
						Episode.DOWNLOAD_ID, Episode.DOWNLOAD_URI,
						Episode.DURATION_LISTENED, Episode.PODCAST_TITLE },
				null, null, null));

		if (!cursor.moveToFirst()) {
			throw new IllegalArgumentException("Episode not found");
		}
		File enclosureFile = cursor.getDownloadFile();
		if (enclosureFile == null || !enclosureFile.isFile()) {
			throw new IllegalArgumentException("Episode not found");
		}
		helper.open(enclosureFile.getAbsolutePath());
		ContentValues values = new ContentValues();
		values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
		updateEpisode(values);
	}

	/**
	 * Helper method to update the episode in the ContentProvider.
	 * 
	 * @param values
	 *            Values to update.
	 */
	private void updateEpisode(ContentValues values) {
		if (uri != null) {
			getContentResolver().update(uri, values, null, null);
		}
	}

	public Uri getCurrentEpisode() {
		return uri;
	}

	private void startForeground() {
		if (notification == null) {
			return;
		}
		startForeground(NOTIFICATION_ID, notification);
	}

	private void stopForeground() {
		stopForeground(true);
	}

	private void savePosition() {
		savePosition(helper.getPosition());
	}

	private void savePosition(int position) {
		ContentValues values = new ContentValues();
		values.put(Episode.DURATION_LISTENED, position);
		updateEpisode(values);
	}

	private Runnable savePositionTask = new Runnable() {
		public void run() {
			savePosition();
			saveHandler.postDelayed(this, 500);
		}
	};

	private int getDurationListened() {
		return cursor.getInt(cursor.getColumnIndex(Episode.DURATION_LISTENED));
	}

	@Override
	public void onPlaybackEvent(Event event) {
		switch (event) {
		case PREPARED:
			onPlayerPrepared();
			break;

		case PLAY:
			onPlayerPlay();
			break;

		case PAUSE:
			onPlayerPause();
			break;

		case STOP:
			onPlayerStop();
			break;

		case END:
			onPlayerEnd();
			break;

		case RESET:
			onPlayerReset();
			break;
		}
	}

	private void onPlayerPlay() {
		saveHandler.post(savePositionTask);
	}

	private void onPlayerReset() {
		stopForeground();
	}

	private void onPlayerEnd() {
		onPlayerPause();
		savePosition(0);
		cursor = null;
		uri = null;
	}

	private void onPlayerStop() {
		onPlayerPause();
		savePosition();
		cursor = null;
		uri = null;
	}

	private void onPlayerPause() {
		saveHandler.removeCallbacks(savePositionTask);
		stopForeground();
	}

	private void onPlayerPrepared() {
		notification = makeNotification();
		startForeground();
		helper.seekTo(getDurationListened());
		helper.play();
	}

	/**
	 * Helper method to create a new notification for the currently playing
	 * episode.
	 * 
	 * @return The newly created Notification.
	 */
	private Notification makeNotification() {
		Notification notification;
		Intent notificationIntent = new Intent(this, ViewEpisodeActivity.class);
		notificationIntent.putExtra("id", cursor.getId());
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Bitmap podcastLogo = Utils.getPodcastLogoBitmap(this,
					cursor.getPodcastId());
			if (podcastLogo != null) {
				Resources res = getResources();
				podcastLogo = Bitmap
						.createScaledBitmap(
								podcastLogo,
								res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
								res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
								false);
			}
			notification = new Notification.Builder(this)
					.setSmallIcon(R.drawable.notification)
					.setLargeIcon(podcastLogo)
					.setContentTitle(cursor.getTitle())
					.setContentText(cursor.getPodcastTitle())
					.setContentIntent(pendingIntent).setOngoing(true)
					.setWhen(0).getNotification();
		} else {
			// Gingerbread (API 10) does not support Notification.Builder
			notification = new Notification(R.drawable.notification, null,
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			notification.setLatestEventInfo(this, cursor.getTitle(),
					cursor.getPodcastTitle(), pendingIntent);
		}
		return notification;
	}

}