package net.x4a42.volksempfaenger.service.playback;

import android.app.NotificationManager;
import android.content.Context;

class PlaybackServiceProxyBuilder
{
    public PlaybackServiceProxy build(PlaybackService service)
    {
        BackgroundPositionSaver positionSaver
                = new BackgroundPositionSaverBuilder().build(service);

        Controller controller
                = new ControllerBuilder().build(service);

        IntentParser      intentParser      = new IntentParser();

        MediaButtonReceiver mediaButtonReceiver
                = new MediaButtonReceiverBuilder().build(service);

        MediaSessionManager mediaSessionManager
                = new MediaSessionManagerBuilder().build(service);

        NotificationManager notificationManager
                = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);

        PlaybackNotificationBuilder playbackNotificationBuilder
                = new PlaybackNotificationBuilder(service);

        PlaybackServiceProxy proxy
                = new PlaybackServiceProxy(service,
                                           positionSaver,
                                           controller,
                                           new PlaybackItemBuilder(),
                                           intentParser,
                                           mediaButtonReceiver,
                                           mediaSessionManager,
                                           notificationManager,
                                           playbackNotificationBuilder);

        controller.setListener(proxy);
        intentParser.setListener(proxy);

        return proxy;
    }
}
