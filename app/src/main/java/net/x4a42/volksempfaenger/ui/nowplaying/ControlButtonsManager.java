package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEvent;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventListener;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacade;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;

class ControlButtonsManager implements PlaybackEventListener,
                                       ImageButton.OnClickListener,
                                       PlaybackServiceConnectionManager.Listener
{
    private final PlaybackEventReceiver            playbackEventReceiver;
    private final PlaybackServiceIntentProvider    intentProvider;
    private final int                              offset;
    private PlaybackServiceFacadeProvider          facadeProvider;
    private LinearLayout                           controlsLayout;
    private ImageButton                            rewindButton;
    private ImageButton                            playPauseButton;
    private ImageButton                            fastForwardButton;
    private ImageButton                            nextButton;
    private ImageButton                            skipButton;

    public ControlButtonsManager(PlaybackEventReceiver            playbackEventReceiver,
                                 PlaybackServiceIntentProvider    intentProvider,
                                 int                              offset)
    {
        this.playbackEventReceiver = playbackEventReceiver;
        this.intentProvider        = intentProvider;
        this.offset                = offset;
    }

    public ControlButtonsManager setFacadeProvider(PlaybackServiceFacadeProvider facadeProvider)
    {
        this.facadeProvider = facadeProvider;
        return this;
    }

    public void onCreateView(View view)
    {
        controlsLayout    = (LinearLayout) view.findViewById(R.id.controls);
        rewindButton      = (ImageButton)  view.findViewById(R.id.back);
        playPauseButton   = (ImageButton)  view.findViewById(R.id.pause);
        fastForwardButton = (ImageButton)  view.findViewById(R.id.forward);
        nextButton        = (ImageButton)  view.findViewById(R.id.next);
        skipButton        = (ImageButton)  view.findViewById(R.id.skip);

        rewindButton.setOnClickListener(this);
        playPauseButton.setOnClickListener(this);
        fastForwardButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

        setNotPlaying();
    }

    public void hide()
    {
        controlsLayout.setVisibility(View.GONE);
    }

    public void show()
    {
        controlsLayout.setVisibility(View.VISIBLE);
    }

    public void onResume()
    {
        playbackEventReceiver.subscribe();
        PlaybackServiceFacade facade = facadeProvider.getFacade();
        if (facade != null && facade.isPlaying())
        {
            setPlaying();
        }
        else
        {
            setNotPlaying();
        }
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
        switch (playbackEvent)
        {
            case PLAYING:
                setPlaying();
                break;
            case PAUSED:
            case ENDED:
                setNotPlaying();
                break;
        }
    }

    //
    // ImageButton.OnClickListener
    //

    @Override
    public void onClick(View v)
    {
        if (rewindButton.equals(v))
        {
            handleRewindClicked(v.getContext());
        }
        else if (playPauseButton.equals(v))
        {
            handlePlayPauseClicked(v.getContext());
        }
        else if (fastForwardButton.equals(v))
        {
            handleFastForwardClicked(v.getContext());
        }
        else if (nextButton.equals(v))
        {
            handleNext(v.getContext());
        }
        else if (skipButton.equals(v))
        {
            handleSkip(v.getContext());
        }
    }

    //
    // PlaybackServiceConnectionManager.Listener
    //

    @Override
    public void onPlaybackServiceConnected()
    {
        if (facadeProvider.getFacade().isPlaying())
        {
            setPlaying();
        }
    }

    @Override
    public void onPlaybackServiceDisconnected()
    {
        setNotPlaying();
    }

    //
    // helper methods
    //

    private void setPlaying()
    {
        playPauseButton.setImageResource(R.drawable.ic_media_pause);
    }

    private void setNotPlaying()
    {
        playPauseButton.setImageResource(R.drawable.ic_media_play);
    }

    private void handleRewindClicked(Context context)
    {
        context.startService(intentProvider.getMoveIntent(-offset));
    }

    private void handlePlayPauseClicked(Context context)
    {
        context.startService(intentProvider.getPlayPauseIntent());
    }

    private void handleFastForwardClicked(Context context)
    {
        context.startService(intentProvider.getMoveIntent(offset));
    }

    private void handleNext(Context context)
    {
        context.startService(intentProvider.getNextIntent());
    }

    private void handleSkip(Context context)
    {
        context.startService(intentProvider.getSkipIntent());
    }

}
