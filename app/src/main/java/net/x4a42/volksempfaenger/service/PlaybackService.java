package net.x4a42.volksempfaenger.service;

import java.io.IOException;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.PreferenceKeys;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.data.Columns.Episode;
import net.x4a42.volksempfaenger.data.Constants;
import net.x4a42.volksempfaenger.data.EpisodeHelper;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.receiver.MediaButtonEventReceiver;
import net.x4a42.volksempfaenger.service.PlaybackHelper.Event;
import net.x4a42.volksempfaenger.service.PlaybackHelper.EventListener;
import net.x4a42.volksempfaenger.service.internal.PlaybackItem;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;

public class PlaybackService extends Service implements EventListener {

	public static final String ACTION_PLAY = "net.x4a42.volksempfaenger.intent.action.PLAY";
	public static final String ACTION_PAUSE = "net.x4a42.volksempfaenger.intent.action.PAUSE";
	public static final String ACTION_STOP = "net.x4a42.volksempfaenger.intent.action.STOP";

	private static final int NOTIFICATION_ID = 0x59d54313;

	private PlaybackItem playbackItem;
	private Handler saveHandler;
	private PlaybackBinder binder;
	private PlaybackRemote remote;
	private PlaybackHelper helper;
	private RemoteControlClient remoteControlClient;
	private ComponentName mediaButtonEventReceiver;

	private PendingIntent pauseIntent, stopIntent;

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

		Intent intent = new Intent(this, PlaybackService.class);
		intent.setAction(ACTION_PAUSE);
		pauseIntent = PendingIntent.getService(this, 0, intent, 0);
		intent = new Intent(this, PlaybackService.class);
		intent.setAction(ACTION_STOP);
		stopIntent = PendingIntent.getService(this, 0, intent, 0);
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
			if (playbackItem == null) {
				// start playing
				try {
					playEpisode(intent.getData());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (!playbackItem.getUri().equals(intent.getData())) {
				// switch episode
				remote.stop();
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
				// resume
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
			if (playbackItem == null) {
				return null;
			} else {
				return playbackItem.getUri();
			}
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
		PlaybackItem playbackItem = new PlaybackItem(this, episode,
				pauseIntent, stopIntent);
		// resets this.playbackItem
		helper.open(playbackItem.getPath());
		// assign here to prevent inadvertent reset
		this.playbackItem = playbackItem;
		ContentValues values = new ContentValues();
		values.put(Episode.STATUS, Constants.EPISODE_STATE_LISTENING);
		updateEpisode(values);
		flattrEpisodeIfAutoPrefIs(net.x4a42.volksempfaenger.Constants.PREF_AUTO_FLATTR_STARTED);
	}

	/**
	 * Helper method to update the episode in the ContentProvider.
	 * 
	 * @param values
	 *            Values to update.
	 */
	private void updateEpisode(ContentValues values) {
		if (playbackItem != null) {
			getContentResolver().update(playbackItem.getUri(), values, null,
					null);
		}
	}

	private void savePosition() {
		savePosition(helper.getPosition());
	}

	private void savePosition(int position) {
		if (playbackItem != null) {
			ContentValues values = new ContentValues();
			values.put(Episode.DURATION_LISTENED, position);
			getContentResolver().update(playbackItem.getUriTime(), values,
					null, null);
		}
	}

	private Runnable savePositionTask = new Runnable() {
		public void run() {
			savePosition();
			saveHandler.postDelayed(this, 10000);
		}
	};

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
			onPlayerStop(true);
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
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in onPlayerPrepared()");
			return;
		}
		helper.seekTo(playbackItem.getDurationListenedAtStart());
		helper.play();

		AudioManager am = helper.getAudioManager();
		am.registerMediaButtonEventReceiver(mediaButtonEventReceiver);
		am.registerRemoteControlClient(remoteControlClient);
	}

	private void onPlayerPlay() {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in onPlayerPlay()");
			return;
		}
		startForeground(NOTIFICATION_ID, playbackItem.getNotification());
		saveHandler.post(savePositionTask);

		playbackItem.editMetadata(remoteControlClient);
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

		Notification notification = playbackItem.getNotification();
		notification.contentView.setImageViewResource(R.id.pause,
				R.drawable.ic_notification_pause);
		notification.contentView.setOnClickPendingIntent(R.id.pause,
				pauseIntent);
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	private void onPlayerReset() {
		stopForeground(true);
		playbackItem = null;

		AudioManager am = helper.getAudioManager();
		am.unregisterRemoteControlClient(remoteControlClient);
		am.unregisterMediaButtonEventReceiver(mediaButtonEventReceiver);
	}

	private void onPlayerEnd() {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in onPlayerEnd()");
			return;
		}
		EpisodeHelper.markAsListened(getContentResolver(),
				playbackItem.getUri());
		flattrEpisodeIfAutoPrefIs(net.x4a42.volksempfaenger.Constants.PREF_AUTO_FLATTR_FINISHED);
		onPlayerStop(false);
		savePosition(0);
	}

	private void onPlayerStop(boolean savePosition) {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in onPlayerStop()");
			return;
		}
		saveHandler.removeCallbacks(savePositionTask);
		if (savePosition) {
			savePosition();
		}
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
	}

	private void onPlayerPause() {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in onPlayerPause()");
			return;
		}
		saveHandler.removeCallbacks(savePositionTask);
		savePosition();
		remoteControlClient
				.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);

		Notification notification = playbackItem.getNotification();
		notification.contentView.setImageViewResource(R.id.pause,
				R.drawable.ic_notification_play);
		notification.contentView.setOnClickPendingIntent(R.id.pause,
				getPlayIntent());
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	private void flattrEpisodeIfAutoPrefIs(String flattrAutoPrefValue) {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in flattrEpisodeIfAutoPrefIs()");
			return;
		}
		SharedPreferences prefs = ((VolksempfaengerApplication) getApplication())
				.getSharedPreferences();
		String autoFlattr = prefs.getString(PreferenceKeys.FLATTR_AUTO, null);
		if (autoFlattr != null
				&& playbackItem.getFlattrStatus() == Constants.FLATTR_STATE_NEW
				&& autoFlattr.equals(flattrAutoPrefValue)) {
			EpisodeHelper.flattr(getContentResolver(), playbackItem.getUri());
			Intent intent = new Intent(this, FlattrService.class);
			startService(intent);
		}
	}

	private PendingIntent getPlayIntent() {
		if (playbackItem == null) {
			Log.e(this, "playbackItem is null in getPlayIntent()");
			return null;
		}
		Intent i = new Intent(this, PlaybackService.class);
		i.setAction(ACTION_PLAY);
		i.setData(playbackItem.getUri());
		return PendingIntent.getService(this, 0, i, 0);
	}
}