package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.FragmentManager;

import net.x4a42.volksempfaenger.HtmlConverter;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.EpisodeDaoWrapper;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManagerBuilder;

class ViewEpisodeActivityProxyBuilder
{
    public ViewEpisodeActivityProxy build(ViewEpisodeActivity activity)
    {
        IntentParser      intentParser  = new IntentParser(activity.getIntent());
        EpisodeDaoWrapper episodeDao    = new EpisodeDaoBuilder().build(activity);
        Episode           episode       = episodeDao.getById(intentParser.getEpisodeId());
        HtmlConverter     converter     = new HtmlConverter();
        Presenter         presenter     = new Presenter(activity, episode, converter);

        FragmentManager fragmentManager
                = activity.getFragmentManager();

        OptionsMenuManager optionsMenuManager
                = new OptionsMenuManagerBuilder().build(activity, episode);

        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build();


        PlaybackServiceConnectionManager connectionManager
                = new PlaybackServiceConnectionManagerBuilder().build(activity);

        ViewEpisodeActivityProxy proxy
                = new ViewEpisodeActivityProxy(activity,
                                               episode,
                                               fragmentManager,
                                               optionsMenuManager,
                                               playbackEventReceiver,
                                               presenter,
                                               connectionManager);

        optionsMenuManager.setFacadeProvider(connectionManager);

        playbackEventReceiver.setListener(optionsMenuManager);

        return proxy;
    }
}
