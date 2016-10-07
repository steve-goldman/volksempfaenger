package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.data.entity.podcast.Podcast;
import net.x4a42.volksempfaenger.data.entity.podcast.PodcastPathProvider;
import net.x4a42.volksempfaenger.event.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.ui.main.MainActivityIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;

@RunWith(MockitoJUnitRunner.class)
public class InfoSectionManagerTest
{
    @Mock Context                       context;
    @Mock PlaybackEventReceiver         playbackEventReceiver;
    @Mock PlaybackServiceFacadeProvider facadeProvider;
    @Mock PlaybackServiceFacade         facade;
    @Mock Episode                       episode;
    @Mock Podcast                       podcast;
    @Mock MainActivityIntentProvider    mainIntentProvider;
    @Mock Intent                        mainIntent;
    @Mock PodcastPathProvider           podcastPathProvider;
    @Mock View                          view;
    @Mock LinearLayout                  infoLayout;
    @Mock ImageView                     podcastLogo;
    @Mock TextView                      episodeText;
    @Mock TextView                      podcastText;
    @Mock ImageLoader                   imageLoader;
    @Mock File                          logoFile;
    String                              title                     = "my-episode-title";
    String                              podcastTitle              = "my-podcast-title";
    String                              url                       = "my-url";
    URI                                 uri                       = URI.create(url);
    InfoSectionManager                  infoSectionManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(facade.getEpisode()).thenReturn(episode);
        Mockito.when(episode.getPodcast()).thenReturn(podcast);
        Mockito.when(episode.getTitle()).thenReturn(title);
        Mockito.when(podcast.getTitle()).thenReturn(podcastTitle);
        Mockito.when(mainIntentProvider.getIntent()).thenReturn(mainIntent);
        Mockito.when(view.findViewById(R.id.info)).thenReturn(infoLayout);
        Mockito.when(view.findViewById(R.id.logo)).thenReturn(podcastLogo);
        Mockito.when(view.findViewById(R.id.episode)).thenReturn(episodeText);
        Mockito.when(view.findViewById(R.id.podcast)).thenReturn(podcastText);
        Mockito.when(infoLayout.getContext()).thenReturn(context);
        Mockito.when(podcastPathProvider.getLogoUrl(podcast)).thenReturn(url);
        Mockito.when(logoFile.exists()).thenReturn(true);
        Mockito.when(logoFile.toURI()).thenReturn(uri);

        infoSectionManager = new InfoSectionManager(playbackEventReceiver,
                                                    mainIntentProvider,
                                                    podcastPathProvider,
                                                    imageLoader)
                .setFacadeProvider(facadeProvider);
    }

    @Test
    public void onCreateView() throws Exception
    {
        infoSectionManager.onCreateView(view);

        Mockito.verify(infoLayout).setOnClickListener(infoSectionManager);
        Mockito.verify(episodeText).setText("");
        Mockito.verify(podcastText).setText("");
    }

    @Test
    public void hide() throws Exception
    {
        infoSectionManager.onCreateView(view);
        infoSectionManager.hide();

        Mockito.verify(infoLayout).setVisibility(View.GONE);
    }

    @Test
    public void show() throws Exception
    {
        infoSectionManager.onCreateView(view);
        infoSectionManager.show();

        Mockito.verify(infoLayout).setVisibility(View.VISIBLE);
    }

    @Test
    public void onResume() throws Exception
    {
        infoSectionManager.onCreateView(view);
        infoSectionManager.onResume();

        Mockito.verify(playbackEventReceiver).subscribe();
    }

    @Test
    public void onPause() throws Exception
    {
        infoSectionManager.onPause();

        Mockito.verify(playbackEventReceiver).unsubscribe();
    }

    @Test
    public void updatePlaying() throws Exception
    {
        Mockito.when(facade.isOpen()).thenReturn(true);
        Mockito.when(facade.isPlaying()).thenReturn(true);

        infoSectionManager.onCreateView(view);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        infoSectionManager.onPlaybackServiceConnected();

        Mockito.verify(episodeText).setText(title);
        Mockito.verify(podcastText).setText(podcastTitle);
        Mockito.verify(imageLoader).displayImage(url, podcastLogo);
    }

    @Test
    public void updateNotPlaying() throws Exception
    {
        Mockito.when(facade.isOpen()).thenReturn(true);
        Mockito.when(facade.isPlaying()).thenReturn(false);

        infoSectionManager.onCreateView(view);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        infoSectionManager.onPlaybackServiceConnected();

        Mockito.verify(episodeText).setText(title);
        Mockito.verify(podcastText).setText(podcastTitle);
        Mockito.verify(imageLoader).displayImage(url, podcastLogo);
    }

    @Test
    public void onServiceDisconnected() throws Exception
    {
        infoSectionManager.onCreateView(view);
        infoSectionManager.onPlaybackServiceDisconnected();
    }

    @Test
    public void onClickedInfo() throws Exception
    {
        infoSectionManager.onCreateView(view);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        infoSectionManager.onClick(infoLayout);

        Mockito.verify(context).startActivity(mainIntent);
    }

    @Test
    public void onPlaybackEvent() throws Exception
    {
        // covered by other tests
        infoSectionManager.onCreateView(view);
        infoSectionManager.onPlaybackEvent(PlaybackEvent.PLAYING);
    }
}
