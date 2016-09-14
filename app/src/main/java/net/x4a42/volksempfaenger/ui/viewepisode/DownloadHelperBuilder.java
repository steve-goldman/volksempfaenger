package net.x4a42.volksempfaenger.ui.viewepisode;

import android.content.Context;
import android.net.Uri;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.PreferencesBuilder;
import net.x4a42.volksempfaenger.data.enclosure.EnclosureDataHelper;
import net.x4a42.volksempfaenger.data.enclosure.EnclosureDataHelperBuilder;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelperBuilder;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.misc.ConnectivityStatusBuilder;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

class DownloadHelperBuilder
{
    public DownloadHelper build(Context context,
                                Uri     episodeUri)
    {
        EpisodeDataHelper episodeDataHelper
                = new EpisodeDataHelperBuilder().build(context);

        EnclosureDataHelper enclosureDataHelper
                = new EnclosureDataHelperBuilder().build(context);

        Preferences preferences
                = new PreferencesBuilder().build(context);

        ConnectivityStatus connectivityStatus
                = new ConnectivityStatusBuilder().build(context);

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
