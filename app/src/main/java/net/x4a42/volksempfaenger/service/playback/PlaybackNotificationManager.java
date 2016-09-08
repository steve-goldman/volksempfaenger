package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.net.Uri;

import net.x4a42.volksempfaenger.PendingIntentBuilder;
import net.x4a42.volksempfaenger.R;

class PlaybackNotificationManager
{
    public static final int                     NotificationId = 0x59d54313;

    private final Service                       service;
    private final Notification                  notification;
    private final PlaybackServiceIntentProvider intentProvider;
    private final NotificationManager           notificationManager;
    private final PendingIntentBuilder          pendingIntentBuilder;

    public PlaybackNotificationManager(Service                       service,
                                       Notification                  notification,
                                       PlaybackServiceIntentProvider intentProvider,
                                       NotificationManager           notificationManager,
                                       PendingIntentBuilder          pendingIntentBuilder)
    {
        this.service              = service;
        this.notification         = notification;
        this.intentProvider       = intentProvider;
        this.notificationManager  = notificationManager;
        this.pendingIntentBuilder = pendingIntentBuilder;
    }

    public void updateForPlay()
    {
        service.startForeground(NotificationId, notification);
        update(R.drawable.ic_notification_pause, getPausePendingIntent());
    }

    public void updateForPause(PlaybackItem playbackItem)
    {
        service.stopForeground(false);
        update(R.drawable.ic_notification_play, getPlayPendingIntent(playbackItem.getEpisodeUri()));
    }

    public void remove()
    {
        service.stopForeground(true);
    }

    private void update(int drawable, PendingIntent pendingIntent)
    {
        notification.contentView.setImageViewResource(R.id.pause, drawable);
        notification.contentView.setOnClickPendingIntent(R.id.pause, pendingIntent);
        notificationManager.notify(NotificationId, notification);
    }

    private PendingIntent getPausePendingIntent()
    {
        return pendingIntentBuilder.buildService(service, 0, intentProvider.getPauseIntent(), 0);
    }

    private PendingIntent getPlayPendingIntent(Uri episodeUri)
    {
        return pendingIntentBuilder.buildService(service, 0, intentProvider.getPlayIntent(episodeUri), 0);
    }

}
