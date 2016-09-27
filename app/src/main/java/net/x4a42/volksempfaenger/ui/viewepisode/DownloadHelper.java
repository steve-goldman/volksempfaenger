package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

class DownloadHelper implements DialogInterface.OnClickListener
{
    private final Context                       context;
    private final Episode                       episode;
    private final Preferences                   preferences;
    private final ConnectivityStatus            connectivityStatus;
    private final DownloadServiceIntentProvider intentProvider;
    private final AlertDialogBuilderFactory     dialogBuilderFactory;

    public DownloadHelper(Context                       context,
                          Episode                       episode,
                          Preferences                   preferences,
                          ConnectivityStatus            connectivityStatus,
                          DownloadServiceIntentProvider intentProvider,
                          AlertDialogBuilderFactory     dialogBuilderFactory)
    {
        this.context              = context;
        this.episode              = episode;
        this.preferences          = preferences;
        this.connectivityStatus   = connectivityStatus;
        this.intentProvider       = intentProvider;
        this.dialogBuilderFactory = dialogBuilderFactory;
    }

    public void download()
    {
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
                new long[]{episode.get_id()}, true));
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
