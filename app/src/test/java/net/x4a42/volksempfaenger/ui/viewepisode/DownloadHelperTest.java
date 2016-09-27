package net.x4a42.volksempfaenger.ui.viewepisode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import net.x4a42.volksempfaenger.Preferences;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.entity.episode.Episode;
import net.x4a42.volksempfaenger.misc.AlertDialogBuilderFactory;
import net.x4a42.volksempfaenger.misc.ConnectivityStatus;
import net.x4a42.volksempfaenger.service.download.DownloadServiceIntentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DownloadHelperTest
{
    @Mock Context                       context;
    @Mock Episode                       episode;
    @Mock Preferences                   preferences;
    @Mock ConnectivityStatus            connectivityStatus;
    @Mock DownloadServiceIntentProvider intentProvider;
    @Mock Intent                        intent;
    @Mock AlertDialogBuilderFactory     dialogBuilderFactory;
    @Mock AlertDialog.Builder           dialogBuilder;
    long                                episodeId            = 10;
    DownloadHelper                      downloadHelper;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(episode.get_id()).thenReturn(episodeId);
        Mockito.when(intentProvider.getDownloadIntent(Mockito.eq(new long[]{episodeId}),
                                                      Mockito.eq(true)))
               .thenReturn(intent);
        Mockito.when(dialogBuilderFactory.create()).thenReturn(dialogBuilder);

        downloadHelper = new DownloadHelper(context,
                                            episode,
                                            preferences,
                                            connectivityStatus,
                                            intentProvider,
                                            dialogBuilderFactory);
    }

    @Test
    public void downloadWifiNotPreferred()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.downloadWifiOnly()).thenReturn(false);

        downloadHelper.download();

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void downloadWifiPreferredNotConnected()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.downloadWifiOnly()).thenReturn(true);

        downloadHelper.download();

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
        Mockito.when(preferences.downloadWifiOnly()).thenReturn(true);

        downloadHelper.download();

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void onClickPositive()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.downloadWifiOnly()).thenReturn(true);

        downloadHelper.download();
        downloadHelper.onClick(Mockito.mock(DialogInterface.class), DialogInterface.BUTTON_POSITIVE);

        Mockito.verify(context).startService(intent);
    }

    @Test
    public void onClickNegative()
    {
        Mockito.when(connectivityStatus.isWifiConnected()).thenReturn(false);
        Mockito.when(preferences.downloadWifiOnly()).thenReturn(true);

        downloadHelper.download();
        downloadHelper.onClick(Mockito.mock(DialogInterface.class), DialogInterface.BUTTON_NEGATIVE);

        Mockito.verify(context, Mockito.never()).startService(intent);

    }
}
