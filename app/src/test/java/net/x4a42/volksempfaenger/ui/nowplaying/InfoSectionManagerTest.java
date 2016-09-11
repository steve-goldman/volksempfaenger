package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.FileBuilder;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.net.URI;

public class InfoSectionManagerTest
{
    Context                           context                   = Mockito.mock(Context.class);
    PlaybackEventReceiver             playbackEventReceiver     = Mockito.mock(PlaybackEventReceiver.class);
    PlaybackServiceFacadeProvider     facadeProvider            = Mockito.mock(PlaybackServiceFacadeProvider.class);
    PlaybackServiceFacade             facade                    = Mockito.mock(PlaybackServiceFacade.class);
    Uri                               episodeUri                = Mockito.mock(Uri.class);
    long                              podcastId                 = 199;
    String                            title                     = "my-episode-title";
    String                            podcastTitle              = "my-podcast-title";
    PlaybackServiceIntentProvider     playbackIntentProvider    = Mockito.mock(PlaybackServiceIntentProvider.class);
    Intent                            playPauseIntent           = Mockito.mock(Intent.class);
    ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider = Mockito.mock(ViewEpisodeActivityIntentProvider.class);
    Intent                            viewEpisodeIntent         = Mockito.mock(Intent.class);
    View                              view                      = Mockito.mock(View.class);
    LinearLayout                      infoLayout                = Mockito.mock(LinearLayout.class);
    ImageView                         podcastLogo               = Mockito.mock(ImageView.class);
    TextView                          episodeText               = Mockito.mock(TextView.class);
    TextView                          podcastText               = Mockito.mock(TextView.class);
    ImageButton                       playPauseButton           = Mockito.mock(ImageButton.class);
    ImageLoader                       imageLoader               = Mockito.mock(ImageLoader.class);
    FileBuilder                       fileBuilder               = Mockito.mock(FileBuilder.class);
    File                              logoFile                  = Mockito.mock(File.class);
    String                            url                       = "my-url";
    URI                               uri                       = URI.create(url);
    InfoSectionManager                infoSectionManager;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(facade.getPodcastId()).thenReturn(podcastId);
        Mockito.when(facade.getTitle()).thenReturn(title);
        Mockito.when(facade.getPodcastTitle()).thenReturn(podcastTitle);
        Mockito.when(facade.getEpisodeUri()).thenReturn(episodeUri);
        Mockito.when(playbackIntentProvider.getPlayPauseIntent()).thenReturn(playPauseIntent);
        Mockito.when(viewEpisodeIntentProvider.getIntent(episodeUri)).thenReturn(viewEpisodeIntent);
        Mockito.when(view.findViewById(R.id.info)).thenReturn(infoLayout);
        Mockito.when(view.findViewById(R.id.logo)).thenReturn(podcastLogo);
        Mockito.when(view.findViewById(R.id.episode)).thenReturn(episodeText);
        Mockito.when(view.findViewById(R.id.podcast)).thenReturn(podcastText);
        Mockito.when(view.findViewById(R.id.info_pause)).thenReturn(playPauseButton);
        Mockito.when(playPauseButton.getContext()).thenReturn(context);
        Mockito.when(infoLayout.getContext()).thenReturn(context);
        Mockito.when(fileBuilder.build(Mockito.anyString())).thenReturn(logoFile);
        Mockito.when(logoFile.exists()).thenReturn(true);
        Mockito.when(logoFile.toURI()).thenReturn(uri);

        infoSectionManager = new InfoSectionManager(playbackEventReceiver,
                                                    playbackIntentProvider,
                                                    viewEpisodeIntentProvider,
                                                    imageLoader,
                                                    fileBuilder)
                .setFacadeProvider(facadeProvider);
    }

    @Test
    public void onCreateView() throws Exception
    {
        infoSectionManager.onCreateView(view);

        Mockito.verify(infoLayout).setOnClickListener(infoSectionManager);
        Mockito.verify(playPauseButton).setOnClickListener(infoSectionManager);
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

        Mockito.verify(playPauseButton).setImageResource(R.drawable.ic_media_pause);
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

        Mockito.verify(playPauseButton).setImageResource(R.drawable.ic_media_play);
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
    public void onClickPlayPause() throws Exception
    {
        infoSectionManager.onCreateView(view);
        infoSectionManager.onClick(playPauseButton);

        Mockito.verify(context).startService(playPauseIntent);
    }

    @Test
    public void onClickedInfo() throws Exception
    {
        infoSectionManager.onCreateView(view);
        Mockito.when(facadeProvider.getFacade()).thenReturn(facade);
        infoSectionManager.onClick(infoLayout);

        Mockito.verify(context).startActivity(viewEpisodeIntent);
    }

    @Test
    public void onPlaybackEvent() throws Exception
    {
        // covered by other tests
        infoSectionManager.onCreateView(view);
        infoSectionManager.onPlaybackEvent(PlaybackEvent.PLAYING);
    }
}