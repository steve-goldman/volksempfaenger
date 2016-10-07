package net.x4a42.volksempfaenger.receiver.connectivitychange;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        new ConnectivityChangeReceiverProxyBuilder().build().onReceive(intent);
    }
}
