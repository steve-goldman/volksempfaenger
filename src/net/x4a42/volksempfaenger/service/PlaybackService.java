package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.io.IOException;

import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.receiver.MediaButtonEventReceiver;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.ui.MainActivity;
import net.x4a42.volksempfaenger.ui.ViewEpisodeActivity;
import net.x4a42.volksempfaenger.ui.ViewSubscriptionActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.view.KeyEvent;
import android.widget.RemoteViews;

public class PlaybackService extends Service implements EventListener {

	public static final String ACTION_PLAY = "net.x4a42.volksempfaenger.intent.action.PLAY";
	public static final String ACTION_PAUSE = "net.x4a42.volksempfaenger.intent.action.PAUSE";
	public static final String ACTION_STOP = "net.x4a42.volksempfaenger.intent.action.STOP";

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
	private ComponentName mediaButtonEventReceiver;

	private PendingIntent playIntent;
	private PendingIntent pauseIntent;

	@Override
	public void onCreate() {
		super.onCreate();

		saveHandler = new Handler();
		binder = new PlaybackBinder();
		remote = new PlaybackRemote();
		helper = new PlaybackHelper(this);
		helper.registerEventListener(this);

		mediaButtonEventReceiver = new ComponentName(getPackageName(),
				MediaButtonEventReceiver.class.getName());

		PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, new Intent(
						Intent.ACTION_MEDIA_BUTTON)
						.setComponent(mediaButtonEventReceiver), 0);
		// create and register the remote control client
		remoteControlClient = new RemoteControlClient(mediaPendingIntent);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

