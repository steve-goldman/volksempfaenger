package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
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
import net.x4a42.volksempfaenger.event.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.ui.main.MainActivityIntentProvider;

class InfoSectionManager implements PlaybackEventListener,
                                    View.OnClickListener,
                                    PlaybackServiceConnectionManager.Listener
{
    private final PlaybackEventReceiver      playbackEventReceiver;
    private final MainActivityIntentProvider mainIntentProvider;
    private final PodcastPathProvider        podcastPathProvider;
    private final ImageLoader                imageLoader;
    private PlaybackServiceFacadeProvider    facadeProvider;
    private LinearLayout                     infoLayout;
    private ImageView                        podcastLogo;
    private TextView                         episodeText;
    private TextView                         podcastText;

    public InfoSectionManager(PlaybackEventReceiver      playbackEventReceiver,
                              MainActivityIntentProvider mainIntentProvider,
                              PodcastPathProvider        podcastPathProvider,
                              ImageLoader                imageLoader)
    {
        this.playbackEventReceiver = playbackEventReceiver;
        this.mainIntentProvider    = mainIntentProvider;
        this.podcastPathProvider   = podcastPathProvider;
        this.imageLoader           = imageLoader;
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

        infoLayout.setOnClickListener(this);

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
        context.startActivity(mainIntentProvider.getIntent());
    }

    private void update()
    {
        PlaybackServiceFacade facade  = facadeProvider.getFacade();
        if (facade == null)
        {
            updateEmpty();
            return;
        }

        Episode episode = facade.getEpisode();
        if (episode == null)
        {
            updateEmpty();
            return;
        }

        Podcast podcast = episode.getPodcast();

        episodeText.setText(episode.getTitle());
        podcastText.setText(podcast.getTitle());

        String url = podcastPathProvider.getLogoUrl(podcast);
        podcastLogo.setImageResource(android.R.color.transparent);
        imageLoader.displayImage(url, podcastLogo);
    }

    private void updateEmpty()
    {
        episodeText.setText("");
        podcastText.setText("");
    }
}
