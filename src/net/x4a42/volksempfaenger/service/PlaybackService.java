package net.x4a42.volksempfaenger.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.DatabaseHelper;
import net.x4a42.volksempfaenger.ui.ViewEpisodeActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements OnPreparedListener,
		OnAudioFocusChangeListener, OnCompletionListener {

	private static final int NOTIFICATION_ID = 0x59d54313;
	private final String TAG = getClass().getSimpleName();

	private MediaPlayer player;
	private Notification notification;
	private NotificationManager notificationManager;
	private AudioManager audioManager;
	private AudioNoisyReceiver audioNoisyReceiver;
	private DatabaseHelper dbHelper;
	private Cursor cursor;
	
	private Handler saveHandler;

	private static enum PlayerState {
		IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, ERROR
	}

	private PlayerState playerState = PlayerState.IDLE;
	private PlayerListener playerListener = new DefaultPlayerListener();
	private long enclosureId;

	private long getEpisodeId() {
		return cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.ID));
	}

	private long getEnclosureId() {
		return cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_ID));
	}

	private String getPodcastTitle() {
		return cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.PODCAST_TITLE));
	}

	private String getEpisodeTitle() {
		return cursor.getString(cursor
				.getColumnIndex(DatabaseHelper.ExtendedEpisode.EPISODE_TITLE));
	}

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioNoisyReceiver = new AudioNoisyReceiver();
		registerReceiver(audioNoisyReceiver, new IntentFilter(
				AudioManager.ACTION_AUDIO_BECOMING_NOISY));
		dbHelper = DatabaseHelper.getInstance(this);
		saveHandler = new Handler();
	}

	public class PlaybackBinder extends Binder {
		public PlaybackService getService() {
			return PlaybackService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new PlaybackBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(audioNoisyReceiver);
		if (player != null) {
			player.release();
			player = null;
		}
	}

	private void playFile(String path) throws IllegalArgumentException,
			IOException {
		if (playerState != PlayerState.IDLE) {
			resetPlayer();
		}
		player.setDataSource(path);
		playerState = PlayerState.INITIALIZED;
		playerState = PlayerState.PREPARING;
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.prepareAsync();
	}

	public void playEpisode(long episodeId) throws IllegalArgumentException,
			IOException {
		// TODO Auto-generated method stub
		cursor = dbHelper.getReadableDatabase().query(
				DatabaseHelper.ExtendedEpisode._TABLE, null,
				String.format("%s = ?", DatabaseHelper.ExtendedEpisode.ID),
				new String[] { String.valueOf(episodeId) }, null, null, null);
		if (!cursor.moveToFirst()) {
			throw new IllegalArgumentException("Episode not found");
		}
		File enclosureFile;
		try {
			enclosureFile = new File(
					new URI(
							cursor.getString(cursor
									.getColumnIndex(DatabaseHelper.ExtendedEpisode.ENCLOSURE_FILE))));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Episode not found");
		}
		if (!enclosureFile.isFile()) {
			throw new IllegalArgumentException("Episode not found");
		}
		enclosureId = getEnclosureId();
		playFile(enclosureFile.getAbsolutePath());
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Episode.STATE,
				DatabaseHelper.Episode.STATE_LISTENING);
		updateEpisode(values);
	}

	private void updateEpisode(ContentValues values) {
		dbHelper.getWritableDatabase().update(DatabaseHelper.Episode._TABLE,
				values, String.format("%s = ?", DatabaseHelper.Episode.ID),
				new String[] { String.valueOf(getEpisodeId()) });
	}

	private void updateEnclosure(ContentValues values) {
		dbHelper.getWritableDatabase().update(DatabaseHelper.Enclosure._TABLE,
				values, String.format("%s = ?", DatabaseHelper.Enclosure.ID),
				new String[] { String.valueOf(getEnclosureId()) });
	}

	public void play() {
		if (playerState == PlayerState.PAUSED
				|| playerState == PlayerState.PREPARED) {
			audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN);
			// TODO maybe we should do something special when we can't get audio
			// focus
			player.start();
			playerState = PlayerState.STARTED;
			startForeground();
			saveHandler.post(savePositionTask);
		} else {
			Log.e(TAG,
					"Unable to play: player has neither been 'paused' nor 'prepared'");
		}
	}

	public void pause() {
		if (playerState == PlayerState.STARTED) {
			saveHandler.removeCallbacks(savePositionTask);
			player.pause();
			playerState = PlayerState.PAUSED;
			savePosition();
			stopForeground();
		} else {
			Log.e(TAG, "Unable to pause: player has not been 'started'");
		}
	}

	public boolean isPlaying() {
		if (playerState == PlayerState.STARTED) {
			return true;
		} else {
			return false;
		}
	}

	public int getDuration() {
		if (playerState != PlayerState.IDLE) {
			return player.getDuration();
		} else {
			Log.e(TAG, "No duration: player is 'idle'");
			return 0;
		}
	}

	public int getCurrentPosition() {
		if (playerState != PlayerState.IDLE) {
			return player.getCurrentPosition();
		} else {
			Log.e(TAG, "No position: player is 'idle'");
			return 0;
		}
	}

	public void seekTo(int position) {
		if (playerState == PlayerState.STARTED
				|| playerState == PlayerState.PAUSED) {
			player.seekTo(position);
		} else {
			Log.e(TAG, "Unable to seek: player is neither playing nor 'paused'");
		}
	}

	public void stop() {
		stop(false);
	}

	public void stop(boolean completed) {
		if (playerState == PlayerState.STARTED || playerState == PlayerState.PAUSED || completed) {
			saveHandler.removeCallbacks(savePositionTask);
			if (completed) {
				savePosition(0);
			} else {
				savePosition();
			}
			stopForeground();
			enclosureId = -1;
			playerListener.onPlayerStopped();
			resetPlayer();
		} else {
			Log.e(TAG, "Unable to stop: player is not playing");
		}
	}

	public long getCurrentEpisode() {
		return getEpisodeId();
	}

	private void resetPlayer() {
		player.reset();
		playerState = PlayerState.IDLE;
		stopForeground();
	}

	public void onPrepared(MediaPlayer mp) {
		playerState = PlayerState.PREPARED;
		notification = new Notification(R.drawable.notification, null,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent notificationIntent = new Intent(this, ViewEpisodeActivity.class);

		notificationIntent.putExtra("id", getEpisodeId());
		PendingIntent pendingIntent = PendingIntent.getActivity(
				playerListener.getContext(), 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, getEpisodeTitle(),
				getPodcastTitle(), pendingIntent);
		startForeground();
		playerListener.onPlayerPrepared();
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

	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:
			if (player != null) {
				player.setVolume(1.0f, 1.0f);
			}
			break;
		case AudioManager.AUDIOFOCUS_LOSS:
			if (playerState == PlayerState.STARTED) {
				stop();
				// TODO notify activity
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			if (player != null && player.isPlaying()) {
				player.pause();
				// TODO notify activity
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			if (player != null && player.isPlaying()) {
				player.setVolume(0.1f, 0.1f);
			}
			break;
		}
	}

	public void setPlayerListener(PlayerListener listener) {
		playerListener = listener;
	}

	public interface PlayerListener {
		public void onPlayerPaused();

		public void onPlayerStopped();

		public void onPlayerPrepared();

		public Context getContext();
	}

	private class AudioNoisyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
				if (PlaybackService.this.isPlaying()) {
					PlaybackService.this.pause();
					PlaybackService.this.playerListener.onPlayerPaused();
				}
			}
		}
	}

	private class DefaultPlayerListener implements PlayerListener {
		public void onPlayerPaused() {
			Log.d(TAG, "No PlayerListener set");
		}

		public void onPlayerStopped() {
			Log.d(TAG, "No PlayerListener set");
		}

		public void onPlayerPrepared() {
			Log.d(TAG, "No PlayerListener set");
		}

		public Context getContext() {
			return PlaybackService.this;
		}

	}

	public void onCompletion(MediaPlayer mp) {
		stop(true);
	}

	private void savePosition() {
		if (playerState == PlayerState.STARTED
				|| playerState == PlayerState.PAUSED) {
			savePosition(player.getCurrentPosition());
		}
	}

	private void savePosition(long position) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.Enclosure.DURATION_LISTENED, position);
		updateEnclosure(values);
	}

	private Runnable savePositionTask = new Runnable() {
		public void run() {
			savePosition();
			saveHandler.postDelayed(this, 500);
		}
	};
}