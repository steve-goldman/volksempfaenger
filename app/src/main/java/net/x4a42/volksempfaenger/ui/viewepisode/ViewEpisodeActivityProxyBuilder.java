package net.x4a42.volksempfaenger.ui.viewepisode;

import android.net.Uri;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.ToastMaker;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorLoader;
import net.x4a42.volksempfaenger.data.episode.EpisodeCursorLoaderBuilder;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;

public class ViewEpisodeActivityProxyBuilder
{
    public ViewEpisodeActivityProxy build(ViewEpisodeActivity activity)
    {
        Uri episodeUri
                = new ViewEpisodeIntentWrapper(activity.getIntent()).getEpisodeUri();

        ViewEpisodeOptionsMenuManager optionsMenuManager
                = new ViewEpisodeOptionsMenuManager(episodeUri, activity.getMenuInflater());

        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build(activity);

        EpisodeCursorLoader episodeCursorLoader
                = new EpisodeCursorLoaderBuilder().build(activity, episodeUri);

        HtmlConverter converter         = new HtmlConverter();

        ViewEpisodePresenter presenter
                = new ViewEpisodePresenter(activity, converter);

        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(activity);

        EpisodeDataHelper episodeDataHelper
                = new EpisodeDataHelperBuilder().build(activity);

        ToastMaker      toastMaker      = new ToastMaker(activity);

        NavUtilsWrapper navUtilsWrapper = new NavUtilsWrapper(activity);

        IntentBuilder   intentBuilder   = new IntentBuilder();

        EpisodeSharer   sharer          = new EpisodeSharer(activity, intentBuilder, converter);

        DownloadHelper  downloadHelper  = new DownloadHelperBuilder().build(activity, episodeUri);

        ViewEpisodeActivityProxy proxy
                = new ViewEpisodeActivityProxy(activity,
                                               episodeUri,
                                               optionsMenuManager,
                                               playbackEventReceiver,
                                               episodeCursorLoader,
                                               presenter,
                                               intentProvider,
                                               episodeDataHelper,
                                               toastMaker,
                                               navUtilsWrapper,
                                               sharer,
                                               intentBuilder,
                                               downloadHelper);

        optionsMenuManager
                .setListener(proxy)
                .setCursorProvider(proxy)
                .setFacadeProvider(proxy);

        playbackEventReceiver.setListener(proxy);

        episodeCursorLoader.setListener(proxy);

        return proxy;
    }
}
