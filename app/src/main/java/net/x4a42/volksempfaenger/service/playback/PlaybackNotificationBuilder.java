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
import net.x4a42.volksempfaenger.ui.MainActivity;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivity;
import net.x4a42.volksempfaenger.ui.ViewSubscriptionActivity;

class PlaybackNotificationBuilder
{
    private final Context context;

    public PlaybackNotificationBuilder(Context context)
    {
        this.context = context;
    }

    public Notification build(PlaybackItem playbackItem, boolean isPlaying)
    {
        TaskStackBuilder taskStackBuilder = buildTaskStack(context, playbackItem);
        Bitmap           podcastLogo      = buildPodcastLogo(context, playbackItem);
        RemoteViews      content          = buildContent(context, playbackItem, podcastLogo, isPlaying);

        return buildNotification(context, playbackItem, taskStackBuilder, content);
    }

    private TaskStackBuilder buildTaskStack(Context context, PlaybackItem playbackItem)
    {
        // Build back stack as proposed in
        // "Google I/O 2012 - Navigation in Android", see
        // http://www.youtube.com/watch?v=XwGHJJYBs0Q
        Intent intent;
        TaskStackBuilder taskBuilder = TaskStackBuilder.create(context);

        // MainActivity
        intent = new Intent(context, MainActivity.class);
        taskBuilder.addNextIntent(intent);

        // ViewSubscriptionActivity
        intent = new Intent(context, ViewSubscriptionActivity.class);
        intent.setData(playbackItem.getEpisodeUri());
        taskBuilder.addNextIntent(intent);

        // ViewEpisodeActivity
        intent = new Intent(context, ViewEpisodeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(playbackItem.getEpisodeUri());
        taskBuilder.addNextIntent(intent);

        return taskBuilder;
    }

    private Bitmap buildPodcastLogo(Context context, PlaybackItem playbackItem)
    {
        // Get the podcast logo and scale it
        Resources res = context.getResources();
        return Utils.getPodcastLogoBitmap(context, playbackItem.getPodcastId(),
                                          res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                                          res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height));

    }

    private RemoteViews buildContent(Context context, PlaybackItem playbackItem, Bitmap podcastLogo, boolean isPlaying)
    {
        // Build the layout for the notification
        RemoteViews content = new RemoteViews(context.getPackageName(), R.layout.notification_playing);

        if (podcastLogo != null)
        {
            content.setImageViewBitmap(R.id.podcast_logo, podcastLogo);
        }

        content.setTextViewText(R.id.episode_title, playbackItem.getTitle());
        content.setTextViewText(R.id.podcast_title, playbackItem.getPodcastTitle());

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
                    PendingIntent.getService(context, 0, intentProvider.getPlayIntent(playbackItem.getEpisodeUri()), 0));
        }

        content.setOnClickPendingIntent(
                R.id.collapse, PendingIntent.getService(context, 0, intentProvider.getStopIntent(), 0));

        return content;
    }

    private Notification buildNotification(Context          context,
                                           PlaybackItem     playbackItem,
                                           TaskStackBuilder taskStackBuilder,
                                           RemoteViews      content)
    {
        return Utils.notificationFromBuilder(
                new Notification.Builder(context)
                        .setContent(content)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(playbackItem.getTitle())
                        .setContentText(playbackItem.getPodcastTitle())
                        .setContentIntent(taskStackBuilder.getPendingIntent(0, 0))
                        .setOngoing(true).setWhen(0));
    }

}
