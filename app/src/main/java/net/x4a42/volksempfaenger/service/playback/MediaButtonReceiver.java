package net.x4a42.volksempfaenger.service.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

class MediaButtonReceiver extends BroadcastReceiver
{
    private final Context                       context;
    private final PlaybackServiceIntentProvider intentProvider;

    public MediaButtonReceiver(Context context, PlaybackServiceIntentProvider intentProvider)
    {
        this.context        = context;
        this.intentProvider = intentProvider;
    }

    public void destroy()
    {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String intentAction = intent.getAction();
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intentAction))
        {
            return;
        }

        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (event == null)
        {
            return;
        }

        int eventAction = event.getAction();
        if (eventAction == KeyEvent.ACTION_UP)
        {
            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode())
            {
                context.startService(intentProvider.getPlayPauseIntent());
            }
        }
    }
}
