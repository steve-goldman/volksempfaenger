package net.x4a42.volksempfaenger.receiver;

import net.x4a42.volksempfaenger.service.PlaybackService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaButtonEventReceiver extends BroadcastReceiver {
	static boolean ignoreNext = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
			return;
		}

		final KeyEvent event = (KeyEvent) intent
				.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

		if (event == null) {
			return;
		}
		// FIXME
		// intents are being received twice
		// we do not know why, so ignore every second intent
		if (ignoreNext) {
			ignoreNext = false;
			return;
		} else {
			ignoreNext = true;
		}
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MEDIA_PLAY:
			break;
		case KeyEvent.KEYCODE_MEDIA_PAUSE:
			break;
		case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			Intent keyIntent = new Intent(context, PlaybackService.class);
			keyIntent.setAction(Intent.ACTION_MEDIA_BUTTON);
			keyIntent.putExtra(Intent.EXTRA_KEY_EVENT, event);
			context.startService(keyIntent);
			break;
		default:
			return;
		}
	}
}
