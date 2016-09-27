package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.misc.ImageLoaderProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

class InfoSectionManagerBuilder
{
    public InfoSectionManager build(Context context, PlaybackServiceConnectionManager connectionManager)
    {
        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build();

        PlaybackServiceIntentProvider playbackIntentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider
                = new ViewEpisodeActivityIntentProviderBuilder().build(context);

        PodcastPathProvider podcastPathProvider
                = new PodcastPathProvider(context);
        ImageLoader imageLoader
                = new ImageLoaderProvider(context).get();

        InfoSectionManager infoSectionManager
                = new InfoSectionManager(playbackEventReceiver,
                                         playbackIntentProvider,
                                         viewEpisodeIntentProvider,
                                         podcastPathProvider,
                                         imageLoader)
                .setFacadeProvider(connectionManager);

        playbackEventReceiver.setListener(infoSectionManager);

        return infoSectionManager;
    }
}
