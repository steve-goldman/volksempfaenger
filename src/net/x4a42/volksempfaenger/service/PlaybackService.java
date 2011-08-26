package net.x4a42.volksempfaenger.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class PlaybackService extends Service implements OnPreparedListener {
	private final String TAG = getClass().getSimpleName();
	private MediaPlayer player;
	private OnPreparedListener customListener;

	private static enum PlayerState {
		IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, ERROR
	}

	private PlayerState playerState = PlayerState.IDLE;

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
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
		if (player != null) {
			player.release();
			player = null;
		}
	}

	public void playFile(String path, MediaPlayer.OnPreparedListener listener)
			throws IllegalArgumentException, IOException {
		if (playerState != PlayerState.IDLE) {
			resetPlayer();
		}
		player.setDataSource(path);
		playerState = PlayerState.INITIALIZED;
		playerState = PlayerState.PREPARING;
		player.setOnPreparedListener(this);
		customListener = listener;
		player.prepareAsync();
	}

	public void play() {
		if (playerState == PlayerState.PAUSED
				|| playerState == PlayerState.PREPARED) {
			player.start();
			playerState = PlayerState.STARTED;
		} else {
			Log.e(TAG,
					"Unable to play: player has neither been 'paused' nor 'prepared'");
		}
	}

	public void pause() {
		if (playerState == PlayerState.STARTED) {
			player.pause();
			playerState = PlayerState.PAUSED;
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

	private void resetPlayer() {
		player.release();
		player.reset();
		playerState = PlayerState.IDLE;
	}

	public void onPrepared(MediaPlayer mp) {
		playerState = PlayerState.PREPARED;
		customListener.onPrepared(null);
	}
}
