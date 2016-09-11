package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.service.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;

class ControlButtonsManagerBuilder
{
    public ControlButtonsManager build(Context context, PlaybackServiceConnectionManager connectionManager)
    {
        PlaybackEventReceiver playbackEventReceiver
                = new PlaybackEventReceiverBuilder().build();

        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        int offset = context.getResources().getInteger(R.integer.move_offset);

        ControlButtonsManager controlButtonsManager
                = new ControlButtonsManager(playbackEventReceiver,
                                            intentProvider,
                                            offset)
                .setFacadeProvider(connectionManager);

        playbackEventReceiver.setListener(controlButtonsManager);

        return controlButtonsManager;
    }
}
