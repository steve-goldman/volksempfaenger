package net.x4a42.volksempfaenger.service.playback;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.ui.MainActivity;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class PlaybackNotificationBuilder
{
    private final Context context;

    public PlaybackNotificationBuilder(Context context)
    {
        this.context = context;
    }

    public Notification build(Episode playbackEpisode, boolean isPlaying)
    {
        TaskStackBuilder taskStackBuilder = buildTaskStack(context, playbackEpisode);
        Bitmap           podcastLogo      = buildPodcastLogo(context, playbackEpisode);
        RemoteViews      content          = buildContent(context, playbackEpisode, podcastLogo, isPlaying);

        return buildNotification(context, playbackEpisode, taskStackBuilder, content);
    }

    private TaskStackBuilder buildTaskStack(Context context, Episode playbackEpisode)
    {
        // Build back stack as proposed in
        // "Google I/O 2012 - Navigation in Android", see
        // http://www.youtube.com/watch?v=XwGHJJYBs0Q
        Intent intent;
        TaskStackBuilder taskBuilder = TaskStackBuilder.create(context);

        // MainActivity
        intent = new Intent(context, MainActivity.class);
        taskBuilder.addNextIntent(intent);

        // ViewEpisodeActivity
        intent = new ViewEpisodeActivityIntentProviderBuilder().build(context).getIntent(playbackEpisode);
        taskBuilder.addNextIntent(intent);

        return taskBuilder;
    }

    private Bitmap buildPodcastLogo(Context context, Episode playbackEpisode)
    {
        // Get the podcast logo and scale it
        Resources res = context.getResources();
        return Utils.getPodcastLogoBitmap(context, playbackEpisode.getPodcastId(),
                                          res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                                          res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height));

    }

    private RemoteViews buildContent(Context context, Episode playbackEpisode, Bitmap podcastLogo, boolean isPlaying)
    {
        // Build the layout for the notification
        RemoteViews content = new RemoteViews(context.getPackageName(), R.layout.notification_playing);

        if (podcastLogo != null)
        {
            content.setImageViewBitmap(R.id.podcast_logo, podcastLogo);
        }

        content.setTextViewText(R.id.episode_title, playbackEpisode.getTitle());
        content.setTextViewText(R.id.podcast_title, playbackEpisode.getPodcast().getTitle());

        PlaybackServiceIntentProvider intentProvider = new PlaybackServiceIntentProviderBuilder().build(context);

        if (isPlaying)
        {
            content.setImageViewResource(R.id.pause, R.drawable.ic_notification_pause);
            content.setOnClickPendingIntent(
                    R.id.pause,
                    PendingIntent.getService(context, 0, intentProvider.getPauseIntent(), 0));
        }
        else
        {
            content.setImageViewResource(R.id.pause, R.drawable.ic_notification_play);
            content.setOnClickPendingIntent(
                    R.id.pause,
                    PendingIntent.getService(context, 0, intentProvider.getPlayIntent(playbackEpisode), 0));
        }

        content.setOnClickPendingIntent(
                R.id.collapse, PendingIntent.getService(context, 0, intentProvider.getStopIntent(), 0));

        return content;
    }

    private Notification buildNotification(Context          context,
                                           Episode          playbackEpisode,
                                           TaskStackBuilder taskStackBuilder,
                                           RemoteViews      content)
    {
        return Utils.notificationFromBuilder(
                new Notification.Builder(context)
                        .setContent(content)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(playbackEpisode.getTitle())
                        .setContentText(playbackEpisode.getPodcast().getTitle())
                        .setContentIntent(taskStackBuilder.getPendingIntent(0, 0))
                        .setOngoing(true).setWhen(0));
    }

}
