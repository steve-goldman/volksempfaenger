package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.x4a42.volksempfaenger.FileBuilder;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.ui.viewepisode.ViewEpisodeActivityIntentProvider;

import java.io.File;

class InfoSectionManager implements PlaybackEventListener,
                                           View.OnClickListener,
                                           PlaybackServiceConnectionManager.Listener
{
    private final PlaybackEventReceiver             playbackEventReceiver;
    private final PlaybackServiceIntentProvider     playbackIntentProvider;
    private final ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider;
    private final ImageLoader                       imageLoader;
    private final FileBuilder                       fileBuilder;
    private PlaybackServiceFacadeProvider           facadeProvider;
    private LinearLayout                            infoLayout;
    private ImageView                               podcastLogo;
    private TextView                                episodeText;
    private TextView                                podcastText;
    private ImageButton                             playPauseButton;

    public InfoSectionManager(PlaybackEventReceiver             playbackEventReceiver,
                              PlaybackServiceIntentProvider     playbackIntentProvider,
                              ViewEpisodeActivityIntentProvider viewEpisodeIntentProvider,
                              ImageLoader                       imageLoader,
                              FileBuilder                       fileBuilder)
    {
        this.fileBuilder               = fileBuilder;
        this.playbackEventReceiver     = playbackEventReceiver;
        this.playbackIntentProvider    = playbackIntentProvider;
        this.viewEpisodeIntentProvider = viewEpisodeIntentProvider;
        this.imageLoader               = imageLoader;
    }

    public InfoSectionManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public void onCreateView(View view)
    {
        infoLayout      = (LinearLayout) view.findViewById(R.id.info);
        podcastLogo     = (ImageView)    view.findViewById(R.id.logo);
        episodeText     = (TextView)     view.findViewById(R.id.episode);
        podcastText     = (TextView)     view.findViewById(R.id.podcast);
        playPauseButton = (ImageButton)  view.findViewById(R.id.info_pause);

        infoLayout.setOnClickListener(this);
        playPauseButton.setOnClickListener(this);

        update();
    }

    public void hide()
    {
        infoLayout.setVisibility(View.GONE);
    }

    public void show()
    {
        infoLayout.setVisibility(View.VISIBLE);
    }

    public void onResume()
    {
        playbackEventReceiver.subscribe();
        update();
    }

    public void onPause()
    {
        playbackEventReceiver.unsubscribe();
    }

    //
    // PlaybackEventListener
    //

    @Override
    public void onPlaybackEvent(PlaybackEvent playbackEvent)
    {
        update();
    }

    //
    // View.OnClickListener
    //

    @Override
    public void onClick(View v)
    {
        if (infoLayout.equals(v))
        {
            handleInfoClicked(v.getContext());
        }
        else if (playPauseButton.equals(v))
        {
            handlePlayPauseClicked(v.getContext());
        }
    }

    //
    // PlaybackServiceConnectionManager.Listener
    //

    @Override
    public void onPlaybackServiceConnected()
    {
        update();
    }

    @Override
    public void onPlaybackServiceDisconnected()
    {
        update();
    }

    //
    // helper methods
    //

    private void handleInfoClicked(Context context)
    {
        context.startActivity(viewEpisodeIntentProvider.getIntent(
                facadeProvider.getFacade().getEpisodeUri()));
    }

    private void handlePlayPauseClicked(Context context)
    {
        context.startService(playbackIntentProvider.getPlayPauseIntent());
    }

    private void update()
    {
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        if (facade == null || !facade.isOpen())
        {
            episodeText.setText("");
            podcastText.setText("");
            return;
        }

        episodeText.setText(facade.getTitle());
        podcastText.setText(facade.getPodcastTitle());
        playPauseButton.setImageResource(
                facade.isPlaying() ?
                        R.drawable.ic_media_pause : R.drawable.ic_media_play);

        File   logoFile = fileBuilder.build(String.valueOf(facade.getPodcastId()));
        String url      = logoFile.exists() ? logoFile.toURI().toString() : null;

        imageLoader.displayImage(url, podcastLogo);
    }

}
