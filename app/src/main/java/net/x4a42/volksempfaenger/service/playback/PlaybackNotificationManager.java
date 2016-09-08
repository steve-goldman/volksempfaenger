package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.net.Uri;

import net.x4a42.volksempfaenger.R;

class PlaybackNotificationManager
{
    private static final int                    NOTIFICATION_ID = 0x59d54313;

    private final Service                       service;
    private final Notification                  notification;
    private final PlaybackServiceIntentProvider intentProvider;
    private final NotificationManager           notificationManager;

    public PlaybackNotificationManager(Service                       service,
                                       Notification                  notification,
                                       PlaybackServiceIntentProvider intentProvider,
                                       NotificationManager           notificationManager)
    {
        this.service             = service;
        this.notification        = notification;
        this.intentProvider      = intentProvider;
        this.notificationManager = notificationManager;
    }

    public void updateForPlay()
    {
        service.startForeground(NOTIFICATION_ID, notification);
        update(R.drawable.ic_notification_pause, getPausePendingIntent());
    }

    public void updateForPause(PlaybackItem playbackItem)
    {
        service.stopForeground(false);
        update(R.drawable.ic_notification_play, getPlayPendingIntent(playbackItem.getEpisodeUri()));
    }

    public void remove()
    {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void update(int drawable, PendingIntent pendingIntent)
    {
        notification.contentView.setImageViewResource(R.id.pause, drawable);
        notification.contentView.setOnClickPendingIntent(R.id.pause, pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private PendingIntent getPausePendingIntent()
    {
        return PendingIntent.getService(service, 0, intentProvider.getPauseIntent(), 0);
    }

    private PendingIntent getPlayPendingIntent(Uri episodeUri)
    {
        return PendingIntent.getService(service, 0, intentProvider.getPlayIntent(episodeUri), 0);
    }

}
