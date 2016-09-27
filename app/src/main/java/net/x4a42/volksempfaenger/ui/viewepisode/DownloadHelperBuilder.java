package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.misc.ConnectivityStatusBuilder;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

class DownloadHelperBuilder
{
    public DownloadHelper build(Context context,
                                Episode episode)
    {
        Preferences preferences
                = new PreferencesBuilder().build(context);

        ConnectivityStatus connectivityStatus
                = new ConnectivityStatusBuilder().build(context);

        DownloadServiceIntentProvider intentProvider
                = new DownloadServiceIntentProvider(context, new IntentBuilder());

        AlertDialogBuilderFactory dialogBuilderFactory
                = new AlertDialogBuilderFactory(context);

        return new DownloadHelper(context,
                                  episode,
                                  preferences,
                                  connectivityStatus,
                                  intentProvider,
                                  dialogBuilderFactory);
    }
}
