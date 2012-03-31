package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.io.IOException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.receiver.MediaButtonEventReceiver;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.ui.NowPlayingActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;

public class PlaybackService extends Service implements EventListener {

	public static final String TAG = "PlaybackService";
	public static final String ACTION_PLAY = "net.x4a42.volksempfaenger.intent.action.PLAY";

	private static final int NOTIFICATION_ID = 0x59d54313;

	private Uri uri;
	private Uri uriTime;
	private Notification notification;
	private EpisodeCursor cursor;
	private Handler saveHandler;
	private PlaybackBinder binder;
	private PlaybackRemote remote;
	private PlaybackHelper helper;
	private RemoteControlClient remoteControlClient;

	@Override
	public void onCreate() {
		super.onCreate();

		saveHandler = new Handler();
		binder = new PlaybackBinder();
		remote = new PlaybackRemote();
		helper = new PlaybackHelper(this);
		helper.registerEventListener(this);

		ComponentName mediaButtonEventReceiver = new ComponentName(
				getPackageName(), MediaButtonEventReceiver.class.getName());

		AudioManager am = helper.getAudioManager();
		am.registerMediaButtonEventReceiver(mediaButtonEventReceiver);

		PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, new Intent(
						Intent.ACTION_MEDIA_BUTTON)
						.setComponent(mediaButtonEventReceiver), 0);
		// create and register the remote control client
		remoteControlClient = new RemoteControlClient(mediaPendingIntent);
		am.registerRemoteControlClient(remoteControlClient);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action;
		if (intent == null) {
			return START_STICKY;
		} else if ((action = intent.getAction()) == null) {
			return START_STICKY;
		} else {
			action = action.intern();
		}

		if (action == ACTION_PLAY) {

			try {
				playEpisode(intent.getData());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (action == Intent.ACTION_MEDIA_BUTTON) {

			KeyEvent event = (KeyEvent) intent
					.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

			if (event != null) {
				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_MEDIA_PLAY:
					remote.play();
					break;

				case KeyEvent.KEYCODE_MEDIA_PAUSE:
					remote.pause();
					break;

				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					if (remote.isPlaying()) {
						remote.pause();
					} else {
						remote.play();
					}
				}
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
		assert (VolksempfaengerContentProvider.getTypeMime(episode) == VolksempfaengerContentProvider.Mime.EPISODE_ITEM);
		uri = episode;
		uriTime = ContentUris.withAppendedId(
				VolksempfaengerContentProvider.EPISODETIME_URI,
				ContentUris.parseId(episode));
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
		if (uriTime != null) {
			ContentValues values = new ContentValues();
			values.put(Episode.DURATION_LISTENED, position);
			getContentResolver().update(uriTime, values, null, null);
		}
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
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

	}

	private void onPlayerReset() {
		stopForeground();
	}

	private void onPlayerEnd() {
		onPlayerPause();
		savePosition(0);
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);

		cursor = null;
		uri = null;
		uriTime = null;
	}

	private void onPlayerStop() {
		onPlayerPause();
		savePosition();
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);

		cursor = null;
		uri = null;
		uriTime = null;
	}

	private void onPlayerPause() {
		saveHandler.removeCallbacks(savePositionTask);
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

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
		Intent notificationIntent = new Intent(this, NowPlayingActivity.class);
		notificationIntent.setData(uri);
		notificationIntent.putExtra(
				NowPlayingActivity.EXTRA_LAUNCHED_FROM_NOTIFICATION, true);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
				.setLargeIcon(podcastLogo).setContentTitle(cursor.getTitle())
				.setContentText(cursor.getPodcastTitle())
				.setContentIntent(pendingIntent).setOngoing(true).setWhen(0)
				.getNotification();

		return notification;
	}

}