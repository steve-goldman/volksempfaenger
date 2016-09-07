package net.x4a42.volksempfaenger.service.playback;

import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;

public class PlaybackServiceProxyBuilder
{
    public PlaybackServiceProxy build(PlaybackService service)
    {
        PlaybackBackgroundPositionSaver positionSaver
                = new PlaybackBackgroundPositionSaverBuilder().build(service);

        PlaybackController controller
                = new PlaybackControllerBuilder().build(service);

        PlaybackServiceIntentParser     intentParser        = new PlaybackServiceIntentParser();

        EpisodeDataHelper episodeDataHelper   = new EpisodeDataHelperBuilder().build(service);

        PlaybackMediaButtonReceiver     mediaButtonReceiver
                = new PlaybackMediaButtonReceiverBuilder().build(service);

        MediaSessionManager             mediaSessionManager
                = new MediaSessionManagerBuilder().build(service);

        PlaybackServiceProxy proxy = new PlaybackServiceProxy(service,
                                                              positionSaver,
                                                              controller,
                                                              new PlaybackItemBuilder(),
                                                              intentParser,
                                                              episodeDataHelper,
                                                              mediaButtonReceiver,
                                                              mediaSessionManager,
                                                              new PlaybackNotificationManagerBuilder());

        controller.setListener(proxy);
        intentParser.setListener(proxy);

        return proxy;
    }
}
