package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import net.x4a42.volksempfaenger.PendingIntentBuilder;
import net.x4a42.volksempfaenger.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PlaybackNotificationManagerTest
{
    Service                       service              = Mockito.mock(Service.class);
    Uri                           episodeUri           = Mockito.mock(Uri.class);
    PlaybackItem                  playbackItem         = Mockito.mock(PlaybackItem.class);
    Notification                  notification         = Mockito.mock(Notification.class);
    RemoteViews                   contentView          = Mockito.mock(RemoteViews.class);
    PlaybackServiceIntentProvider intentProvider       = Mockito.mock(PlaybackServiceIntentProvider.class);
    Intent                        playIntent           = Mockito.mock(Intent.class);
    Intent                        pauseIntent          = Mockito.mock(Intent.class);
    NotificationManager           notificationManager  = Mockito.mock(NotificationManager.class);
    PendingIntentBuilder          pendingIntentBuilder = Mockito.mock(PendingIntentBuilder.class);
    PendingIntent                 playPendingIntent    = Mockito.mock(PendingIntent.class);
    PendingIntent                 pausePendingIntent   = Mockito.mock(PendingIntent.class);
    PlaybackNotificationManager   playbackNotificationManager;

    @Before
    public void setUp() throws Exception
    {
        notification.contentView = contentView;
        Mockito.when(playbackItem.getEpisodeUri()).thenReturn(episodeUri);
        Mockito.when(intentProvider.getPlayIntent(episodeUri)).thenReturn(playIntent);
        Mockito.when(intentProvider.getPauseIntent()).thenReturn(pauseIntent);
        Mockito.when(pendingIntentBuilder.buildService(service, 0, playIntent, 0)).thenReturn(playPendingIntent);
        Mockito.when(pendingIntentBuilder.buildService(service, 0, pauseIntent, 0)).thenReturn(pausePendingIntent);

        playbackNotificationManager = new PlaybackNotificationManager(service,
                                                                      notification,
                                                                      intentProvider,
                                                                      notificationManager,
                                                                      pendingIntentBuilder);
    }

    @Test
    public void testUpdateForPlay() throws Exception
    {
        playbackNotificationManager.updateForPlay();

        Mockito.verify(service).startForeground(PlaybackNotificationManager.NotificationId,
                                                notification);

        Mockito.verify(contentView).setImageViewResource(R.id.pause,
                                                         R.drawable.ic_notification_pause);

        Mockito.verify(contentView).setOnClickPendingIntent(R.id.pause, pausePendingIntent);

        Mockito.verify(notificationManager).notify(PlaybackNotificationManager.NotificationId, notification);
    }

    @Test
    public void testUpdateForPause() throws Exception
    {
        playbackNotificationManager.updateForPause(playbackItem);

        Mockito.verify(service).stopForeground(false);

        Mockito.verify(contentView).setImageViewResource(R.id.pause,
                                                         R.drawable.ic_notification_play);

        Mockito.verify(contentView).setOnClickPendingIntent(R.id.pause, playPendingIntent);

        Mockito.verify(notificationManager).notify(PlaybackNotificationManager.NotificationId, notification);
    }

    @Test
    public void testRemove() throws Exception
    {
        playbackNotificationManager.remove();

        Mockito.verify(service).stopForeground(true);
    }
}