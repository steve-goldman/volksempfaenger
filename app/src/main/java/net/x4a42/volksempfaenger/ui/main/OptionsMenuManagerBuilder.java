package net.x4a42.volksempfaenger.ui.main;

import android.app.Activity;

import net.x4a42.volksempfaenger.service.playback.PlaybackServiceFacadeProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProvider;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceIntentProviderBuilder;
import net.x4a42.volksempfaenger.service.syncall.SyncAllServiceIntentProvider;
import net.x4a42.volksempfaenger.service.syncall.SyncAllServiceIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.addsubscription.AddSubscriptionActivityIntentProviderBuilder;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProvider;
import net.x4a42.volksempfaenger.ui.settings.SettingsActivityIntentProviderBuilder;

class OptionsMenuManagerBuilder
{
    public OptionsMenuManager build(Activity activity, PlaybackServiceFacadeProvider facadeProvider)
    {
        AddSubscriptionActivityIntentProvider addSubscriptionIntentProvider
                = new AddSubscriptionActivityIntentProviderBuilder().build(activity);

        SyncAllServiceIntentProvider syncAllIntentProvider
                = new SyncAllServiceIntentProviderBuilder().build(activity);

        PlaybackServiceIntentProvider playbackIntentProvider
                = new PlaybackServiceIntentProviderBuilder().build(activity);

        SettingsActivityIntentProvider settingsIntentProvider
                = new SettingsActivityIntentProviderBuilder().build(activity);

        return new OptionsMenuManager(activity,
                                      addSubscriptionIntentProvider,
                                      syncAllIntentProvider,
                                      playbackIntentProvider,
                                      settingsIntentProvider,
                                      facadeProvider);
    }
}
