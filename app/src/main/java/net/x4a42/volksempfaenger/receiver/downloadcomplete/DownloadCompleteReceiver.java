package net.x4a42.volksempfaenger.receiver.downloadcomplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadCompleteReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        new DownloadCompleteReceiverProxyBuilder().build(context).onReceive(intent);
    }
}
