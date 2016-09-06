package net.x4a42.volksempfaenger.service.playback;

import android.media.AudioManager;

import net.x4a42.volksempfaenger.Log;

public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener
{
    public interface Listener
    {
        void onAudioFocusGained();
        void onAudioFocusLost();
        void onAudioFocusLostTransiently();
        void onAudioFocusLostTransientlyCanDuck();
    }

    private final AudioManager audioManager;
    private Listener           listener;
    private boolean            hasFocus;

    public AudioFocusManager(AudioManager audioManager)
    {
        this.audioManager = audioManager;
    }

    public AudioFocusManager setListener(Listener listener)
    {
        this.listener = listener;
        return this;
    }

    public void requestFocus()
    {
        if (hasFocus)
        {
            return;
        }

        if (audioManager.requestAudioFocus(this,
                                           AudioManager.STREAM_MUSIC,
                                           AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
        {
            Log.d(this, "could not get audio focus");
            return;
        }

        hasFocus = true;
    }

    public void abandonFocus()
    {
        if (!hasFocus)
        {
            return;
        }

        audioManager.abandonAudioFocus(this);

        hasFocus = false;
    }

    //
    // AudioManager.OnAudioFocusChangeListener
    //

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        switch (focusChange)
        {
            case AudioManager.AUDIOFOCUS_GAIN:
                handleAudioFocusGain();
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                handleAudioFocusLoss();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                handleAudioFocusLossTransient();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                handleAudioFocusLossTransientCanDuck();
                break;

            default:
                Log.d(this, String.format("Unexpected focusChangeCode:%d", focusChange));
                break;
        }
    }

    private void handleAudioFocusGain()
    {
        listener.onAudioFocusGained();
    }

    private void handleAudioFocusLoss()
    {
        listener.onAudioFocusLost();
    }

    private void handleAudioFocusLossTransient()
    {
        listener.onAudioFocusLostTransiently();
    }

    private void handleAudioFocusLossTransientCanDuck()
    {
        listener.onAudioFocusLostTransientlyCanDuck();
    }

}
