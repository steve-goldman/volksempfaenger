package net.x4a42.volksempfaenger.service.playback;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;

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

        PlaybackServiceProxy proxy
                = new PlaybackServiceProxy(service,
                                           positionSaver,
                                           controller,
                                           new PlaybackItemBuilder(),
                                           intentParser,
                                           mediaButtonReceiver,
                                           mediaSessionManager,
                                           new PlaybackNotificationManagerBuilder());

        controller.setListener(proxy);
        intentParser.setListener(proxy);

        return proxy;
    }
}