			// Required for Jelly Bean to show lockscreen controls
			// displays a 'previous' button on ICS because of a bug
			// https://code.google.com/p/android/issues/detail?id=29920&q=remotecontrolclient&colspec=ID%20Type%20Status%20Owner%20Summary%20Stars
			remoteControlClient
					.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE);
		}

		{
			Intent i = new Intent(this, PlaybackService.class);
			i.setAction(ACTION_PAUSE);
			pauseIntent = PendingIntent.getService(this, 0, i, 0);
		}
		{
			Intent i = new Intent(this, PlaybackService.class);
			i.setAction(ACTION_PLAY);
			playIntent = PendingIntent.getService(this, 0, i, 0);
		}

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
			if (notification == null) {
				try {
					playEpisode(intent.getData());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				remote.play();
			}

		} else if (action == ACTION_PAUSE) {

			remote.pause();
		} else if (action == ACTION_STOP) {
			remote.stop();
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

		public void stop() {
			helper.stop();
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
						Episode.DOWNLOAD_ID, Episode.DOWNLOAD_LOCAL_URI,
						Episode.DURATION_LISTENED, Episode.PODCAST_TITLE,
						Episode.ENCLOSURE_URL, Episode.FLATTR_STATUS }, null,
				null, null));

		if (!cursor.moveToFirst()) {
			throw new IllegalArgumentException("Episode not found");
		}
		File enclosureFile = cursor.getDownloadFile();
		String path;
		if (enclosureFile == null || !enclosureFile.isFile()) {
			path = cursor.getEnclosureUrl();
		} else {
			path = enclosureFile.getAbsolutePath();
		}
		helper.open(path);
		ContentValues values = new ContentValues();
		values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
		updateEpisode(values);
		final MetadataEditor metadataEditor = remoteControlClient
				.editMetadata(true);
		metadataEditor
				.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK,
						Utils.getPodcastLogoBitmap(this, cursor.getPodcastId()))
				.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
						cursor.getTitle())
				.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,
						cursor.getPodcastTitle());
		metadataEditor.apply();
		flattrEpisodeIfAutoPrefIs(net.x4a42.volksempfaenger.Constants.PREF_AUTO_FLATTR_STARTED);
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
			saveHandler.postDelayed(this, 10000);
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
		default:
			break;
		}
	}

	private void onPlayerPrepared() {
		helper.seekTo(getDurationListened());
		helper.play();

		AudioManager am = helper.getAudioManager();
		am.registerMediaButtonEventReceiver(mediaButtonEventReceiver);
		am.registerRemoteControlClient(remoteControlClient);
	}

	private void onPlayerPlay() {
		if (notification == null) {
			notification = makeNotification();
			startForeground();
		}
		saveHandler.post(savePositionTask);
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

		notification.contentView.setImageViewResource(R.id.pause,
				R.drawable.ic_notification_pause);
		notification.contentView.setOnClickPendingIntent(R.id.pause,
				pauseIntent);
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	private void onPlayerReset() {
		stopForeground();
		notification = null;
	}

	private void onPlayerEnd() {
		EpisodeHelper.markAsListened(getContentResolver(), uri);
		flattrEpisodeIfAutoPrefIs(net.x4a42.volksempfaenger.Constants.PREF_AUTO_FLATTR_FINISHED);
		savePosition(0);
		onPlayerStop();
	}

	private void onPlayerStop() {
		saveHandler.removeCallbacks(savePositionTask);
		onPlayerReset();
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);

		cursor = null;
		uri = null;
		uriTime = null;

		AudioManager am = helper.getAudioManager();
		am.unregisterRemoteControlClient(remoteControlClient);
		am.unregisterMediaButtonEventReceiver(mediaButtonEventReceiver);
	}

	private void onPlayerPause() {
		saveHandler.removeCallbacks(savePositionTask);
		savePosition();
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		notification.contentView.setImageViewResource(R.id.pause,
				R.drawable.ic_notification_play);
		notification.contentView
				.setOnClickPendingIntent(R.id.pause, playIntent);
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * Helper method to create a new notification for the currently playing
	 * episode.
	 * 
	 * @return The newly created Notification.
	 */
	private Notification makeNotification() {

		// Build back stack as proposed in
		// "Google I/O 2012 - Navigation in Android", see
		// http://www.youtube.com/watch?v=XwGHJJYBs0Q
		Intent intent;
		TaskStackBuilder taskBuilder = TaskStackBuilder.create(this);

		// MainActivity
		intent = new Intent(this, MainActivity.class);
		taskBuilder.addNextIntent(intent);

		// ViewSubscriptionActivity
		intent = new Intent(this, ViewSubscriptionActivity.class);
		intent.setData(cursor.getPodcastUri());
		taskBuilder.addNextIntent(intent);

		// ViewEpisodeActivity
		intent = new Intent(this, ViewEpisodeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setData(uri);
		taskBuilder.addNextIntent(intent);

		// Get the podcast logo and scale it
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

		// Build the layout for the notification
		RemoteViews content = new RemoteViews(getPackageName(),
				R.layout.notification_playing);
		if (podcastLogo != null) {
			content.setImageViewBitmap(R.id.podcast_logo, podcastLogo);
		}
		content.setTextViewText(R.id.episode_title, cursor.getTitle());
		content.setTextViewText(R.id.podcast_title, cursor.getPodcastTitle());
		content.setOnClickPendingIntent(R.id.pause, pauseIntent);
		{
			PendingIntent stopIntent;
			Intent i = new Intent(this, PlaybackService.class);
			i.setAction(ACTION_STOP);
			stopIntent = PendingIntent.getService(this, 0, i, 0);
			content.setOnClickPendingIntent(R.id.collapse, stopIntent);
		}

		// Build the notification and return it
		return Utils.notificationFromBuilder(new Notification.Builder(this)
				.setContent(content).setSmallIcon(R.drawable.notification)
				.setContentTitle(cursor.getTitle())
				.setContentText(cursor.getPodcastTitle())
				.setContentIntent(taskBuilder.getPendingIntent(0, 0))
				.setOngoing(true).setWhen(0));

	}

	private void flattrEpisodeIfAutoPrefIs(String flattrAutoPrefValue) {
		SharedPreferences prefs = ((VolksempfaengerApplication) getApplication())
				.getSharedPreferences();
		String autoFlattr = prefs.getString(PreferenceKeys.FLATTR_AUTO, null);
		if (autoFlattr != null
				&& cursor.getFlattrStatus() == Constants.FLATTR_STATE_NEW
				&& autoFlattr.equals(flattrAutoPrefValue)) {
			EpisodeHelper.flattr(getContentResolver(), uri);
			Intent intent = new Intent(this, FlattrService.class);
			startService(intent);
		}
	}
}