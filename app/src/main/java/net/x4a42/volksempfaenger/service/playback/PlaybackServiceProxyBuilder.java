package net.x4a42.volksempfaenger.service.playback;

import android.app.NotificationManager;
import android.content.Context;

import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episodeposition.EpisodePositionDaoWrapper;
import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEventReceiver;
import net.x4a42.volksempfaenger.service.playlistdownload.EpisodeDownloadEventReceiverBuilder;

class PlaybackServiceProxyBuilder
{
    public PlaybackServiceProxy build(PlaybackService service)
    {
        EpisodePositionDaoWrapper episodePositionDao
                = new EpisodePositionDaoBuilder().build(service);

        Controller controller
                = new ControllerBuilder().build(service, episodePositionDao);

        BackgroundPositionSaver positionSaver
                = new BackgroundPositionSaverBuilder().build(controller, episodePositionDao);

        IntentParser intentParser = new IntentParser();

        MediaButtonReceiver mediaButtonReceiver
                = new MediaButtonReceiverBuilder().build(service);

        MediaSessionManager mediaSessionManager
                = new MediaSessionManagerBuilder().build(service);

        NotificationManager notificationManager
                = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        PlaybackNotificationBuilder playbackNotificationBuilder
                = new PlaybackNotificationBuilder(service);

        Playlist playlist = new PlaylistProvider(service).get();

        PlaybackServiceProxy proxy
                = new PlaybackServiceProxy(positionSaver,
                                           controller,
                                           intentParser,
                                           mediaButtonReceiver,
                                           mediaSessionManager,
                                           notificationManager,
                                           playbackNotificationBuilder,
                                           playlist);

        controller.setListener(proxy);
        intentParser.setListener(proxy);

        return proxy;
    }
}
