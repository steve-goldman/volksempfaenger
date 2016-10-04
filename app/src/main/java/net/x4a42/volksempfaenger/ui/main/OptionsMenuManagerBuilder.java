package net.x4a42.volksempfaenger.ui.main;

import android.app.Activity;

import net.x4a42.volksempfaenger.NavUtilsWrapper;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProviderBuilder;

class OptionsMenuManagerBuilder
{
    public OptionsMenuManager build(Activity activity, PlaybackServiceFacadeProvider facadeProvider)
    {
        NavUtilsWrapper navUtilsWrapper = new NavUtilsWrapper(activity);

        AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider
                = new AddSubscriptionActivityIntentProviderBuilder().build(activity);

        PlaybackServiceIntentProvider playbackIntentProvider
                = new PlaybackServiceIntentProviderBuilder().build(activity);

        return new OptionsMenuManager(activity,
                                      navUtilsWrapper,
                                      addSubscriptionIntentProvider,
                                      playbackIntentProvider,
                                      facadeProvider);
    }
}
