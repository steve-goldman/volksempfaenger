package net.x4a42.volksempfaenger.service.playback;

import android.app.NotificationManager;
import android.content.Context;

import net.x4a42.volksempfaenger.data.playlist.Playlist;
import net.x4a42.volksempfaenger.data.playlist.PlaylistProvider;

class PlaybackServiceProxyBuilder
{
    public PlaybackServiceProxy build(PlaybackService service)
    {
        Controller controller
                = new ControllerBuilder().build(service);

        BackgroundPositionSaver positionSaver
                = new BackgroundPositionSaverBuilder().build(service, controller);

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
