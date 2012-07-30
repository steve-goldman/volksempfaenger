package net.x4a42.volksempfaenger.service;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.PowerManager;

public class PlaybackHelper implements OnPreparedListener,
		OnCompletionListener, OnAudioFocusChangeListener {

	private Context context;
	private AudioManager audioManager;
	private MediaPlayer player = new MediaPlayer();
	private AudioNoisyReceiver audioNoisyReceiver = new AudioNoisyReceiver();
	private List<EventListener> eventListeners = new Vector<EventListener>();
	private boolean requestedAutoPlay;

	public static enum Event {
		RESET, PREPARED, PLAY, PAUSE, STOP, END, DESTROY
	}

	public interface EventListener {
		public void onPlaybackEvent(Event event);
	}

	public PlaybackHelper(Context context) {
		this.context = context;
		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		context.registerReceiver(audioNoisyReceiver, new IntentFilter(
				AudioManager.ACTION_AUDIO_BECOMING_NOISY));
		player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	/**
	 * Reset the player.
	 */
	public void reset() {
		player.reset();
		emitEvent(Event.RESET);
	}

	public void open(String file) throws IOException {
		open(file, false);
	}

	public void open(String file, boolean autoPlay) throws IOException {
		reset();
		requestedAutoPlay = autoPlay;
		player.setDataSource(file);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.prepareAsync();
	}

	public void play() {
		audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
		// TODO maybe we should do something special when we can't get audio
		// focus
		player.start();
		emitEvent(Event.PLAY);
	}

	public void pause() {
		player.pause();
		emitEvent(Event.PAUSE);
	}

	public void stop() {
		player.stop();
		emitEvent(Event.STOP);
	}

	public void seekTo(int position) {
		player.seekTo(position);
	}

	public int getDuration() {
		return player.getDuration();
	}

	public int getPosition() {
		return player.getCurrentPosition();
	}

	public void movePosition(int offset) {
		int position = getPosition() + offset;
		if (position < 0) {
			position = 0;
		} else if (position > getDuration()) {
			position = getDuration();
		}
		seekTo(position);
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}

	/**
	 * This method should be called from the onDestroy method of the
	 * service/context.
	 */
	public void destroy() {
		emitEvent(Event.DESTROY);
		context.unregisterReceiver(audioNoisyReceiver);
		player.release();
	}

	/**
	 * Adds an EventListener.
	 * 
	 * @param listener
	 *            The EventListener to add.
	 */
	public void registerEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	/**
	 * Removes an EventListener.
	 * 
	 * @param listener
	 *            The EventListener to remove.
	 */
	public void unregisterEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

	/**
	 * Emits an Event.
	 * 
	 * @param event
	 *            The Event to emit.
	 */
	private void emitEvent(Event event) {
		for (EventListener listener : eventListeners) {
			listener.onPlaybackEvent(event);
		}
	}

	private class AudioNoisyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent
					.getAction())) {
				if (player.isPlaying()) {
					player.pause();
					emitEvent(Event.PAUSE);
				}
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		emitEvent(Event.END);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		emitEvent(Event.PREPARED);
		if (requestedAutoPlay) {
			play();
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		switch (focusChange) {
		case AudioManager.AUDIOFOCUS_GAIN:
			player.setVolume(1.0f, 1.0f);
			break;

		case AudioManager.AUDIOFOCUS_LOSS:
			if (isPlaying()) {
				stop();
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			if (isPlaying()) {
				pause();
			}
			break;

		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			if (player.isPlaying()) {
				player.setVolume(0.1f, 0.1f);
			}
			break;
		}

	}

}