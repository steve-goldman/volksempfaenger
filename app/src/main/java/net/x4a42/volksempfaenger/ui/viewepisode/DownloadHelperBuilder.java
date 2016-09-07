package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.data.enclosure.EnclosureDataHelper;
import net.x4a42.volksempfaenger.data.enclosure.EnclosureDataHelperBuilder;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

public class DownloadHelperBuilder
{
    public DownloadHelper build(Context context,
                                Uri     episodeUri)
    {
        EpisodeDataHelper episodeDataHelper
                = new EpisodeDataHelperBuilder().build(context);

        EnclosureDataHelper enclosureDataHelper
                = new EnclosureDataHelperBuilder().build(context);

        Preferences preferences
                = new Preferences(context, PreferenceManager.getDefaultSharedPreferences(context));

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityStatus connectivityStatus = new ConnectivityStatus(connectivityManager);

        DownloadServiceIntentProvider intentProvider
                = new DownloadServiceIntentProvider(context, new IntentBuilder());

        AlertDialogBuilderFactory dialogBuilderFactory
                = new AlertDialogBuilderFactory(context);

        return new DownloadHelper(context,
                                  episodeUri,
                                  episodeDataHelper,
                                  enclosureDataHelper,
                                  preferences,
                                  connectivityStatus,
                                  intentProvider,
                                  dialogBuilderFactory);
    }
}
