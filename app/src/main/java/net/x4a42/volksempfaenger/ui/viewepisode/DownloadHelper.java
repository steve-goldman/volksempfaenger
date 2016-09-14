package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.EnclosureCursor;
import net.x4a42.volksempfaenger.data.EpisodeCursor;
import net.x4a42.volksempfaenger.data.enclosure.EnclosureDataHelper;
import net.x4a42.volksempfaenger.data.episode.EpisodeDataHelper;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

class DownloadHelper implements DialogInterface.OnClickListener
{
    private final Context                       context;
    private final Uri                           episodeUri;
    private final EpisodeDataHelper episodeDataHelper;
    private final EnclosureDataHelper           enclosureDataHelper;
    private final Preferences                   preferences;
    private final ConnectivityStatus            connectivityStatus;
    private final DownloadServiceIntentProvider intentProvider;
    private final AlertDialogBuilderFactory     dialogBuilderFactory;
    private EpisodeCursor                       episodeCursor;

    public DownloadHelper(Context                       context,
                          Uri                           episodeUri,
                          EpisodeDataHelper             episodeDataHelper,
                          EnclosureDataHelper           enclosureDataHelper,
                          Preferences                   preferences,
                          ConnectivityStatus            connectivityStatus,
                          DownloadServiceIntentProvider intentProvider,
                          AlertDialogBuilderFactory     dialogBuilderFactory)
    {
        this.context              = context;
        this.episodeUri           = episodeUri;
        this.episodeDataHelper    = episodeDataHelper;
        this.enclosureDataHelper  = enclosureDataHelper;
        this.preferences          = preferences;
        this.connectivityStatus   = connectivityStatus;
        this.intentProvider       = intentProvider;
        this.dialogBuilderFactory = dialogBuilderFactory;
    }

    public void download(EpisodeCursor episodeCursor)
    {
        this.episodeCursor = episodeCursor;

        // set the enclosure id for the download service
        setEnclosureId();

        if (preferences.downloadWifiOnly() && !connectivityStatus.isWifiConnected())
        {
            showWarning();
            return;
        }

        startDownloadService();
    }

    private void startDownloadService()
    {
        context.startService(intentProvider.getDownloadIntent(
                new long[]{episodeCursor.getId()}, true));
    }

    private void setEnclosureId()
    {
        if (episodeCursor.getEnclosureId() != 0)
        {
            return;
        }

        EnclosureCursor enclosureCursor = enclosureDataHelper.getForEpisode(episodeCursor.getId());
        episodeDataHelper.setEnclosure(episodeUri, enclosureCursor.getId());
    }

    private void showWarning()
    {
        AlertDialog.Builder builder = dialogBuilderFactory.create();
        builder.setMessage(R.string.dialog_download_mobile);
        builder.setTitle(R.string.menu_download);
        builder.setPositiveButton(R.string.ok, this);
        builder.setNegativeButton(R.string.cancel, this);
        builder.create();
        builder.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        switch (which)
        {
            case DialogInterface.BUTTON_POSITIVE:
                startDownloadService();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                // no-op
                break;
        }
    }
}
