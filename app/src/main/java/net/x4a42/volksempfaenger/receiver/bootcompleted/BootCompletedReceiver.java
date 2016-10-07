package net.x4a42.volksempfaenger.receiver.bootcompleted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        new BootCompletedReceiverProxyBuilder().build(context).onReceive();
    }
}
