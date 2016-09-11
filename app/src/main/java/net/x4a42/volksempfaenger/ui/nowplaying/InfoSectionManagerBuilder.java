package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.x4a42.volksempfaenger.FileBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProviderBuilder;

import java.io.File;

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

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

        // TODO: put "logos" someplace central
        FileBuilder fileBuilder
                = new FileBuilder(new File(context.getExternalFilesDir(null), "logos"));

        InfoSectionManager infoSectionManager
                = new InfoSectionManager(playbackEventReceiver,
                                         playbackIntentProvider,
                                         viewEpisodeIntentProvider,
                                         ImageLoader.getInstance(),
                                         fileBuilder)
                .setFacadeProvider(connectionManager);

        playbackEventReceiver.setListener(infoSectionManager);

        return infoSectionManager;
    }
}
