package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManagerBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;

class ViewEpisodeActivityProxyBuilder
{
    public ViewEpisodeActivityProxy build(ViewEpisodeActivity activity)
    {
        IntentParser      intentParser = new IntentParser(activity.getIntent());
        EpisodeDaoWrapper episodeDao   = new EpisodeDaoBuilder().build(activity);
        Episode           episode      = episodeDao.getById(intentParser.getEpisodeId());

        FragmentManager fragmentManager
                = activity.getFragmentManager();

        OptionsMenuManager optionsMenuManager
                = new OptionsMenuManager(episode, activity.getMenuInflater());

        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build();

        HtmlConverter converter         = new HtmlConverter();

        Presenter presenter
                = new Presenter(activity, episode, converter);

        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(activity);

        ToastMaker      toastMaker      = new ToastMaker(activity);

        NavUtilsWrapper navUtilsWrapper = new NavUtilsWrapper(activity);

        IntentBuilder   intentBuilder   = new IntentBuilder();

        EpisodeSharer   sharer          = new EpisodeSharer(activity, episode, intentBuilder, converter);

        DownloadHelper  downloadHelper  = new DownloadHelperBuilder().build(activity, episode);

        PlaybackServiceConnectionManager connectionManager
                = new PlaybackServiceConnectionManagerBuilder().build(activity);

        ViewEpisodeActivityProxy proxy
                = new ViewEpisodeActivityProxy(activity,
                                               episode,
                                               fragmentManager,
                                               optionsMenuManager,
                                               playbackEventReceiver,
                                               presenter,
                                               intentProvider,
                                               toastMaker,
                                               navUtilsWrapper,
                                               sharer,
                                               intentBuilder,
                                               downloadHelper,
                                               connectionManager);

        optionsMenuManager
                .setListener(proxy)
                .setFacadeProvider(connectionManager);

        playbackEventReceiver.setListener(proxy);

        return proxy;
    }
}
