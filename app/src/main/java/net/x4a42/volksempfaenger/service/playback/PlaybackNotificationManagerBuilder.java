package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

import net.x4a42.volksempfaenger.PendingIntentBuilder;

class PlaybackNotificationManagerBuilder
{
    public PlaybackNotificationManager build(Service service, PlaybackItem playbackItem)
    {
        Notification                  notification
                = new PlaybackNotificationBuilder().build(service, playbackItem);

        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(service);

        NotificationManager           notificationManager
                = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntentBuilder          pendingIntentBuilder = new PendingIntentBuilder();

        return new PlaybackNotificationManager(service,
                                               notification,
                                               intentProvider,
                                               notificationManager,
                                               pendingIntentBuilder);
    }
}
