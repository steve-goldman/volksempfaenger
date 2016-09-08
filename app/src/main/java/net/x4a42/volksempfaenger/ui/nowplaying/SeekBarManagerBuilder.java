package net.x4a42.volksempfaenger.ui.nowplaying;

import android.content.Context;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.RepeatingIntervalTimer;
import net.x4a42.volksempfaenger.RepeatingIntervalTimerBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;

 class SeekBarManagerBuilder
{
    public SeekBarManager build(Context context, PlaybackServiceConnectionManager connectionManager)
    {
        int delayMillis = context.getResources().getInteger(R.integer.seekbar_delay_millis);

        RepeatingIntervalTimer repeatingIntervalTimer
                = new RepeatingIntervalTimerBuilder().build(delayMillis);

        PlaybackServiceIntentProvider intentProvider
                = new PlaybackServiceIntentProviderBuilder().build(context);

        SeekBarManager seekBarManager
                = new SeekBarManager(repeatingIntervalTimer,
                                     intentProvider)
                .setFacadeProvider(connectionManager);

        repeatingIntervalTimer.setRunnable(seekBarManager);

        return seekBarManager;
    }
}
