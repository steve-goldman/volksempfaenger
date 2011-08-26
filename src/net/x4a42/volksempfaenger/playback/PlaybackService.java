package net.x4a42.volksempfaenger.playback;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlaybackService extends Service {
	private MediaPlayer player;
	private static enum PlayerState {
		IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, ERROR, END
	}
	private PlayerState playerState = PlayerState.IDLE;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(player != null) {
			player.release();
			player = null;
		}
	}
}
