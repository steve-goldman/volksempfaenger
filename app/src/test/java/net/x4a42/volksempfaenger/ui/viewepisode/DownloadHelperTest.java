package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DownloadHelperTest
{
    Context                       context              = Mockito.mock(Context.class);
    Uri                           episodeUri           = Mockito.mock(Uri.class);
    long                          episodeId            = 10;
    EpisodeDataHelper             episodeDataHelper    = Mockito.mock(EpisodeDataHelper.class);
    EnclosureCursor               enclosureCursor      = Mockito.mock(EnclosureCursor.class);
    long                          enclosureId          = 20;
    EnclosureDataHelper           enclosureDataHelper  = Mockito.mock(EnclosureDataHelper.class);
    Preferences                   preferences          = Mockito.mock(Preferences.class);
    ConnectivityStatus            connectivityStatus   = Mockito.mock(ConnectivityStatus.class);
    DownloadServiceIntentProvider intentProvider       = Mockito.mock(DownloadServiceIntentProvider.class);
    Intent                        intent               = Mockito.mock(Intent.class);
    AlertDialogBuilderFactory     dialogBuilderFactory = Mockito.mock(AlertDialogBuilderFactory.class);
    AlertDialog.Builder           dialogBuilder        = Mockito.mock(AlertDialog.Builder.class);
    EpisodeCursor                 episodeCursor        = Mockito.mock(EpisodeCursor.class);
    DownloadHelper                downloadHelper;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(episodeCursor.getId()).thenReturn(episodeId);
        Mockito.when(enclosureDataHelper.getForEpisode(episodeId)).thenReturn(enclosureCursor);
        Mockito.when(enclosureCursor.getId()).thenReturn(enclosureId);
        Mockito.when(intentProvider.getDownloadIntent(Mockito.eq(new long[]{episodeId}),
                                                      Mockito.eq(true)))
               .thenReturn(intent);
        Mockito.when(dialogBuilderFactory.create()).thenReturn(dialogBuilder);

        downloadHelper = new DownloadHelper(context,
                                            episodeUri,
                                            episodeDataHelper,
                                            enclosureDataHelper,
                                            preferences,
                                            connectivityStatus,
                                            intentProvider,
                                            dialogBuilderFactory);
    }

    @Test
    public void downloadSetsEnclosureId() throws Exception
    {
        downloadHelper.download(episodeCursor);

        Mockito.verify(episodeDataHelper).setEnclosure(episodeUri, enclosureId);
    }

    @Test
    public void downloadWifiNotPreferred()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.isDownloadWifi()).thenReturn(false);

        downloadHelper.download(episodeCursor);

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void downloadWifiPreferredNotConnected()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.isDownloadWifi()).thenReturn(true);

        downloadHelper.download(episodeCursor);

        Mockito.verify(context, Mockito.never()).startService(intent);
        Mockito.verify(dialogBuilder).setMessage(R.string.dialog_download_mobile);
        Mockito.verify(dialogBuilder).setTitle(R.string.menu_download);
        Mockito.verify(dialogBuilder).setPositiveButton(R.string.ok, downloadHelper);
        Mockito.verify(dialogBuilder).setNegativeButton(R.string.cancel, downloadHelper);
        Mockito.verify(dialogBuilder).create();
        Mockito.verify(dialogBuilder).show();
    }

    @Test
    public void downloadWifiPreferredConnected()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(true);
        Mockito.when(preferences.isDownloadWifi()).thenReturn(true);

        downloadHelper.download(episodeCursor);

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void onClickPositive()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.isDownloadWifi()).thenReturn(true);

        downloadHelper.download(episodeCursor);
        downloadHelper.onClick(Mockito.mock(DialogInterface.class), DialogInterface.BUTTON_POSITIVE);

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void onClickNegative()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.isDownloadWifi()).thenReturn(true);

        downloadHelper.download(episodeCursor);
        downloadHelper.onClick(Mockito.mock(DialogInterface.class), DialogInterface.BUTTON_NEGATIVE);

        Mockito.verify(context, Mockito.never()).startService(intent);

    }
}